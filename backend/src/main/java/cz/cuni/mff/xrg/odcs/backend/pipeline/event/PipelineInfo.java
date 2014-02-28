package cz.cuni.mff.xrg.odcs.backend.pipeline.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecordType;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;

/**
 * Event that is used to publish informations about pipeline execution. 
 * 
 * @author Petyr
 *
 */
public class PipelineInfo extends PipelineEvent {

	private static final Logger LOG = LoggerFactory
			.getLogger(PipelineInfo.class);
	
	private final String shortMessage;
	
	private final String longMessage;
	
	protected PipelineInfo(PipelineExecution execution,
			Object source,
			String shortMessage,
			String longMessage) {
		super(null, execution, source);
		this.shortMessage = shortMessage;
		this.longMessage = longMessage;
	}

	@Override
	public MessageRecord getRecord() {
		return new MessageRecord(time, MessageRecordType.PIPELINE_INFO, null, execution, shortMessage, longMessage);
	}

	public static PipelineInfo createWait(PipelineExecution execution,
			Object source) {
		LOG.info("Execution is waiting for running conflict pipelines to end ...");
		return new PipelineInfo(execution, source, "Pipeline is waiting as there are conflicts pipeline running", "");
	}
	
	public static PipelineInfo createWaitEnd(PipelineExecution execution,
			Object source) {
		LOG.info("Execution continue");
		return new PipelineInfo(execution, source, "Pipeline continue", "");
	}	
	
	public static PipelineInfo createStart(PipelineExecution execution,
			Object source) {
		// prepare message
		final String msgShort
				= String.format("Starting execution: %d", execution.getId());
		final String msgLong
				= String.format("Starting execution number: %d pipeline: %s", 
						execution.getId(), execution.getPipeline().getName());
		LOG.info(msgLong);
		return new PipelineInfo(execution, source, 
				msgShort, msgLong);
	} 
	
}
