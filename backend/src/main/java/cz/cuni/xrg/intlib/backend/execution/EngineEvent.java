package cz.cuni.xrg.intlib.backend.execution;

import org.springframework.context.ApplicationEvent;

/**
 * Event primary used to communicate with Engine.
 * 
 * @author Petyr
 *
 */
public class EngineEvent extends ApplicationEvent  {

	private EngineEventType type;
	
	public EngineEvent(EngineEventType type, Object source) {
		super(source);
		this.type = type;
	}

	public EngineEventType getType() {
		return type;
	}
	
}
