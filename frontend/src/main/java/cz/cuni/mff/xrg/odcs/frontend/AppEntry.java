package cz.cuni.mff.xrg.odcs.frontend;

import com.github.wolfie.refresher.Refresher;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.DefaultErrorHandler;
import static com.vaadin.server.DefaultErrorHandler.doDefault;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.commons.app.communication.Client;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.frontend.auth.AuthenticationService;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.DecorationHelper;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.RefreshManager;
import cz.cuni.mff.xrg.odcs.frontend.gui.MenuLayout;
import cz.cuni.mff.xrg.odcs.frontend.gui.ModifiableComponent;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Initial;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Login;
import cz.cuni.mff.xrg.odcs.frontend.navigation.ClassNavigator;
import cz.cuni.mff.xrg.odcs.frontend.navigation.ClassNavigatorHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.dialogs.DefaultConfirmDialogFactory;
import virtuoso.jdbc4.VirtuosoException;

import cz.cuni.mff.xrg.odcs.frontend.navigation.ClassNavigatorImpl;

/**
 * Frontend application entry point. Also provide access to the application
 * services like database connection. To access the class use
 * ((AppEntry)UI.getCurrent()).
 *
 * @author Petyr
 *
 */
@Theme("OdcsTheme")
public class AppEntry extends com.vaadin.ui.UI {

	private static final Logger LOG = LoggerFactory.getLogger(AppEntry.class);
	/**
	 * Spring application context.
	 */
	@Autowired
	private ApplicationContext context;
	@Autowired
	private MenuLayout main;
	@Autowired
	private ClassNavigatorHolder navigatorHolder;
	private Client backendClient;
	private RefreshManager refreshManager;
	private String storedNavigation = null;
	private String lastView = null;
	private String actualView = null;
	
	@Autowired
    private AppConfig appConfiguration;
	
	@Autowired
	private AuthenticationContext authCtx;
	
	@Autowired
	private AuthenticationService authService;

