/**
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.mff.xrg.odcs.commons.app.facade;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import cz.cuni.mff.xrg.odcs.commons.app.ScheduledJobsPriority;
import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.DbSchedule;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.DbScheduleNotification;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleNotificationRecord;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleType;

/**
 * Facade providing actions with plan.
 * 
 * @author Jan Vojt
 * @author Petyr
 */
@Transactional(readOnly = true)
class ScheduleFacadeImpl implements ScheduleFacade {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleFacadeImpl.class);

    @Autowired
    private DbSchedule scheduleDao;

    @Autowired
    private DbScheduleNotification scheduleNotificationDao;

    @Autowired(required = false)
    private AuthenticationContext authCtx;

    @Autowired
    private PipelineFacade pipelineFacade;

    /**
     * Schedule factory. Explicitly call {@link #save(cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule)} to persist created entity.
     * 
     * @return initialized Schedule
     */
    @PreAuthorize("hasRole('scheduleRule.create') and hasRole('pipeline.run')")
    @Override
    public Schedule createSchedule() {
        Schedule sch = new Schedule();
        if (authCtx != null) {
            sch.setOwner(authCtx.getUser());
            if (this.authCtx.getUser().getUserActor() != null) {
                sch.setActor(this.authCtx.getUser().getUserActor());
            }
        }
        return sch;
    }

    /**
     * Returns list of all Plans currently persisted in database.
     * 
     * @return Plans list
     * @deprecated use container with paging instead
     */
    @Deprecated
    @PostFilter("hasPermission(filterObject,'scheduleRule.read')")
    @Override
    public List<Schedule> getAllSchedules() {
        return scheduleDao.getAllSchedules();
    }

    /**
     * Fetches all {@link Schedule}s planned for given pipeline.
     * 
     * @param pipeline
     * @return
     */
    @PostFilter("hasPermission(filterObject, 'scheduleRule.read')")
    @Override
    public List<Schedule> getSchedulesFor(Pipeline pipeline) {
        return scheduleDao.getSchedulesFor(pipeline);
    }

    /**
     * Fetches all {@link Schedule}s which are activated in
     * certain time and the execution for the scheduled pipeline
     * isn't already queued or running.
     * 
     * @return
     */
    @PostFilter("hasPermission(filterObject, 'scheduleRule.read')")
    @Override
    public List<Schedule> getAllTimeBasedNotQueuedRunning() {
        return scheduleDao.getAllTimeBasedNotQueuedRunning();
    }

    /**
     * Fetches all {@link Schedule}s which are activated in
     * certain time and the execution for the scheduled pipeline
     * isn't already queued or running.
     * <br/>
     * Fetched schedules are locked for update so other backends don't execute them too
     * 
     * @return
     */
    @PostFilter("hasPermission(filterObject, 'scheduleRule.read')")
    @Override
    public List<Schedule> getAllTimeBasedNotQueuedRunningForCluster() {
        return this.scheduleDao.getAllTimeBasedNotQueuedRunningForCluster();
    }

    /**
     * Find Schedule in database by ID and return it.
     * 
     * @param id
     * @return
     */
    @PostAuthorize("hasPermission(returnObject, 'scheduleRule.read')")
    @Override
    public Schedule getSchedule(long id) {
        return scheduleDao.getInstance(id);
    }

    /**
     * Saves any modifications made to the Schedule into the database.
     * 
     * @param schedule
     */
    @Transactional
    @PreAuthorize("hasPermission(#schedule, 'scheduleRule.edit') and hasPermission(#schedule.getPipeline(), 'pipeline.run')")
    @Override
    public void save(Schedule schedule) {
        scheduleDao.save(schedule);
    }

    /**
     * Deletes Schedule from the database.
     * 
     * @param schedule
     */
    @Transactional
    @PreAuthorize("hasPermission(#schedule, 'scheduleRule.delete')")
    @Override
    public void delete(Schedule schedule) {
        scheduleDao.delete(schedule);
    }

    /**
     * Deletes notification setting for schedule.
     * 
     * @param notify
     *            notification settings to delete
     */
    @Transactional
    @Override
    public void deleteNotification(ScheduleNotificationRecord notify) {
        scheduleNotificationDao.delete(notify);
    }

    /**
     * Create execution for given schedule. Also if the schedule is runOnce then
     * disable it. Ignore enable/disable option for schedule.
     * 
     * @param schedule
     */
    @PreAuthorize("hasPermission(#schedule.getPipeline(), 'pipeline.run')")
    @Transactional
    @Override
    public void execute(Schedule schedule) {
        // update schedule
        Date oldLastChedule = schedule.getLastExecution();
        schedule.setLastExecution(new Date());
        // if the schedule is run one then disable it
        if (schedule.isJustOnce()) {
            schedule.setEnabled(false);
        }
        // create PipelineExecution
        PipelineExecution pipelineExec = new PipelineExecution(
                schedule.getPipeline());
        // set related scheduler
        pipelineExec.setSchedule(schedule);
        // will wake up other pipelines on end ..
        pipelineExec.setSilentMode(false);
        // set user .. copy owner of schedule
        pipelineExec.setOwner(schedule.getOwner());
        pipelineExec.setActor(schedule.getActor());

        Long epoch = (long) System.currentTimeMillis();
        Long orderNumber = schedule.getPriority();
        if (orderNumber != ScheduledJobsPriority.IGNORE.getValue()) { // no dividing by 0
            orderNumber = (epoch / orderNumber);
        }

        pipelineExec.setOrderNumber(orderNumber);

        // save data into DB -> in next DB check Engine start the execution
        pipelineFacade.save(pipelineExec);

        LOG.debug("Last schedule {} - > {} , new exec id: {}",
                oldLastChedule, schedule.getLastExecution(),
                pipelineExec.getId());

        save(schedule);
    }

    /**
     * Check for all schedule that run after some execution and run them
     * if all the the pre-runs has been executed. The call of this
     * function may be expensive as it check for all runAfter based
     * pipelines.
     */
    @Transactional
    @Override
    public void executeFollowers() {
        List<Schedule> toRun = this.scheduleDao.getActiveRunAfterBased();
        // filter those that should not run
        toRun = filterActiveRunAfter(toRun);
        // and execute
        for (Schedule schedule : toRun) {
            execute(schedule);
        }
    }

    /**
     * Check for all schedule that run after some execution and run them
     * if all the the pre-runs has been executed. The call of this
     * function may be expensive as it check for all runAfter based
     * pipelines.
     */
    @Transactional
    @Override
    public void executeFollowers(String backendID) {
        List<Schedule> toRun = this.scheduleDao.getActiveRunAfterBased();
        // filter those that should not run
        toRun = filterActiveRunAfter(toRun, backendID);
        // and execute
        for (Schedule schedule : toRun) {
            execute(schedule);
        }
    }

    /**
     * Executes all pipelines scheduled to follow given pipeline.
     * 
     * @param pipeline
     *            to follow
     */
    @Transactional
    @Override
    public void executeFollowers(Pipeline pipeline) {
        List<Schedule> toRun = scheduleDao.getFollowers(pipeline, true);
        // filter those that should not run
        toRun = filterActiveRunAfter(toRun);
        // and execute
        for (Schedule schedule : toRun) {
            execute(schedule);
        }
    }

    /**
     * @return schedules that are of type {@link ScheduleType#AFTER_PIPELINE} and that should be executed (all their {@link Schedule#afterPipelines
     *         after-pipeline} executions finished).
     */
    @PreAuthorize("hasRole('scheduleRule.read')")
    private List<Schedule> filterActiveRunAfter(List<Schedule> candidates) {
        return filterActiveRunAfter(candidates, null);
    }

    /**
     * @return schedules that are of type {@link ScheduleType#AFTER_PIPELINE} and that should be executed (all their {@link Schedule#afterPipelines
     *         after-pipeline} executions finished).
     */
    @PreAuthorize("hasRole('scheduleRule.read')")
    private List<Schedule> filterActiveRunAfter(List<Schedule> candidates, String backendID) {
        List<Schedule> result = new LinkedList<>();

        for (Schedule schedule : candidates) {
            List<Date> times = null;
            if (backendID != null) {
                times = this.scheduleDao.getLastExecForRunAfter(schedule, backendID);
            } else {
                times = this.scheduleDao.getLastExecForRunAfter(schedule);
            }
            boolean execute = true;
            for (Date item : times) {
                if (item == null) {
                    // no successfull execution so far .. 
                    execute = false;
                } else if (schedule.getLastExecution() == null) {
                    // schedule has never started any pipeline so far
                    // -> consider execution dependency satisfied
                    execute = true;
                } else if (item.before(schedule.getLastExecution())) {
                    // was executed before, but not atfer 
                    execute = false;
                }
            }

            if (execute) {
                // add to the result list
                result.add(schedule);
            }
        }

        return result;
    }

}
