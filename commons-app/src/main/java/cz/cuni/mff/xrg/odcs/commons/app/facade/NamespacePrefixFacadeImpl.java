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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import cz.cuni.mff.xrg.odcs.commons.app.rdf.namespace.DbNamespacePrefix;
import cz.cuni.mff.xrg.odcs.commons.app.rdf.namespace.NamespacePrefix;

/**
 * Facade for managing persisted entities of {@link NamespacePrefix}.
 * 
 * @author Jan Vojt
 */
@Transactional(readOnly = true)
class NamespacePrefixFacadeImpl implements NamespacePrefixFacade {

    private static final Logger LOG = LoggerFactory.getLogger(NamespacePrefixFacadeImpl.class);

    @Autowired
    private DbNamespacePrefix prefixDao;

    /**
     * Namespace prefix factory.
     * 
     * @param name
     * @param URI
     * @return
     */
    @Override
    public NamespacePrefix createPrefix(String name, String URI) {
        return new NamespacePrefix(name, URI);
    }

    /**
     * Fetch all RDF namespace prefixes defined in application.
     * 
     * @return
     */
    @Override
    public List<NamespacePrefix> getAllPrefixes() {
        return prefixDao.getAllPrefixes();
    }

    /**
     * Fetch a single namespace RDF prefix given by ID.
     * 
     * @param id
     * @return
     */
    @Override
    public NamespacePrefix getPrefix(long id) {
        return prefixDao.getInstance(id);
    }

    /**
     * Find prefix with given name in persistent storage.
     * 
     * @param name
     * @return
     */
    @Override
    public NamespacePrefix getPrefixByName(String name) {
        return prefixDao.getByName(name);
    }

    /**
     * Persists given RDF namespace prefix. If it is persisted already, all changes
     * performed on object are updated.
     * 
     * @param prefix
     *            namespace prefix to persist or update
     */
    @Transactional
    @Override
    public void save(NamespacePrefix prefix) {
        prefixDao.save(prefix);
    }

    /**
     * Deletes RDF namespace prefix from persistent storage.
     * 
     * @param prefix
     */
    @Transactional
    @Override
    public void delete(NamespacePrefix prefix) {
        prefixDao.delete(prefix);
    }

}
