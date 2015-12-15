/**
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.mff.xrg.odcs.commons.app.facade;

import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.rdf.namespace.NamespacePrefix;

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
     * @return created namespace prefix
     */
    NamespacePrefix createPrefix(String name, String URI);

    /**
     * Fetch all RDF namespace prefixes defined in application.
     * 
     * @return list of all namespace prefixes in the system
     */
    List<NamespacePrefix> getAllPrefixes();

    /**
     * Fetch a single namespace RDF prefix given by ID.
     * 
     * @param id
     * @return namespace prefix with given id
     */
    NamespacePrefix getPrefix(long id);

    /**
     * Find prefix with given name in persistent storage.
     * 
     * @param name
     * @return namespace prefix with given name
     */
    NamespacePrefix getPrefixByName(String name);

    /**
     * Persists given RDF namespace prefix. If it is persisted already, all changes
     * performed on object are updated.
     * 
     * @param prefix
     *            namespace prefix to persist or update
     */
    void save(NamespacePrefix prefix);

    /**
     * Deletes RDF namespace prefix from persistent storage.
     * 
     * @param prefix
     */
    void delete(NamespacePrefix prefix);

}
