package cz.cuni.mff.xrg.odcs.commons.app.facade;

import cz.cuni.mff.xrg.odcs.commons.app.rdf.namespace.NamespacePrefix;
import java.util.List;

/**
 * Facade for managing persisted entities of {@link NamespacePrefix}.
 * 
 * @author Jan Vojt
 */
public interface NamespacePrefixFacade extends Facade {
	
	/**
	 * Namespace prefix factory.
	 * 
	 * @param name
	 * @param URI
	 * @return 
	 */
	NamespacePrefix createPrefix(String name, String URI);
	
	/**
	 * Fetch all RDF namespace prefixes defined in application.
	 * 
	 * @return 
	 */
	List<NamespacePrefix> getAllPrefixes();
	
	/**
	 * Fetch a single namespace RDF prefix given by ID.
	 * 
	 * @param id
	 * @return 
	 */
	NamespacePrefix getPrefix(long id);
	
	/**
	 * Find prefix with given name in persistent storage.
	 * 
	 * @param name
	 * @return 
	 */
	NamespacePrefix getPrefixByName(String name);
	
	/**
	 * Persists given RDF namespace prefix. If it is persisted already, all changes
	 * performed on object are updated.
	 * 
	 * @param prefix namespace prefix to persist or update
	 */
	void save(NamespacePrefix prefix);
	
	/**
	 * Deletes RDF namespace prefix from persistent storage.
	 * 
	 * @param prefix 
	 */
	void delete(NamespacePrefix prefix);
	
}