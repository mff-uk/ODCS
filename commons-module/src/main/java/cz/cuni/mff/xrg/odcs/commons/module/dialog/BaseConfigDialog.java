package cz.cuni.mff.xrg.odcs.commons.module.dialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.configuration.DPUConfigObject;
import cz.cuni.mff.xrg.odcs.commons.module.config.ConfigWrap;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogContext;

/**
 * 
 * Class which should be used by DPU developer as a base class from which his
 * DPU's configuration dialog is derived.
 * 
 * @author petyr
 * @param <C> Particular configuration object of the DPU
 */
public abstract class BaseConfigDialog<C extends DPUConfigObject>
		extends AbstractConfigDialog<C> {
	
	private static final Logger LOG = LoggerFactory.getLogger(BaseConfigDialog.class);
	
	/**
	 * Used to convert configuration object into byte array and back.
	 */
	private final ConfigWrap<C> configWrap;

	/**
	 * Last valid configuration that is in dialog. Is used to detect changes in
	 * configuration by function {@link #hasConfigChanged()}.
	 */
	private String lastSetConfig;
	
	/**
	 * DPUs context. 
	 */
	private ConfigDialogContext context;
	
	/**
	 * Initialize {@link BaseConfigDialog} for given configuration class.
	 * 
	 * @param configClass Configuration class.
	 */
	public BaseConfigDialog(Class<C> configClass) {
		this.configWrap = new ConfigWrap<>(configClass);
		this.lastSetConfig = null;
	}

	@Override
	public void setContext(ConfigDialogContext newContext) {
		this.context = newContext;
	}
	
	@Override
	public void setConfig(String conf) throws ConfigException {
		C config;
		try {
			config = configWrap.deserialize(conf);		
		} catch (ConfigException e) {
			LOG.error("Failed to deserialize configuration, using default instead.");
			// failed to deserialize configuraiton, use default
			config = configWrap.createInstance();
			setConfiguration(config);
			// rethrow
			throw e;
		}
		
		boolean originalConfigNull = config == null;
		
		if (originalConfigNull) {
			LOG.warn("The deserialized confirugarion is null, using default instead.");
			// null -> try to use default configuration
			config = configWrap.createInstance();
			if (config == null) {
				throw new ConfigException(
						"Missing configuration and failed to create default."
								+ "No configuration has been loaded into dialog.");
			}
		}
		
		// in every case set the configuration
		setConfiguration(config);
		lastSetConfig = conf;
		
		if (!config.isValid()) {
			if (originalConfigNull) {
				// newly created configuration is invalid
				throw new ConfigException(
						"The default configuration is invalid, there is "
								+ "probably problem in DPU's implementation.");
			} else {
				// notify for invalid configuration
				throw new ConfigException(
						"Invalid configuration loaded into dialog.");
			}
		}
	}

	@Override
	public String getConfig() throws ConfigException {
		C configuration = getConfiguration();
		// check for validity before saving
		if (configuration == null) {
			throw new ConfigException("Configuration dialog return null.");
		}
		
		if (!configuration.isValid()) {
			throw new ConfigException("Cofiguration dialog returns invalid configuration.");
		}

		lastSetConfig = configWrap.serialize(getConfiguration());
		return lastSetConfig;
	}

	@Override
	public String getToolTip() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public boolean hasConfigChanged() {
		String configString;
		try {
			C config = getConfiguration();
			configString = configWrap.serialize(config);
		} catch (ConfigException e) {
			// exception according to definition return false
			LOG.warn("Dialog configuration is invalid. It's assumed unchanged: ", 
					e.getLocalizedMessage());
			return false;
		} catch (Throwable e) {
			LOG.warn("Unexpected exception. Configuration is assumed to be unchanged.", e);
			return false;
		}
		
		if (lastSetConfig == null) {
			return configString == null;
		} else {
			return lastSetConfig.compareTo(configString) != 0;
		}
	}
	
	/**
	 * @return Dialog's context.
	 */
	protected ConfigDialogContext getContext() {
		return this.context;
	}
	
	/**
	 * Set dialog interface according to passed configuration. If the passed
	 * configuration is invalid ConfigException can be thrown.
	 * 
	 * @param conf Configuration object.
	 * @throws ConfigException
	 */
	protected abstract void setConfiguration(C conf) throws ConfigException;

	/**
	 * Get configuration from dialog. In case of presence invalid configuration
	 * in dialog throw ConfigException.
	 * 
	 * @return Configuration object.
	 * @throws cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException
	 */
	protected abstract C getConfiguration() throws ConfigException;
	
}
