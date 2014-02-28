package cz.cuni.mff.xrg.odcs.commons.app.facade;

import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.commons.app.auth.ShareType;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.DbExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.DbOpenEvent;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.DbPipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.OpenEvent;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;
import java.util.ArrayList;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

/**
 * Facade providing actions with pipelines.
 *
 * @author Jan Vojt
 */
@Transactional(readOnly = true)
class PipelineFacadeImpl implements PipelineFacade {

    private static final Logger LOG = LoggerFactory.getLogger(PipelineFacadeImpl.class);
	
    @Autowired(required = false)
    private AuthenticationContext authCtx;
	
	@Autowired
	private DbPipeline pipelineDao;
	
	@Autowired
	private DbExecution executionDao;
	
	@Autowired
	private DPUFacade dpuFacade;
	
	@Autowired
	private DbOpenEvent openEventDao;
	
	/**
	 * Timeout how long will we consider {@link OpenEvent} active.
	 */
	private static final int PPL_OPEN_TTL = 10;

    /* ******************* Methods for managing Pipeline ******************** */
    /**
     * Pipeline factory with preset currently logged-in {@link User} as owner.
     * Created instance is not yet managed by {@link EntityManager}, thus needs
     * to be saved with {@link #save(Pipeline)} method.
     *
     * @return newly created pipeline
     */
	@Override
    public Pipeline createPipeline() {
		Pipeline newPipeline = new Pipeline();
		newPipeline.setVisibility(ShareType.PRIVATE);
        if (authCtx != null) {
            newPipeline.setUser(authCtx.getUser());
        }
		return newPipeline;
    }

    /**
     * Creates a clone of given pipeline, persists it, and returns it as a new
	 * instance. Original owner is not preserved, rather currently logged in
	 * user is set as an owner of the newly created pipeline.
     *
     * @param pipeline original pipeline to copy
     * @return newly copied pipeline
     */
	@Transactional
    @PreAuthorize("hasPermission(#pipeline, 'copy')")
	@Override
    public Pipeline copyPipeline(Pipeline pipeline) {

		Pipeline newPipeline = new Pipeline(pipeline);
		
		// determine new name for pipeline
		String oName = "Copy of " + pipeline.getName();
		String nName = oName;
		int no = 1;
		while (hasPipelineWithName(nName, null)) {
			nName = oName + " #" + no++;
		}
		newPipeline.setName(nName);
		newPipeline.setVisibility(ShareType.PRIVATE);
		
        if (authCtx != null) {
            newPipeline.setUser(authCtx.getUser());
        }
		
		save(newPipeline);
		return newPipeline;
    }

    /**
     * Returns list of all pipelines persisted in the database.
     *
     * @return list of pipelines
	 * @deprecated performance intensive for many pipelines in DB, use lazy
	 *			   container with paging instead
     */
	@Deprecated
    @PostFilter("hasPermission(filterObject,'view')")
	@Override
    public List<Pipeline> getAllPipelines() {
		return pipelineDao.getAll();
    }

    /**
     * Find pipeline in database by ID and return it.
     *
     * @param id of Pipeline
     * @return Pipeline the found pipeline or null if the pipeline with given ID
     * does not exist
     */
    @PostAuthorize("hasPermission(returnObject,'view')")
	@Override
    public Pipeline getPipeline(long id) {
		return pipelineDao.getInstance(id);
    }

    /**
     * Saves any modifications made to the pipeline into the database.
     *
     * @param pipeline
     */
    @Transactional
    @PreAuthorize("hasPermission(#pipeline,'save')")
	@Override
    public void save(Pipeline pipeline) {
		
		// If pipeline is public, we need to make sure
		// all DPU templates used in this pipeline are
		// public as well.
		if (ShareType.PUBLIC.contains(pipeline.getShareType())) {
			for (DPUTemplateRecord dpu : getPrivateDPUs(pipeline)) {
				if (ShareType.PRIVATE.equals(dpu.getShareType())) {
					// we found a private DPU in public pipeline -> make public
					dpu.setVisibility(ShareType.PUBLIC_RO);
					dpuFacade.save(dpu);
				}
			}
		}
		pipeline.setLastChange(new Date());
		pipelineDao.save(pipeline);
    }

    /**
     * Deletes pipeline from database.
     *
     * @param pipeline
     */
    @Transactional
    @PreAuthorize("hasPermission(#pipeline, 'delete')")
	@Override
    public void delete(Pipeline pipeline) {
		pipelineDao.delete(pipeline);
    }
	
    @PreAuthorize("hasPermission(#dpu, 'view')")
    @PostFilter("hasPermission(filterObject,'view')")
	@Override
    public List<Pipeline> getPipelinesUsingDPU(DPUTemplateRecord dpu) {
		return pipelineDao.getPipelinesUsingDPU(dpu);
    }
	