	@Override
	protected void init(com.vaadin.server.VaadinRequest request) {

		// create main application uber-view and set it as app. content
		// in panel, for possible vertical scrolling
		//main = new MenuLayout();
		main.enter();
		setContent(main);

		// create a navigator to control the views
		// and set it into the navigator holder
		ClassNavigatorImpl navInstance = new ClassNavigatorImpl(this, main.getViewLayout(), context);
		((ClassNavigatorHolder) navigatorHolder).setNavigator(navInstance);
		main.setNavigation(navigatorHolder);

		ConfirmDialog.Factory df = new DefaultConfirmDialogFactory() {
			// We change the default order of the buttons
			@Override
			public ConfirmDialog create(String caption, String message,
					String okCaption, String cancelCaption) {
				ConfirmDialog d = super.create(caption, message,
						okCaption,
						cancelCaption);

				LOG.info("Dialog info: w:{} {} h:{} {} cap:{}", d.getWidth(),
						d.getWidthUnits(), d.getHeight(), d.getHeightUnits(),
						caption != null ? caption.length() : 0);

				// we inceare by 1.5 .. so prevent from creating 
				// unecesary scroll bars on some resolutions and zoom levels
				// also it should not do somehing bad as at the end the dialog
				// is just litle bit heigher then originally
				d.setHeight(d.getHeight() + 1.5f, d.getHeightUnits());
				if (caption != null && caption.length() > 30) {
					// adjust the width as there is not enough space for text
					d.setWidth(caption.length() * 0.73f, d.getWidthUnits());
				}

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

		this.addDetachListener(new DetachListener() {
			@Override
			public void detach(DetachEvent event) {
				if (backendClient != null) {
					backendClient.close();
				}
			}
		});

		ErrorHandler errorHandler = new DefaultErrorHandler() {
			@Override
			public void error(com.vaadin.server.ErrorEvent event) {
				Throwable cause = DecorationHelper.findFinalCause(event.getThrowable());
				if (cause != null) {
					if (cause.getClass() == VirtuosoException.class && ((VirtuosoException) cause).getErrorCode() == VirtuosoException.IOERROR && cause.getMessage().contains("Connection refused")) {
						Notification.show("Cannot connect to database!", "Please make sure that the database is running and properly configured.", Type.ERROR_MESSAGE);
						return;
					}

					// Display the error message in a custom fashion
					//String text = String.format("Exception: %s, Source: %s", cause.getClass().getName(), cause.getStackTrace().length > 0 ? cause.getStackTrace()[0].toString() : "unknown");
					//Notification.show(cause.getMessage(), text, Type.ERROR_MESSAGE);
					Notification.show("Unexpected error occured.", "Please reload the application.", Type.ERROR_MESSAGE);
					// and log ...
					LOG.error("Uncaught exception", cause);
				} else {
					// Do the default error handling (optional)
					doDefault(event);
				}
			}
		};
		// Configure the error handler for the UI
		VaadinSession.getCurrent().setErrorHandler(errorHandler);
		this.setErrorHandler(errorHandler);

		/**
		 * Checking user every time request is made.
		 */
		navInstance.addViewChangeListener(new ViewChangeListener() {
			@Override
			public boolean beforeViewChange(ViewChangeListener.ViewChangeEvent event) {
				main.refreshUserBar();

				// TODO adjust this once Login screen will be presenters 
				//	to event.getNewView().equals(Login.class)
				if (!(event.getNewView() instanceof Login)
						&& !authCtx.isAuthenticated()
						&& !authService.tryRememberMeLogin(RequestHolder.getRequest())) {
					
					storedNavigation = event.getViewName();
					String parameters = event.getParameters();
					if (parameters != null) {
						storedNavigation += "/" + parameters;
					}
					navigatorHolder.navigateTo(Login.class);
					getMain().refreshUserBar();
					return false;
				}
				setNavigationHistory(event);

				refreshManager.removeListener(RefreshManager.EXECUTION_MONITOR);
				refreshManager.removeListener(RefreshManager.DEBUGGINGVIEW);
				refreshManager.removeListener(RefreshManager.PIPELINE_LIST);
				refreshManager.removeListener(RefreshManager.SCHEDULER);
				refreshManager.removeListener(RefreshManager.PIPELINE_EDIT);
				
				return true;
			}

			@Override
			public void afterViewChange(ViewChangeListener.ViewChangeEvent event) {
				main.setActiveMenuItem(event.getViewName());
			}
		});

		// attach a listener so that we'll get asked isViewChangeAllowed?
		navInstance.addViewChangeListener(new ViewChangeListener() {
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
							navigatorHolder.navigateTo(pendingViewAndParameters);
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

		backendClient = new Client(
				appConfiguration.getString(ConfigProperty.BACKEND_HOST),
				appConfiguration.getInteger(ConfigProperty.BACKEND_PORT));

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
				// LOG.debug("Backend status refreshed.");
			}
		});
	}

	/**
	 * Return to page which user tried to accessed before redirecting to login page.
	 */
	public void navigateAfterLogin() {
		if (storedNavigation == null) {
			navigatorHolder.navigateTo(Initial.class);
		} else {
			String navigationTarget = storedNavigation;
			storedNavigation = null;
			navigatorHolder.navigateTo(navigationTarget);
		}
	}

	private void setNavigationHistory(ViewChangeListener.ViewChangeEvent event) {
		lastView = actualView;
		actualView = event.getViewName();
		if (event.getParameters() != null) {
			actualView += "/" + event.getParameters();
		}
	}

	/**
	 * Navigate to previous view.
	 */
	public void navigateToLastView() {
		if (lastView != null) {
			navigatorHolder.navigateTo(lastView);
		} else {
			navigatorHolder.navigateTo("");
		}
	}

	/**
	 * Get current navigation.
	 * @return Navigator.
	 */
	public ClassNavigator getNavigation() {
		return navigatorHolder;
	}

	/**
	 * Fetches spring bean. For cases when auto-wiring is not a possibility.
	 *
	 * @param <T> 
	 * @param type Class of the bean to fetch.
	 * @return bean
	 */
	public <T extends Object> T getBean(Class<T> type) {
		return context.getBean(type);
	}

	/**
	 * Get main layout.
	 * @return Main layout.
	 */
	public MenuLayout getMain() {
		return main;
	}

	/**
	 * Get backend client.
	 * @return Backend client.
	 */
	public Client getBackendClient() {
		return backendClient;
	}

	/**
	 * Get refresh manager.
	 * @return Refresh manager.
	 */
	public RefreshManager getRefreshManager() {
		return refreshManager;
	}

	/**
	 * Set URI fragment.
	 * @param uriFragment New URI fragment.
	 * @param throwEvents True to fire event.
	 */
	public void setUriFragment(String uriFragment, boolean throwEvents) {
		Page.getCurrent().setUriFragment(uriFragment, throwEvents);
		if(uriFragment.length() > 0) {
			actualView = uriFragment.substring(1);
		}
	}
}
