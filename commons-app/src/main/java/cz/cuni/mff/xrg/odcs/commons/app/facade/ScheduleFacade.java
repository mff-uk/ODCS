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

import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleNotificationRecord;

/**
 * Facade providing actions with plan.
 * 
 * @author Jan Vojt
 * @author Petyr
 */
public interface ScheduleFacade extends Facade {

    /**
     * Schedule factory. Explicitly call {@link #save(cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule)} to
     * persist created entity.
     * 
     * @return initialized Schedule
     */
    Schedule createSchedule();

    /**
     * Returns list of all Plans currently persisted in database.
     * 
     * @return Plans list
     * @deprecated use container with paging instead
     */
    @Deprecated
    List<Schedule> getAllSchedules();

    /**
     * Fetches all {@link Schedule}s planned for given pipeline.
     * 
     * @param pipeline
     * @return list of schedules for pipeline
     */
    List<Schedule> getSchedulesFor(Pipeline pipeline);

    /**
     * Fetches all {@link Schedule}s which are activated in certain time
     * and the execution for the scheduled pipeline isn't already running.
     * 
     * @return list of all schedules planned to launch on time
     */
    List<Schedule> getAllTimeBasedNotQueuedRunning();

    /**
     * Fetches all {@link Schedule}s which are activated in certain time
     * and the execution for the scheduled pipeline isn't already running.
     * 
     * @return list of all schedules planned to launch on time
     */
    List<Schedule> getAllTimeBasedNotQueuedRunningForCluster();

    /**
     * Find Schedule in database by ID and return it.
     * 
     * @param id
     * @return schedule
     */
    Schedule getSchedule(long id);

    /**
     * Saves any modifications made to the Schedule into the database.
     * 
     * @param schedule
     */
    void save(Schedule schedule);

    /**
     * Deletes Schedule from the database.
     * 
     * @param schedule
     */
    void delete(Schedule schedule);

    /**
     * Deletes notification setting for schedule.
     * 
     * @param notify
     *            notification settings to delete
     */
    void deleteNotification(ScheduleNotificationRecord notify);

    /**
     * Create execution for given schedule. Also if the schedule is runOnce then
     * disable it. Ignore enable/disable option for schedule.
     * 
     * @param schedule
     */
    void execute(Schedule schedule);

    /**
     * Checks all schedule that run after some execution and run them if all the pre-runs
     * have been executed by this backend node
     * 
     * @param backendID
     */
    void executeFollowers(String backendID);

    /**
     * Checks all schedule that run after some execution and run them if all the pre-runs
     * have been executed
     */
    void executeFollowers();

    /**
     * Executes all pipelines scheduled to follow given pipeline.
     * 
     * @param pipeline
     *            to follow
     */
    void executeFollowers(Pipeline pipeline);

}
