package cz.cuni.xrg.intlib.commons.data;

/**
 * Interface for DataUnit factory.
 * 
 * @author Petyr
 *
 */
public interface DataUnitFactory {
	
	/**
	 * Return instance of class that implements required interface.
	 *
	 * @param type Required {@link DataUnitType} type.
	 * @return {@link DataUnit} or null in case of unsupported type. 
	 */	
	public DataUnit create(DataUnitType type);
	
	/**
	 * Return instance of class that implements required interface.
	 *
	 * @param type Required {@link DataUnitType} type.
	 * @param mergerPrepare Correspond to parameter margeRepare in 
	 * {@link cz.cuni.xrg.intlib.commons.data.DataUnit#createNew(String,File,boolean) createNew} method.
	 * @return {@link DataUnit} or null in case of unsupported type. 
	 */
	public DataUnit create(DataUnitType type, boolean mergePrepare);
}
