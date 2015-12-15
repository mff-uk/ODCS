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
package cz.cuni.mff.xrg.odcs.frontend.dpu.validator;

import java.lang.reflect.InvocationTargetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUValidator;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUValidatorException;
import cz.cuni.mff.xrg.odcs.frontend.i18n.Messages;
import eu.unifiedviews.dpu.config.vaadin.AbstractConfigDialog;
import eu.unifiedviews.dpu.config.vaadin.ConfigDialogProvider;

/**
 * Validate DPU's dialog.
 * 
 * @author Petyr
 */
@Component
class ConfigurationDialogValidator implements DPUValidator {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationDialogValidator.class);

    @Override
    public void validate(DPUTemplateRecord dpu, Object dpuInstance)
            throws DPUValidatorException {
        if (dpuInstance instanceof ConfigDialogProvider) {
            @SuppressWarnings("rawtypes")
            ConfigDialogProvider dialogProvider = (ConfigDialogProvider) dpuInstance;

            try {
                java.lang.reflect.Method method = dialogProvider.getClass().getMethod("getConfigurationDialog");
                final Object result = method.invoke(dialogProvider);
                // Try to load a dialog.
                AbstractConfigDialog<?> configDialog = (AbstractConfigDialog<?>)result;
            } catch (NoSuchMethodException | SecurityException ex) {
                LOG.error("Can't get method.", ex);
                throw new DPUValidatorException(Messages.getString("ConfigurationDialogValidator.exception") + ex.getMessage(), ex);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                LOG.error("Can't call method.", ex);
                throw new DPUValidatorException(Messages.getString("ConfigurationDialogValidator.exception") + ex.getMessage(), ex);
            } catch (Throwable t) {
                // catch everything ..
                LOG.error("Dialog load failed.", t);
                throw new DPUValidatorException(Messages.getString("ConfigurationDialogValidator.exception") + t.getMessage(), t);
            }
        } else {
            // no dialog
        }
    }
}
