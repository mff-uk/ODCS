package cz.cuni.mff.xrg.odcs.frontend;

import com.github.wolfie.refresher.Refresher;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;

import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.commons.app.communication.Client;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUExplorer;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUFacade;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.LogFacade;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUModuleManipulator;
import cz.cuni.mff.xrg.odcs.commons.app.module.ModuleFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.rdf.namespace.NamespacePrefixFacade;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleFacade;
import cz.cuni.mff.xrg.odcs.commons.app.user.UserFacade;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.IntlibHelper;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.RefreshManager;
import cz.cuni.mff.xrg.odcs.frontend.gui.MenuLayout;
import cz.cuni.mff.xrg.odcs.frontend.gui.ModifiableComponent;
import cz.cuni.mff.xrg.odcs.frontend.gui.ViewNames;
//import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.RefreshThread;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.dialogs.DefaultConfirmDialogFactory;
import ru.xpoft.vaadin.DiscoveryNavigator;

/**
 * Frontend application entry point. Also provide access to the application
 * services like database connection. To access the class use
 * ((AppEntry)UI.getCurrent()).
 *
 * @author Petyr
 *
 */
@Push(PushMode.AUTOMATIC)
@Theme("IntLibTheme")
public class AppEntry extends com.vaadin.ui.UI {

	private static final Logger LOG = LoggerFactory.getLogger(AppEntry.class);
	/**
	 * Used to resolve URL request and select active view.
	 */
	private com.vaadin.navigator.Navigator navigator;
	/**
	 * Spring application context.
	 */
	@Autowired
	private ApplicationContext context;
	private MenuLayout main;
	private Date lastAction = null;
	private Client backendClient;
	private RefreshManager refreshManager;
	private String storedNavigation = null;
	private String lastView = null;
	private String actualView = null;

