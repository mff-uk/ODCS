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
package cz.cuni.mff.xrg.odcs.backend.context;

import java.util.Date;
import java.util.Locale;

import cz.cuni.mff.xrg.odcs.commons.app.i18n.LocaleHolder;
import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.DataUnitFactory;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.mff.xrg.odcs.commons.app.resource.ResourceManager;
import cz.cuni.mff.xrg.odcs.commons.app.facade.RuntimePropertiesFacade;
import cz.cuni.mff.xrg.odcs.commons.app.properties.RuntimeProperty;
import eu.unifiedviews.dataunit.DataUnit;


/**
 * Component that is used to create {@link Context} for give {@link DPUInstanceRecord} and {@link ExecutionContextInfo}.
 * If context has some previous data ie. {@link ExecutionContextInfo} is not
 * empty data are not loaded. To load data use {@link ContextRestore}
 *
 * @author Petyr
 */
abstract class ContextCreator {

    /**
     * Factory used to create {@link DataUnit}s.
     */
    @Autowired
    private DataUnitFactory dataUnitFactory;

    @Autowired
    private ResourceManager resourceManager;

    /**
     * Create context for given {@link DPUInstanceRecord} and {@link ExecutionContextInfo}. The context is ready for use. Data from {@link ExecutionContextInfo}
     * are not loaded into context.
     *
     * @param dpuInstance
     * @param contextInfo
     * @param lastSuccExec
     * @return
     */
    public Context createContext(DPUInstanceRecord dpuInstance, ExecutionContextInfo contextInfo, Date lastSuccExec) {
        // create empty context
        Context newContext = createPureContext();
        // fill context with data

        newContext.setDPU(dpuInstance);
        newContext.setContextInfo(contextInfo);
        newContext.setLastSuccExec(lastSuccExec);
        newContext.setLocale(LocaleHolder.getLocale());

        newContext.setInputsManager(DataUnitManager.createInputManager(dpuInstance, dataUnitFactory, contextInfo, resourceManager));
        newContext.setOutputsManager(DataUnitManager.createOutputManager(dpuInstance, dataUnitFactory, contextInfo, resourceManager));

        return newContext;
    }

    /**
     * Method for spring that create new {@link Context}.
     *
     * @return
     */
    protected abstract Context createPureContext();

}
