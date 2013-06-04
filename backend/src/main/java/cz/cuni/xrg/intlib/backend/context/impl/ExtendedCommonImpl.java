package cz.cuni.xrg.intlib.backend.context.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cz.cuni.xrg.intlib.backend.data.DataUnitFactoryImpl;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContext;
import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;
import cz.cuni.xrg.intlib.commons.data.DataUnitFactory;

/**
 * Provide implementation of commons methods for 
 * {@link ExtendedExtractContextImpl}, {@link ExtendedLoadContextImpl} 
 * and {@link ExtendedTransformContextImpl}
 * 
 * @author Petyr
 *
 */
class ExtendedCommonImpl {

	/**
	 * Unique context id.
	 */
	private String id;
	
    /**
     * Custom data holder.
     */
    private Map<String, Object> customData;

    /**
     * True id the related DPURecord should be run in debug mode.
     */
    private boolean isDebugging;
	
    /**
     * PipelineExecution. The one who caused
     * run of this DPURecord.
     */
	private PipelineExecution execution;

	/**
	 * Instance of DPURecord for which is this context.
	 */
	private DPUInstance dpuInstance;
    	
	/**
	 * Used factory.
	 */
	private DataUnitFactoryImpl dataUnitFactory;
	
	/**
	 * Manage mapping context into execution's directory. 
	 */
	private ExecutionContext contextWriter;
	
	/**
	 * Counter used to generate unique id for data.
	 */
	private int storeCounter;	
	
	/**
	 * Log facade.
	 */
	private Logger logger;
	
	public ExtendedCommonImpl(String id, PipelineExecution execution, DPUInstance dpuInstance, 
			ExecutionContext contextWriter) {
		this.id = id;
		this.customData = new HashMap<String, Object>();
		this.isDebugging = execution.isDebugging();
		this.execution = execution;
		this.dpuInstance = dpuInstance;
		this.dataUnitFactory = new DataUnitFactoryImpl(this.id, contextWriter, dpuInstance);
		this.contextWriter = contextWriter;
		this.storeCounter = 0;
		this.logger = Logger.getLogger(ExtendedCommonImpl.class); 
	}	
	
	public String storeData(Object object) throws Exception {
		String id = Integer.toString(this.storeCounter) + ".ser";
		++this.storeCounter;
		// ...
		try
		{
			FileOutputStream fileOut =
				new FileOutputStream( new File(contextWriter.getDirForDPUStorage(dpuInstance), id) );
			ObjectOutputStream outStream =
				new ObjectOutputStream(fileOut);
			outStream.writeObject(object);
			outStream.close();
			fileOut.close();
		} catch(IOException e) {
			logger.error("storeData", e);
		}				
		return null;
	}

	public Object loadData(String id) {
		Object result = null;
		try {
			FileInputStream fileIn = 
					new FileInputStream( new File(contextWriter.getDirForDPUStorage(dpuInstance), id));
			ObjectInputStream inStream = new ObjectInputStream(fileIn);
			result = inStream.readObject();
			inStream.close();
			fileIn.close();
		} catch (IOException e) {
			logger.error("loadData", e);
		} catch (ClassNotFoundException e) {
			logger.error("loadData", e);
		}
		return result;
	}

	public void storeDataForResult(String id, Object object) {
		// TODO Petyr: storeDataForResult	
	}

	public boolean isDebugging() {
		return isDebugging;
	}

	public Map<String, Object> getCustomData() {
		return customData;
	}

	public DataUnitFactory getDataUnitFactory() {
		return dataUnitFactory;
	}	
	
	public PipelineExecution getPipelineExecution() {
		return execution;
	}

	public DPUInstance getDPUInstance() {
		return dpuInstance;
	}	
}