	@Override
	protected void init(com.vaadin.server.VaadinRequest request) {

		// create main application uber-view and set it as app. content
		// in panel, for possible vertical scrolling
		main = new MenuLayout();
		setContent(main);

		ConfirmDialog.Factory df = new DefaultConfirmDialogFactory() {
			// We change the default order of the buttons
			@Override
			public ConfirmDialog create(String caption, String message,
					String okCaption, String cancelCaption) {
				ConfirmDialog d = super.create(caption, message,
						okCaption,
						cancelCaption);

				// Change the order of buttons
				d.setContentMode(ConfirmDialog.ContentMode.TEXT);

				Button ok = d.getOkButton();
				ok.setWidth(120, Unit.PIXELS);
				HorizontalLayout buttons = (HorizontalLayout) ok.getParent();
				buttons.removeComponent(ok);
				buttons.addComponent(ok, 1);
				buttons.setComponentAlignment(ok, Alignment.MIDDLE_RIGHT);

				return d;
			}
		};
		ConfirmDialog.setFactory(df);

		// create a navigator to control the views
		this.navigator = new DiscoveryNavigator(this, main.getViewLayout());

		this.addDetachListener(new DetachListener() {
			@Override
			public void detach(DetachEvent event) {
//				if (refreshThread != null) {
//					refreshThread.interrupt();
//				}
				if (backendClient != null) {
					backendClient.close();
				}

			}
		});

		// Configure the error handler for the UI
		this.setErrorHandler(new DefaultErrorHandler() {
			@Override
			public void error(com.vaadin.server.ErrorEvent event) {
				Throwable cause = IntlibHelper.findFinalCause(event.getThrowable());
				if (cause != null) {
//					if(cause.getClass() == VirtuosoException.class && ((VirtuosoException)cause).getErrorCode() == VirtuosoException.IOERROR) {
//						Notification.show("Cannot connect to database!", "Please make sure that the database is running and properly configured.", Type.ERROR_MESSAGE);
//						return;
//					}

					// Display the error message in a custom fashion
					String text = String.format("Exception: %s, Source: %s", cause.getClass().getName(), cause.getStackTrace().length > 0 ? cause.getStackTrace()[0].toString() : "unknown");
					Notification.show(cause.getMessage(), text, Notification.Type.ERROR_MESSAGE);
					// and log ...
					LOG.error("Uncaught exception", cause);
				} else {
					// Do the default error handling (optional)
					doDefault(event);
				}
			}
		});

		/**
		 * Checking user every time request is made.
		 */
		this.getNavigator().addViewChangeListener(new ViewChangeListener() {
			@Override
			public boolean beforeViewChange(ViewChangeListener.ViewChangeEvent event) {
				if (!event.getViewName().equals(ViewNames.LOGIN.getUrl()) && !checkAuthentication()) {
					storedNavigation = event.getViewName();
					String parameters = event.getParameters();
					if (parameters != null) {
						storedNavigation += "/" + parameters;
					}
					getNavigator().navigateTo(ViewNames.LOGIN.getUrl());
					getMain().refreshUserBar();
					return false;
				}
				setNavigationHistory(event);
				setActive();
//				if(refreshThread == null || !refreshThread.isAlive()) {
//					setupRefreshThread();
//					LOG.debug("Starting new refresh thread.");
//				}

				if (!event.getViewName().equals(ViewNames.EXECUTION_MONITOR.getUrl())) {
					refreshManager.removeListener(RefreshManager.EXECUTION_MONITOR);
					refreshManager.removeListener(RefreshManager.DEBUGGINGVIEW);
					//refreshThread.refreshExecution(null, null);
				}
				return true;
			}

			@Override
			public void afterViewChange(ViewChangeListener.ViewChangeEvent event) {
			}
		});

		// attach a listener so that we'll get asked isViewChangeAllowed?
		this.getNavigator().addViewChangeListener(new ViewChangeListener() {
			private String pendingViewAndParameters;
			private ModifiableComponent lastView;
			boolean forceViewChange = false;

			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {
				if (forceViewChange) {
					forceViewChange = false;
					pendingViewAndParameters = null;
					return true;
				}

				if (event.getOldView() instanceof ModifiableComponent
						&& ((ModifiableComponent) event.getOldView()).isModified()) {

					// save the View where the user intended to go
					lastView = (ModifiableComponent) event.getOldView();
					pendingViewAndParameters = event.getViewName();
					if (event.getParameters() != null) {
						pendingViewAndParameters += "/";
						pendingViewAndParameters += event
								.getParameters();
					}

					// Prompt the user to save or cancel if the name is changed
					ConfirmDialog.show(getUI(), "Unsaved changes", "There are unsaved changes.\nDo you wish to save them or discard?", "Save", "Discard changes", new ConfirmDialog.Listener() {
						@Override
						public void onClose(ConfirmDialog cd) {
							if (cd.isConfirmed()) {
								if (!lastView.saveChanges()) {
									return;
								}
							} else {
								forceViewChange = true;
							}
							navigator.navigateTo(pendingViewAndParameters);
						}
					});
					//Notification.show("Please apply or cancel your changes", Type.WARNING_MESSAGE);

					return false;
				} else {
					return true;
				}
			}

			@Override
			public void afterViewChange(ViewChangeEvent event) {
				pendingViewAndParameters = null;
			}
		});

		AppConfig config = getAppConfiguration();
		backendClient = new Client(
				config.getString(ConfigProperty.BACKEND_HOST),
				config.getInteger(ConfigProperty.BACKEND_PORT));

		Refresher refresher = new Refresher();
		refresher.setRefreshInterval(5000);
		addExtension(refresher);
		refreshManager = new RefreshManager(refresher);
		refreshManager.addListener(RefreshManager.BACKEND_STATUS, new Refresher.RefreshListener() {
			private boolean lastBackendStatus = false;

			@Override
			public void refresh(Refresher source) {
				boolean isRunning = getBackendClient().checkStatus();
				if (lastBackendStatus != isRunning) {
					lastBackendStatus = isRunning;
					main.refreshBackendStatus(lastBackendStatus);
				}
				LOG.debug("Backend status refreshed.");
			}
		});
	}

	public void navigateAfterLogin() {
		if (storedNavigation == null) {
			getNavigator().navigateTo(ViewNames.INITIAL.getUrl());
		} else {
			String navigationTarget = storedNavigation;
			storedNavigation = null;
			getNavigator().navigateTo(navigationTarget);
		}
	}

