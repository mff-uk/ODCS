package cz.cuni.xrg.intlib.backend;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUFacade;
import cz.cuni.xrg.intlib.commons.app.execution.log.LogFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineFacade;
import cz.cuni.xrg.intlib.commons.app.scheduling.ScheduleFacade;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Class provide access to database through provided facades.
 * @author Petyr
 *
 */
public class DatabaseAccess {

	/**
	 * Facade for working with DPUs.
	 */
	@Autowired
	private DPUFacade dpu;
	
	/**
	 * Facade for working with pipelines.
	 */
	@Autowired
	private PipelineFacade pipeline;

	/**
	 * Facade for working with plans.
	 */
	@Autowired
	private ScheduleFacade schedule;
	
	/**
	 * Facade for working with logs.
	 */	
	@Autowired
	private LogFacade log;
	
	public DPUFacade getDpu() {
		return dpu;
	}

	public PipelineFacade getPipeline() {
		return pipeline;
	}
	
	public ScheduleFacade getSchedule() {
		return schedule;
	}
	
	public LogFacade getLog() {
		return log;
	}	
}
