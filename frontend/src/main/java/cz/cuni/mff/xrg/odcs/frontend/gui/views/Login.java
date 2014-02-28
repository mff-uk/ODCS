package cz.cuni.mff.xrg.odcs.frontend.gui.views;

import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;

import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.frontend.AppEntry;
import cz.cuni.mff.xrg.odcs.frontend.auth.AuthenticationService;
import cz.cuni.mff.xrg.odcs.frontend.RequestHolder;
import cz.cuni.mff.xrg.odcs.frontend.gui.ViewComponent;
import cz.cuni.mff.xrg.odcs.frontend.navigation.Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.TransactionException;

/**
 * LOGIN screen of application.
 *
 * @author Bogo
 */
@org.springframework.stereotype.Component
@Scope("prototype")
@Address(url = "Login")
public class Login extends ViewComponent {

	private static final Logger LOG = LoggerFactory.getLogger(Login.class);
	
    private CssLayout mainLayout;
    private VerticalLayout layout;
    private TextField login;
    private PasswordField password;
	private CheckBox rememberme;
	private Label error;
	
	@Autowired
	private AuthenticationService authService;
	@Autowired
    private AppConfig appConfiguration;

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        buildMainLayout();
        setCompositionRoot(mainLayout);
    }

    private void buildMainLayout() {
        mainLayout = new CssLayout() {
            @Override
            protected String getCss(Component c) {
                if (c instanceof VerticalLayout) {
                    return "position: absolute;"
                            + "    top:0;"
                            + "    bottom: 0;"
                            + "    left: 0;"
                            + "    right: 0;"
                            + "    margin: auto;";
                }
                return null;
            }
        };
        mainLayout.setSizeFull();
        mainLayout.setHeight(600, Unit.PIXELS);
        //setWidth("100%");
        layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        Label logo = new Label();
        logo.setValue("<h1>ODCleanStore</h1>");
        logo.setContentMode(ContentMode.HTML);
        layout.addComponent(logo);
		
		error = new Label();
		error.setStyleName("loginError");
		error.setVisible(false);
		layout.addComponent(error);

        login = new TextField("Login:");
		login.focus();
        layout.addComponent(login);

        password = new PasswordField("Password:");
        layout.addComponent(password);
		
		rememberme = new CheckBox("Remember me");
		layout.addComponent(rememberme);

        Button loginButton = new Button("Login", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                login();
            }
        });
        layout.addComponent(loginButton);
		password.addShortcutListener(new Button.ClickShortcut(loginButton, ShortcutAction.KeyCode.ENTER));
		Label info = new Label(String.format("For account creation, please contact admin at: <a href='mailto:%1$s'>%1$s</a>.", appConfiguration.getString(ConfigProperty.EMAIL_ADMIN)));
		info.setContentMode(ContentMode.HTML);
		layout.addComponent(info);
        layout.setSizeUndefined();
        mainLayout.addComponent(layout);

    }

    private void login() {
        try {
			authService.login(login.getValue(), password.getValue(), rememberme.getValue(), RequestHolder.getRequest());
			
			error.setVisible(false);

			// login is successful
			AppEntry app = (AppEntry)UI.getCurrent();
			app.getMain().refreshUserBar();
			app.navigateAfterLogin();

		} catch (AuthenticationException ex) {
			password.setValue("");
			LOG.info("Invalid credentials for username {}.", login.getValue());
			error.setValue(String.format("Invalid credentials for username %s.", login.getValue()));
			error.setVisible(true);
			error.setSizeUndefined();
		} catch (TransactionException ex) {
			password.setValue("");
			LOG.error("SQL error.", ex);
			error.setValue("Database error, check connection and configuration.");
			error.setVisible(true);
			error.setSizeUndefined();
		}
    }

	@Override
	public boolean isModified() {
		//There are no editable fields.
		return false;
	}
}