	/**
	 * Checks if there is logged in user and if its session is still valid.
	 *
	 * @return true if user and its session are valid, false otherwise
	 */
	private boolean checkAuthentication() {
		return getAuthCtx().isAuthenticated();
	}

	private void setNavigationHistory(ViewChangeListener.ViewChangeEvent event) {
		lastView = actualView;
		actualView = event.getViewName();
		if(event.getParameters() != null) {
			actualView += "/" + event.getParameters();
		}
	}
	
	public void navigateToLastView() {
		if(lastView != null) {
			navigator.navigateTo(lastView);
		} else {
			navigator.navigateTo("");
		}
	}

	/**
	 * Sets last action date to current time.
	 */
	public void setActive() {
		lastAction = new Date();
	}

	/**
	 * Returns facade, which provides services for managing pipelines.
	 *
	 * @return pipeline facade
	 */
	public PipelineFacade getPipelines() {
		return (PipelineFacade) context.getBean("pipelineFacade");
	}

	/**
	 * Return application navigator.
	 *
	 * @return application navigator
	 */
	@Override
	public Navigator getNavigator() {
		return this.navigator;
	}

	/**
	 * Return facade, which provide services for manipulating with modules.
	 *
	 * @return modules facade
	 */
	public ModuleFacade getModules() {
		return (ModuleFacade) context.getBean("moduleFacade");
	}

	/**
	 * Return facade, which provide services for manipulating with DPUs.
	 *
	 * @return dpus facade
	 */
	public DPUFacade getDPUs() {
		return (DPUFacade) context.getBean("dpuFacade");
	}

	/**
	 * Return facade, which provide services for manipulating with Schedules.
	 *
	 * @return schedules facade
	 */
	public ScheduleFacade getSchedules() {
		return (ScheduleFacade) context.getBean("scheduleFacade");
	}

	/**
	 * Return facade, which provide services for manipulating with Schedules.
	 *
	 * @return schedules facade
	 */
	public UserFacade getUsers() {
		return (UserFacade) context.getBean("userFacade");
	}

	/**
	 * Return facade, which provide services for manipulating with Namespace
	 * Prefix.
	 *
	 * @return NamespascePrefix facade
	 */
	public NamespacePrefixFacade getNamespacePrefixes() {
		return (NamespacePrefixFacade) context.getBean("prefixFacade");
	}

	/**
	 * Return facade, which provide services for manipulating with Logs.
	 *
	 * @return log facade
	 */
	public LogFacade getLogs() {
		return (LogFacade) context.getBean("logFacade");
	}

	/**
	 * Return application configuration class.
	 *
	 * @return
	 */
	public AppConfig getAppConfiguration() {
		return (AppConfig) context.getBean("configuration");
	}

	/**
	 * Return class that can be used to explore DPUs.
	 *
	 * @return
	 */
	public DPUExplorer getDPUExplorere() {
		return (DPUExplorer) context.getBean(DPUExplorer.class);
	}

	/**
	 * Fetches spring bean.
	 *
	 * @param name
	 * @return bean
	 * @deprecated use {@link #getBean(java.lang.Class) instead
	 */
	@Deprecated
	public Object getBean(String name) {
		return context.getBean(name);
	}

	/**
	 * Fetches spring bean.
	 *
	 * @param type
	 * @return bean
	 */
	public <T extends Object> T getBean(Class<T> type) {
		return context.getBean(type);
	}

	public MenuLayout getMain() {
		return main;
	}

	/**
	 * Helper method for retrieving authentication context.
	 *
	 * @return authentication context for current user session
	 */
	public AuthenticationContext getAuthCtx() {
		return getBean(AuthenticationContext.class);
	}

	public DPUModuleManipulator getDPUManipulator() {
		return getBean(DPUModuleManipulator.class);
	}

	/**
	 * Gets time of last action.
	 *
	 * @return Time of last action.
	 */
	public Date getLastAction() {
		return lastAction;
	}

	public Client getBackendClient() {
		return backendClient;
	}

	public RefreshManager getRefreshManager() {
		return refreshManager;
	}
}