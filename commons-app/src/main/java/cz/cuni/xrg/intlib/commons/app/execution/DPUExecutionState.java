package cz.cuni.xrg.intlib.commons.app.execution;

import cz.cuni.xrg.intlib.commons.app.execution.context.ProcessingUnitInfo;

/**
 * Describe states for DPU execution. 
 * 
 * @see {@link ProcessingUnitInfo}
 * @author Petyr
 *
 */
public enum DPUExecutionState {
	/**
	 * The DPU is in pre-processing state. It's 
	 * the default state. Only DPU with this state can be executed.
	 */
	PREPROCESSING,
	/**
	 * The DPU is currently being executed. This state is from start
	 * of DPU execution method. The post-processing is not part
	 * of this state.
	 */
	RUNNING,
	/**
	 * The DPU execution has been finished.
	 */
	FINISHED,
	/**
	 * DPU execution failed.
	 */
	FAILED,
	/**
	 * DPU execution has been aborted on user request.
	 */
	ABORTED
}
