package cz.cuni.mff.xrg.odcs.frontend.dpu.dialog;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import eu.unifiedviews.dpu.config.vaadin.ConfigDialogContext;

/**
 * Implementation of {@link ConfigDialogContext}.
 *
 * @author Petyr
 */
public class ConfigDialogContextImpl implements ConfigDialogContext {

    /**
     * True in case that the dialog is used for template, false otherwise.
     */
    private final boolean isTemplate;

    private final Locale locale;

    protected AppConfig appConfig;

    /**
     * Constructor.
     *
     */
    public ConfigDialogContextImpl(boolean isTemplate, Locale locale, AppConfig appConfig) {
        this.isTemplate = isTemplate;
        this.locale = locale;
        this.appConfig = appConfig;
    }

    @Override
    public boolean isTemplate() {
        return this.isTemplate;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public Map<String, String> getEnvironment() {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<Object, Object> entry : appConfig.getProperties().entrySet()) {
            result.put((String) entry.getKey(), (String) entry.getValue());
        }
        return result;
    }

}
