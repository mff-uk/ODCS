package cz.cuni.mff.xrg.odcs.commons.app.facade;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUType;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;

import java.util.List;

/**
 * Facade for working with DPUs.
 *
 * @author Jan Vojt
 * @author Petyr
 */
public interface DPUFacade extends Facade {

	/* ******************* Methods for DPUTemplateRecord management *********************** */
	
	/**
	 * Creates DPU template with given name of given type and assigns currently
	 * logged in user as owner.
	 * 
	 * @param name
	 * @param type
	 * @return newly created DPU template
	 */
	DPUTemplateRecord createTemplate(String name, DPUType type);

	/**
	 * Create copy of DPU template, as the owner the current user is set.
	 * 
	 * @param original
	 * @return
	 */
	 DPUTemplateRecord createCopy(DPUTemplateRecord original);
	
	/**
	 * Creates a new DPURecord with the same properties and configuration as in given
	 * {@link DPUInstance}. Note that newly created DPURecord is only returned, but
	 * not managed by database. To persist it, {@link #save(DPURecord)} must be called
	 * explicitly.
	 *
	 * @param instance
	 * @return new DPURecord
	 */
	DPUTemplateRecord createTemplateFromInstance(DPUInstanceRecord instance);

	/**
	 * Returns list of all DPUTemplateRecords currently persisted in database.
	 * @return DPURecord list
	 */
	List<DPUTemplateRecord> getAllTemplates();
	
	/**
	 * Find DPUTemplateRecord in database by ID and return it.
	 * @param id
	 * @return
	 */
	DPUTemplateRecord getTemplate(long id);
	
	/**
	 * Saves any modifications made to the DPUTemplateRecord into the database.
	 * 
	 * @param dpu
	 */
	void save(DPUTemplateRecord dpu);
	
	/**
	 * Deletes DPUTemplateRecord from the database.
	 * @param dpu
	 */
	void delete(DPUTemplateRecord dpu);

	/**
	 * Fetch all child DPU templates for a given DPU template.
	 * 
	 * @param parent DPU template
	 * @return list of child DPU templates or empty collection
	 */
	List<DPUTemplateRecord> getChildDPUs(DPUTemplateRecord parent);

	/* **************** Methods for DPUInstanceRecord Instance management ***************** */

	/**
	 * Creates DPUInstanceRecord with configuration copied from template without
	 * persisting it.
	 *
	 * @param dpuTemplate to create from
	 * @return newly created DPU instance
	 */
	DPUInstanceRecord createInstanceFromTemplate(DPUTemplateRecord dpuTemplate);

	/**
	 * Returns list of all DPUInstanceRecord currently persisted in database.
	 *
	 * @return DPUInstance list
	 */
	List<DPUInstanceRecord> getAllDPUInstances();

	/**
	 * Find DPUInstanceRecord in database by ID and return it.
	 *
	 * @param id
	 * @return
	 */
	DPUInstanceRecord getDPUInstance(long id);

	/**
	 * Saves any modifications made to the DPUInstanceRecord into the database.
	 * @param dpu
	 */
	void save(DPUInstanceRecord dpu);

	/**
	 * Deletes DPUInstance from the database.
	 * @param dpu
	 */
	void delete(DPUInstanceRecord dpu);

	/* **************** Methods for Record (messages) management ***************** */

	/**
	 * Fetches all DPURecords emitted by given PipelineExecution.
	 *
	 * @param pipelineExec
	 * @return
	 */
	List<MessageRecord> getAllDPURecords(PipelineExecution pipelineExec);

	/**
	 * Find Record in database by ID and return it.
	 *
	 * @param id
	 * @return
	 */
	MessageRecord getDPURecord(long id);

	/**
	 * Saves any modifications made to the Record into the database.
	 *
	 * @param record
	 */
	void save(MessageRecord record);

	/**
	 * Deletes Record from the database.
	 *
	 * @param record
	 */
	void delete(MessageRecord record);
	
}