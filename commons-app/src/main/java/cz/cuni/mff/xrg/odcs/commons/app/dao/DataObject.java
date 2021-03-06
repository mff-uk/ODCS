package cz.cuni.mff.xrg.odcs.commons.app.dao;

import java.io.Serializable;

/**
 * Marker interface for objects that can be used with {@link DataAccess} and
 * {@link DataAccessRead}.
 * 
 * @author Petyr
 * @author Jan Vojt
 *
 */
public interface DataObject extends Serializable {

	/**
	 * @return object's id
	 */
	public Long getId();
		
}
