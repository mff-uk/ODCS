package cz.cuni.xrg.intlib.commons.app.data.pipeline.event;

/**
 * Base class for {@link ETLPipelineImpl} events
 *
 * @author Alex Kreiser (akreiser@gmail.com)
 */
public abstract class PipelineEvent extends ETLEvent {

    protected final ETLPipelineImpl pipeline;
    protected final String id;

    public PipelineEvent(ETLPipelineImpl pipeline, String runId, Object source) {
        super(source);
        this.pipeline = pipeline;
        this.id = runId;
    }

    /**
     * Returns the {@link ETLPipelineImpl} associated to this event.
     *
     * @return
     */
    public ETLPipeline getPipeline() {
        return pipeline;
    }

    /**
     * Returns the unique identifier of this event (!= pipeline id)
     * 
     * @return 
     */
    public String getId() {
        return id;
    }
}