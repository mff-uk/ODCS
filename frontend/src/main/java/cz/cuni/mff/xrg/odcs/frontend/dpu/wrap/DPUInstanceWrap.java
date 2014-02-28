package cz.cuni.mff.xrg.odcs.frontend.dpu.wrap;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.facade.DPUFacade;

/**
 * Wrap {@link DPUInstanceRecord} to made work with configuration and
 * configuration dialog easier.
 *
 * @author Petyr
 *
 */
public class DPUInstanceWrap extends DPURecordWrap {
	
	private final DPUFacade dpuFacade;

	/**
	 * Wrapped DPUTemplateRecord.
	 */
	private final DPUInstanceRecord dpuInstance;

	/**
	 * Create wrap for DPUTemplateRecord.
	 *
	 * @param dpuTemplate
	 * @param dpuFacade
	 */
	public DPUInstanceWrap(DPUInstanceRecord dpuTemplate, DPUFacade dpuFacade) {
		super(dpuTemplate, false);
		this.dpuFacade = dpuFacade;
		this.dpuInstance = dpuTemplate;
	}

	/**
	 * Save wrapped DPUInstanceInto database. To save configuration from dialog
	 * as well call {{@link #saveConfig()} first.
	 */
	public void save() {
		dpuFacade.save(dpuInstance);
	}

	/**
	 * Get DPUInstance record.
	 *
	 * @return DPUInstance record
	 */
	public DPUInstanceRecord getDPUInstanceRecord() {
		return dpuInstance;
	}
}
