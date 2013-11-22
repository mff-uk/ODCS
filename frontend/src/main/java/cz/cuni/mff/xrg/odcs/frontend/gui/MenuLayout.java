package cz.cuni.mff.xrg.odcs.frontend.gui;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.frontend.AuthenticationService;
import cz.cuni.mff.xrg.odcs.frontend.RequestHolder;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Initial;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Login;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Scheduler;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Settings;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.dpu.DPUPresenterImpl;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.executionlist.ExecutionListPresenterImpl;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist.PipelineListPresenterImpl;
import cz.cuni.mff.xrg.odcs.frontend.navigation.ClassNavigator;
import cz.cuni.mff.xrg.odcs.frontend.navigation.ClassNavigatorHolder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Class represent main application component. The component contains menu bar
 * and a place where to place application view.
 *
 * @author Petyr
 *
 */
public class MenuLayout extends CustomComponent {

	private static final long serialVersionUID = 1L;

	private ClassNavigator navigator;

	/**
	 * Authentication context used to render menu with respect to currently
	 * logged in user.
	 */
	@Autowired
	private AuthenticationContext authCtx;

	/**
	 * Authentication service handling logging in and out.
	 */
	@Autowired
	private AuthenticationService authService;

	/**
	 * Used layout.
	 */
	private VerticalLayout mainLayout;

	/**
	 * Menu bar.
	 */
	private MenuBar menuBar;

	/**
	 * Layout for application views.
	 */
	private Panel viewLayout;

	Label userName;

	Button logOutButton;

	Embedded backendStatus;

	/**
	 * The constructor should first build the main layout, set the composition
	 * root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the visual
	 * editor.
	 */
	public void enter() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	private VerticalLayout buildMainLayout() {
		// common part: create layout
		this.mainLayout = new VerticalLayout();
		this.mainLayout.setImmediate(false);
		this.mainLayout.setMargin(false);

		// top-level component properties
		this.setWidth("100.0%");
		//this.setSizeUndefined();

		// menuBar
		this.menuBar = new MenuBar();
		this.menuBar.setImmediate(false);
		this.menuBar.setWidth("100.0%");
		this.menuBar.setHeight("45px");
		this.menuBar.setHtmlContentAllowed(true);
		//this.mainLayout.addComponent(menuBar);

		backendStatus = new Embedded();
		backendStatus.setWidth(16, Unit.PIXELS);
		backendStatus.setHeight(16, Unit.PIXELS);
		backendStatus.setStyleName("backendStatus");

		userName = new Label(authCtx.getUsername());
		userName.setWidth(100, Unit.PIXELS);
		userName.addStyleName("username");

		logOutButton = new Button();
		logOutButton.setWidth(16, Unit.PIXELS);
		logOutButton.setVisible(authCtx.isAuthenticated());
		logOutButton.setStyleName(BaseTheme.BUTTON_LINK);
		logOutButton.setIcon(new ThemeResource("icons/logout.png"), "Log out");
		logOutButton.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				authService.logout(RequestHolder.getRequest());
				authCtx.clear();
				refreshUserBar();
				navigator.navigateTo(Login.class);
			}
		});
		HorizontalLayout menuLine = new HorizontalLayout(menuBar, userName, logOutButton, backendStatus);
		menuLine.setSpacing(true);
		menuLine.setWidth(100, Unit.PERCENTAGE);
		menuLine.setHeight(45, Unit.PIXELS);
		menuLine.addStyleName("loginPanel");
		menuLine.setComponentAlignment(menuBar, Alignment.MIDDLE_CENTER);
		menuLine.setComponentAlignment(backendStatus, Alignment.MIDDLE_CENTER);
		menuLine.setComponentAlignment(userName, Alignment.MIDDLE_CENTER);
		menuLine.setComponentAlignment(logOutButton, Alignment.MIDDLE_CENTER);
		menuLine.setExpandRatio(menuBar, 1.0f);
		this.mainLayout.addComponent(menuLine);

		// viewLayout
		this.viewLayout = new Panel();
		this.viewLayout.setStyleName("viewLayout");
		this.mainLayout.addComponent(viewLayout);

		refreshBackendStatus(false);

		return this.mainLayout;
	}

	/**
	 * Return layout for application views.
	 *
	 * @return
	 */
	public Panel getViewLayout() {
		return this.viewLayout;
	}

	public void refreshUserBar() {
		userName.setValue(authCtx.getUsername());
		logOutButton.setVisible(authCtx.isAuthenticated());
	}

	public void refreshBackendStatus(boolean isRunning) {
		backendStatus.setDescription(isRunning ? "Backend is online!" : "Backend is offline!");
		backendStatus.setSource(new ThemeResource(isRunning ? "icons/online.png" : "icons/offline.png"));
	}

	public void setNavigation(ClassNavigatorHolder navigatorHolder) {
		this.navigator = navigatorHolder;
		// init menuBar
		menuBar.addItem("<b>ODCleanStore</b>", new NavigateToCommand(Initial.class, navigator));
		menuBar.addItem("Pipelines", new NavigateToCommand(PipelineListPresenterImpl.class, navigator));
		menuBar.addItem("DPU Templates", new NavigateToCommand(DPUPresenterImpl.class, navigator));
		menuBar.addItem("Execution Monitor", new NavigateToCommand(ExecutionListPresenterImpl.class, navigator));
//		menuBar.addItem("Browse Data", new NavigateToCommand(ViewNames.DATA_BROWSER.getUrl()));
		menuBar.addItem("Scheduler", new NavigateToCommand(Scheduler.class, navigator));
		menuBar.addItem("Settings", new NavigateToCommand(Settings.class, navigator));
	}

	/**
	 * Class use as command to change sub-pages.
	 *
	 * @author Petyr
	 */
	private class NavigateToCommand implements Command {

		private static final long serialVersionUID = 1L;
		
		private Class<?> clazz;

		private ClassNavigator navigator;
		
		public NavigateToCommand(Class<?> clazz, ClassNavigator navigator) {
			this.clazz = clazz;
			this.navigator = navigator;
		}

		@Override
		public void menuSelected(MenuItem selectedItem) {
			navigator.navigateTo(this.clazz);
		}
	}
}
