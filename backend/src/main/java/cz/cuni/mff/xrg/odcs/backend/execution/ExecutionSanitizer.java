package cz.cuni.mff.xrg.odcs.backend.execution;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.mff.xrg.odcs.backend.data.DataUnitFactory;
import cz.cuni.mff.xrg.odcs.backend.pipeline.event.PipelineRestart;
import cz.cuni.mff.xrg.odcs.backend.pipeline.event.PipelineSanitized;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.DataUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ProcessingUnitInfo;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitCreateException;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Delete context of given execution that has been interrupted by backend
 * unexpected shutdown.
 * 
 * @author Petyr
 * 
 */
class ExecutionSanitizer {

	private static final Logger LOG = LoggerFactory.getLogger(ExecutionSanitizer.class);

	/**
	 * Application's configuration.
	 */
	@Autowired
	protected AppConfig appConfig;

	@Autowired
	private DataUnitFactory dataUnitFactory;
	
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	
	/**
	 * Executions root directory.
	 */
	private File rootDirectory;

	@PostConstruct
	private void propertySetter() {
		this.rootDirectory = new File(appConfig.getString(ConfigProperty.GENERAL_WORKINGDIR));
	}

	/**
	 * Fix possible problems with given execution. Logs of this method are
	 * logged with the execution id of given {@link PipelineExecution}
	 * 
	 * Method does not save the changes into database! So called must secure
	 * persisting of changes into database.
	 * 
	 * @param execution
	 */
	public void sanitize(PipelineExecution execution) {
		// check for flags
		if (execution.getStop()) {
			sanitizeCancelling(execution);
			return;
		}
		
		switch (execution.getStatus()) {
		case CANCELLING:
			sanitizeCancelling(execution);
			return;
		case RUNNING:
			sanitizeRunning(execution);
			return;
		default:
			// do nothing with such pipeline .. 
			return;
		}
	}

	/**
	 * Restart given {@link PipelineExecution} back to
	 * {@link PipelineExecutionStatus#QUEUED} state.
	 * 
	 * @param execution
	 */
	private void sanitizeRunning(PipelineExecution execution) {
		eventPublisher.publishEvent(new PipelineRestart(execution, this));
	
		// set state back to scheduled
		execution.setStatus(PipelineExecutionStatus.QUEUED);
	}

	/**
	 * Complete the canceling process on given {@link PipelineExecution} and set
	 * {@link PipelineExecutionStatus#CANCELLED} state.
	 * 
	 * @param execution
	 */
	private void sanitizeCancelling(PipelineExecution execution) {
		// publish event about this .. 
		eventPublisher.publishEvent(new PipelineSanitized(execution, this));
		
		if (execution.isDebugging()) {
			// no deletion
		} else {
			// delete execution data
			deleteContext(execution);
			// and directory
			File toDelete = new File(rootDirectory, execution.getContext()
					.getRootPath());
			try {
				FileUtils.deleteDirectory(toDelete);
			} catch (IOException e) {
				LOG.warn("Can't delete directory after execution", e);
			}
		}
		Date now = new Date();
		// check if the run has the start set .. 
		if (execution.getStart() == null) {
			// set current as start time
			execution.setStart(now);
			// this means that the execution does not run at all
		}		
		// set canceled state
		execution.setStatus(PipelineExecutionStatus.CANCELLED);
		execution.setEnd(now);
	}

	/**
	 * Delete all dataUnits of given execution.
	 * 
	 * @param execution
	 */
	private void deleteContext(PipelineExecution execution) {
		LOG.info("Deleting context for: {}", execution.getPipeline().getName());
		ExecutionContextInfo context = execution.getContext();
		if (context == null) {
			// nothing to delete
			return;
		}

		Set<DPUInstanceRecord> instances = context.getDPUIndexes();
		for (DPUInstanceRecord dpu : instances) {
			// for each DPU
			ProcessingUnitInfo dpuInfo = context.getDPUInfo(dpu);
			deleteContext(context, dpu, dpuInfo);
		}
	}
	
	/**
	 * Delete dataUnits related to single DPU.
	 * 
	 * @param context
	 * @param dpuInstance
	 * @param dpuInfo
	 */
	private void deleteContext(ExecutionContextInfo context,
			DPUInstanceRecord dpuInstance,
			ProcessingUnitInfo dpuInfo) {
		LOG.info("Deleting context for dpu: {}", dpuInstance.getName());
		
		List<DataUnitInfo> dataUnits = dpuInfo.getDataUnits();
		for (DataUnitInfo dataUnitInfo : dataUnits) {
			// we need to construct the DataUnit, create it and then 
			// delete it
			int index = dataUnitInfo.getIndex();
			final DataUnitType type = dataUnitInfo.getType();
			final String id = context.generateDataUnitId(dpuInstance, index);
			final String name = dataUnitInfo.getName();
			
			final File rootDir = new File(appConfig.getString(ConfigProperty.GENERAL_WORKINGDIR));
			final File directory = new File(rootDir, context.getDataUnitTmpPath(dpuInstance, index));
			// create instance
			
			try {
				ManagableDataUnit dataUnit = dataUnitFactory.create(type, id, name, directory);
				// delete data .. 
				dataUnit.delete();
			} catch (DataUnitCreateException e) {
				LOG.warn("Failed to reinstantiate dataUnit", e);
			}
		}
	}	
	
}