    @PreAuthorize("hasPermission(#dpu, 'delete')")
	@Override
    public List<Pipeline> getAllPipelinesUsingDPU(DPUTemplateRecord dpu) {
		return pipelineDao.getPipelinesUsingDPU(dpu);
    }

	/**
	 * Checks for duplicate pipeline names. The name of pipeline in second
	 * argument is ignored, if given. It is to be used when editing already
	 * existing pipeline.
	 * 
	 * @param newName
	 * @param pipeline to be renamed, or null
	 * @return 
	 */
	@Override
    public boolean hasPipelineWithName(String newName, Pipeline pipeline) {
		Pipeline duplicate = pipelineDao.getPipelineByName(newName);
        return !(duplicate == null || duplicate.equals(pipeline));
    }
	
	/**
	 * Lists all private DPU templates which are used in given pipeline.
	 * 
	 * @param pipeline to inspect for private DPUs
	 * @return list of private DPUs used in pipeline
	 */
	@Override
	public List<DPUTemplateRecord> getPrivateDPUs(Pipeline pipeline) {
		List<DPUTemplateRecord> dpus = new ArrayList<>();
		for (Node node : pipeline.getGraph().getNodes()) {
			DPUTemplateRecord dpu = node.getDpuInstance().getTemplate();
			if (ShareType.PRIVATE.equals(dpu.getShareType())) {
				dpus.add(dpu);
			}
		}
		return dpus;
	}
	
	/**
	 * Creates an open pipeline event with current timestamp. User is taken from
	 * authentication context (currently logged in user).
	 * 
	 * @param pipeline which is open
	 */
	@Transactional
	@Override
	public void createOpenEvent(Pipeline pipeline) {
		
		if (pipeline.getId() == null) {
			// pipeline has not been persisted yet
			// -> it cannot be opened by anyone
			return;
		}
		
		User user = authCtx.getUser();
		if (user == null) {
			// user logged out in the meantime -> ignore
			return;
		}
		
		OpenEvent event = openEventDao.getOpenEvent(pipeline, user);
		
		if (event == null) {
			event = new OpenEvent();
			event.setPipeline(pipeline);
			event.setUser(user);
		}
		
		event.setTimestamp(new Date());
		openEventDao.save(event);
	}
	
	/**
	 * Lists all open events representing a list of pipeline that are currently
	 * open in pipeline canvas. Events of currently logged in user are ignored
	 * and not included in the resulting list.
	 * 
	 * @param pipeline
	 * @return list of open events
	 */
	@Override
	public List<OpenEvent> getOpenPipelineEvents(Pipeline pipeline) {
		LOG.trace("getOpenPipelineEvents({})", pipeline.getId());
		if (pipeline.getId() == null) {
			// pipeline has not been persisted yet
			// -> it cannot be opened by anyone else
			return new ArrayList<>();
		}
		
		Date from = new Date((new Date()).getTime() - PPL_OPEN_TTL * 1000);

		if (authCtx != null) {
			User loggedUser = authCtx.getUser();
			return openEventDao.getOpenEvents(pipeline, from, loggedUser);
		} else {
			// user is null
			return openEventDao.getOpenEvents(pipeline, from);
		}
	}
	
	/**
	 * Checks if (possibly detached) pipeline has been modified by someone else.
	 * 
	 * @param pipeline to check
	 * @return true if pipeline was changed while detached from entity manager,
	 *			false otherwise
	 */
	@Override
	public boolean isUpToDate(Pipeline pipeline) {
		LOG.trace("isUpToDate({})", pipeline.getId());
		if (pipeline.getId() == null) {
			// new pipeline -> lets say it is up-to-date
			return true;
		}
		
		// fetch fresh pipeline from db
		Pipeline dbPipeline = getPipeline(pipeline.getId());
		if (dbPipeline == null) {
			// someone probably deleted pipeline in the meantime
			// -> lets say it is NOT up-to-date
			return false;
		}
		
		Date lastChange = dbPipeline.getLastChange();
                Date myLastChange = pipeline.getLastChange();
		return lastChange == null ? true :
                        myLastChange == null ? false : !lastChange.after(myLastChange);
	}
	
    /* ******************** Methods for managing PipelineExecutions ********* */
    /**
     * Creates a new {@link PipelineExecution}, which represents a pipeline run.
     * Created instance is not yet managed by {@link EntityManager}, thus needs
     * to be saved with {@link #save(PipelineExecution)} method.
     *
     * @param pipeline
     * @return pipeline execution of given pipeline
     */
	@Override
    public PipelineExecution createExecution(Pipeline pipeline) {
		PipelineExecution newExec = new PipelineExecution(pipeline);
        if (authCtx != null) {
            newExec.setOwner(authCtx.getUser());
        }
		return newExec;
    }

