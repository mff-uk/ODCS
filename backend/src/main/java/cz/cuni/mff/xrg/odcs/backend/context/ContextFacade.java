package cz.cuni.mff.xrg.odcs.backend.context;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;

/**
 * Facade that provides method for manipulation with {@link Context}.
 *
 * @author Petyr
 *
 */
public class ContextFacade {
	
	@Autowired
	private ContextCloser closer;

	@Autowired
	private ContextCreator creater;

	@Autowired
	private ContextDeleter deleter;

	@Autowired
	private ContextMerger merger;

	@Autowired
	private ContextRestorer restorer;

	@Autowired
	private ContextSealer sealer;

	@Autowired
	private ContextSaver saver;
	
	/**
	 * If {@link Context} for given {@link DPUInstanceRecord} and
	 * {@link ExecutionContextInfo} already exist then load it. Otherwise create
	 * new empty context.
	 *
	 * @param dpuInstance
	 * @param contextInfo
	 * @param lastSuccExec
	 * @return New context.
	 * @throws ContextException
	 */
	public Context create(DPUInstanceRecord dpuInstance,
			ExecutionContextInfo contextInfo, Date lastSuccExec)
			throws ContextException {		
		Context context = creater.createContext(dpuInstance, contextInfo, lastSuccExec);
		// and try to reload
		try {
			restorer.restore(context);
		} catch (DataUnitException e) {
			throw new ContextException("Failed to create DataUnit.", e);
		}
		// and return
		return context;
	}

	/**
	 * Seal given context against modification from inside of DPU.
	 *
	 * @param context
	 */
	public void seal(Context context) {
		sealer.seal(context);
	}

	/**
	 * Save the data of given context.
	 * @param context 
	 */
	public void save(Context context) {
		saver.save(context);
	}
	
	/**
	 * Close the given context no data are saved.
	 * @param context 
	 */
	public void close(Context context) {
		closer.close(context);		
	}
	
	/**
	 * Delete {@link Context} and it's {@link ExecutionContextInfo}.
	 *
	 * @param context
	 * @param preserveContextInfo If true then data in {@link ExecutionContextInfo} are preserved.
	 */
	public void delete(Context context, boolean preserveContextInfo) {
		deleter.delete(context, preserveContextInfo);
	}

	/**
	 * Add data from left context to the right one based on given script. The
	 * newly created 
	 * {@link cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit} are cleaned by 
	 * calling {@link cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit#clean()}. 
	 * So any previous data are safely deleted and do not contaminate the execution.
	 *
	 * @param left
	 * @param right
	 * @param script
	 * @throws ContextException
	 */
	public void merge(Context left, Context right, String script)
			throws ContextException {
		merger.merge(left, right, script);
	}

}