    /**
     * Fetches all {@link PipelineExecution}s from database.
     *
     * @return list of executions
	 * @deprecated performance intensive for many pipeline executions, use
	 *			   container with paging support instead
     */
	@Deprecated
	@Override
    public List<PipelineExecution> getAllExecutions() {
		return executionDao.getAll();
    }

    /**
     * Fetches all {@link PipelineExecution}s with given state from database.
     *
     * @param status
     * @return list of executions
     */
	@Override
    public List<PipelineExecution> getAllExecutions(PipelineExecutionStatus status) {
		return executionDao.getAll(status);
    }

    /**
     * Find pipeline execution in database by ID and return it.
     *
     * @param id of PipelineExecution
     * @return PipelineExecution
     */
	@Override
    public PipelineExecution getExecution(long id) {
		return executionDao.getInstance(id);
    }

    /**
     * Fetch all executions for given pipeline.
     *
     * @param pipeline
     * @return pipeline executions
     */
	@Override
    public List<PipelineExecution> getExecutions(Pipeline pipeline) {
		return executionDao.getAll(pipeline);
    }

    /**
     * Fetch executions for given pipeline in given status.
     *
     * @param pipeline Pipeline which executions should be fetched.
     * @param status Execution status, in which execution should be.
     * @return PipelineExecutions
     *
     */
	@Override
    public List<PipelineExecution> getExecutions(Pipeline pipeline, PipelineExecutionStatus status) {
        return executionDao.getAll(pipeline, status);
    }

    /**
     * Return end time of latest execution of given status for given pipeline.
     *
     * Ignore null values.
     *
     * @param pipeline
     * @param status Execution status, used to filter pipelines.
     * @return
     */
	@Override
    public Date getLastExecTime(Pipeline pipeline, PipelineExecutionStatus status) {

        HashSet statuses = new HashSet(1);
        statuses.add(status);
        PipelineExecution exec = getLastExec(pipeline, statuses);

        return (exec == null) ? null : exec.getEnd();
    }

    /**
     * Return latest execution of given statuses for given pipeline. Ignore null
     * values.
     *
     * @param pipeline
     * @param statuses Set of execution statuses, used to filter pipelines.
     * @return last execution or null
     */
	@Override
    public PipelineExecution getLastExec(Pipeline pipeline,
            Set<PipelineExecutionStatus> statuses) {
		return executionDao.getLastExecution(pipeline, statuses);
    }

    /**
     * Return latest execution of given pipeline. Ignore null values.
     *
     * @param pipeline
     * @return last execution or null
     */
	@Override
    public PipelineExecution getLastExec(Pipeline pipeline) {
		return executionDao.getLastExecution(pipeline, EnumSet.allOf(PipelineExecutionStatus.class));
    }

    /**
     * Return latest execution of given statuses for given schedule. Ignore null
     * values.
     *
     * @param schedule
     * @param statuses Set of execution statuses, used to filter pipelines.
     * @return last execution or null
     */
	@Override
    public PipelineExecution getLastExec(Schedule schedule,
            Set<PipelineExecutionStatus> statuses) {
        return executionDao.getLastExecution(schedule, statuses);
    }

    /**
     * Tells whether there were any changes to pipeline executions since the
     * last load.
     *
     * <p>
     * This method is provided purely for performance optimization of refreshing
     * execution statuses. Functionality is backed by database trigger
     * &quot;update_last_change&quot;.
     *
     * @param lastLoad
     * @return
     */
	@Override
    public boolean hasModifiedExecutions(Date lastLoad) {
		return executionDao.hasModified(lastLoad);
    }

    /**
     * Persists new {@link PipelineExecution} or updates it if it was already
     * persisted before.
     *
     * @param exec
     */
    @Transactional
	@Override
    public void save(PipelineExecution exec) {
		executionDao.save(exec);
    }

    /**
     * Deletes pipeline from database.
     *
     * @param exec
     */
    @Transactional
	@Override
    public void delete(PipelineExecution exec) {
		executionDao.delete(exec);
    }

    /**
     * Stop the execution.
     *
     * @param execution pipeline execution to stop
     */
	@Override
    @PreAuthorize("hasPermission(#execution, 'save')")
	@Transactional
    public void stopExecution(PipelineExecution execution) {
		PipelineExecution currentExec = getExecution(execution.getId());
		
		if (currentExec.getStatus() == PipelineExecutionStatus.RUNNING) {
			execution.stop();
			save(execution);
		} else {
			// we are not in running state anymore .. so we do not
			// save the pipeline
		}
    }

	/**
	 * Setter for mocking authenticated users.
	 * 
	 * @param authCtx authentication context
	 */
	void setAuthCtx(AuthenticationContext authCtx) {
		this.authCtx = authCtx;
	}

}
