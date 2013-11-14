package cz.cuni.mff.xrg.odcs.frontend.gui.views;

import java.util.Set;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Validator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import cz.cuni.mff.xrg.odcs.commons.app.scheduling.EmailAddress;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.NotificationRecordType;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.UserNotificationRecord;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.frontend.gui.ViewComponent;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.EmailComponent;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.EmailNotifications;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.NamespacePrefixes;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.UsersList;
import cz.cuni.mff.xrg.odcs.frontend.navigation.Address;

import org.springframework.context.annotation.Scope;

/**
 * GUI for Settings page which opens from the main menu. For User role it
 * contains Email notifications form. For Administrator role it contains extra
 * functionality: Users list, Prune execution records, Release locked pipelines
 *
 *
 * @author Maria Kukhar
 *
 */
@org.springframework.stereotype.Component
@Scope("prototype")
@Address(url = "Administrator")
public class Settings extends ViewComponent {

	private static final long serialVersionUID = 1L;
	private GridLayout mainLayout;
	private VerticalLayout accountLayout;
	private VerticalLayout notificationsLayout;
	private VerticalLayout usersLayout;
	private VerticalLayout recordsLayout;
	private VerticalLayout pipelinesLayout;
	private VerticalLayout prefixesLayout;
	private VerticalLayout tabsLayout;
	private Button notificationsButton;
	private Button accountButton;
	private Button usersButton;
	private Button recordsButton;
	private Button pipelinesButton;
	private Button prefixesButton;
	private Button shownTab = null;
	private UsersList usersList;
	private HorizontalLayout buttonBar;
	private EmailComponent email;
	private EmailNotifications emailNotifications;
	private GridLayout emailLayout;
	private NamespacePrefixes prefixesList;
	/**
	 * Currently logged in user.
	 */
	private User loggedUser;

	/**
	 *
	 * The constructor should first build the main layout, set the composition
	 * root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the visual
	 * editor.
	 */
	public Settings() {
	}

	@Override
	public boolean isModified() {

		if (shownTab.equals(notificationsButton)) {
			return areNotificationsModified();
		} else if (shownTab.equals(accountButton)) {
			return isMyAccountModified();
		}
		return false;
	}

	@Override
	public boolean saveChanges() {
		if (shownTab.equals(notificationsButton) || shownTab.equals(accountButton)) {
			return saveEmailNotifications();
		}
		return true;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		loggedUser = App.getApp().getAuthCtx().getUser();
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	private GridLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new GridLayout(2, 1);
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		// top-level component properties
		setWidth("100%");
		setHeight("100%");

		//layout with tabs
		tabsLayout = new VerticalLayout();
		tabsLayout.setWidth("100%");
		tabsLayout.setImmediate(true);

		//layout with my account components
		accountLayout = buildMyAccountLayout();

		emailNotifications = new EmailNotifications();
		emailNotifications.parentComponentUs = this;

		//layout with schedule notifications components
		notificationsLayout = buildNotificationsLayout();

		//layout with user list and user creations components
		usersLayout = new VerticalLayout();
		usersLayout.setImmediate(true);
		usersLayout.setWidth("100%");
		usersLayout.setHeight("100%");
		usersList = new UsersList();
		usersLayout = usersList.buildUsersListLayout();
		usersLayout.setStyleName("settings");

		//layout with Prune execution records
		recordsLayout = new VerticalLayout();
		recordsLayout.setMargin(true);
		recordsLayout.setSpacing(true);
		recordsLayout.setImmediate(true);
		recordsLayout.setStyleName("settings");
		recordsLayout.setWidth("100%");
		recordsLayout.addComponent(new Label("Records"));

		//layout for Delete debug resources
		pipelinesLayout = new VerticalLayout();
		pipelinesLayout.setMargin(true);
		pipelinesLayout.setSpacing(true);
		pipelinesLayout.setImmediate(true);
		pipelinesLayout.setStyleName("settings");
		pipelinesLayout.setWidth("100%");
		pipelinesLayout.addComponent(new Label("Delete all intermediate graphs created \n by the pipelines in the debug mode"));
		Button clearButton = new Button();
		clearButton.setCaption("Clear");
		pipelinesLayout.addComponent(clearButton );
		

		//layout for Namespace Prefixes
		prefixesLayout = new VerticalLayout();
		prefixesLayout.setImmediate(true);
		prefixesLayout.setWidth("100%");
		prefixesLayout.setHeight("100%");
		prefixesList = new NamespacePrefixes();
		prefixesLayout = prefixesList.buildNamespacePrefixesLayout();
		prefixesLayout.setStyleName("settings");
		
		//My account tab
		accountButton = new NativeButton("My account");
		accountButton.setHeight("40px");
		accountButton.setWidth("170px");
		accountButton.setStyleName("selectedtab");
		accountButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				//if before click was pushed Schedule notification tab
				if (shownTab.equals(notificationsButton)) {
					notificationSaveConfirmation(accountButton, accountLayout);
				} else {
					buttonPush(accountButton, accountLayout);
				}

			}
		});
		tabsLayout.addComponent(accountButton);
		tabsLayout.setComponentAlignment(accountButton, Alignment.TOP_RIGHT);


		//Scheduler notifications tab
		notificationsButton = new NativeButton("Scheduler notifications");
		notificationsButton.setHeight("40px");
		notificationsButton.setWidth("170px");
		notificationsButton.setStyleName("multiline");
		notificationsButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				//if before click was pushed My account tab
				if (shownTab.equals(accountButton)) {
					myAccountSaveConfirmation(notificationsButton,
							notificationsLayout);
				} else {
					buttonPush(notificationsButton, notificationsLayout);
				}

			}
		});
		tabsLayout.addComponent(notificationsButton);
		tabsLayout.setComponentAlignment(notificationsButton,
				Alignment.TOP_RIGHT);

		//Manage users tab
		usersButton = new NativeButton("Manage users");
		usersButton.setHeight("40px");
		usersButton.setWidth("170px");
		usersButton.setStyleName("multiline");
		usersButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				//if before click was pushed My account tab
				if (shownTab.equals(accountButton)) {
					myAccountSaveConfirmation(usersButton, usersLayout);
				} else {
					//if before click was pushed Schedule notification tab
					if (shownTab.equals(notificationsButton)) {
						notificationSaveConfirmation(usersButton, usersLayout);
					} else {
						buttonPush(usersButton, usersLayout);
					}
				}
			}
		});
		tabsLayout.addComponent(usersButton);
		tabsLayout.setComponentAlignment(usersButton, Alignment.TOP_RIGHT);

		//Prune execution records tab
		recordsButton = new NativeButton("Prune execution records");
		recordsButton.setHeight("40px");
		recordsButton.setWidth("170px");
		recordsButton.setStyleName("multiline");
		recordsButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				//if before click was pushed My account tab
				if (shownTab.equals(accountButton)) {
					myAccountSaveConfirmation(recordsButton, recordsLayout);
				} else {
					//if before click was pushed Schedule notification tab
					if (shownTab.equals(notificationsButton)) {
						notificationSaveConfirmation(recordsButton,
								recordsLayout);
					} else {
						buttonPush(recordsButton, recordsLayout);
					}
				}
			}
		});
		tabsLayout.addComponent(recordsButton);
		tabsLayout.setComponentAlignment(recordsButton, Alignment.TOP_RIGHT);

		//Delete debug resources tab
		pipelinesButton = new NativeButton("Delete debug resources");
		pipelinesButton.setHeight("40px");
		pipelinesButton.setWidth("170px");
		pipelinesButton.setStyleName("multiline");
		pipelinesButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				//if before click was pushed My account tab
				if (shownTab.equals(accountButton)) {
					myAccountSaveConfirmation(pipelinesButton, pipelinesLayout);
				} else {
					//if before click was pushed Schedule notification tab
					if (shownTab.equals(notificationsButton)) {
						notificationSaveConfirmation(pipelinesButton,
								pipelinesLayout);
					} else {
						buttonPush(pipelinesButton, pipelinesLayout);
					}

				}
			}
		});
		tabsLayout.addComponent(pipelinesButton);
		tabsLayout.setComponentAlignment(pipelinesButton, Alignment.TOP_RIGHT);

		//Namespace prefixes tab
		prefixesButton = new NativeButton("Namespace Prefixes");
		prefixesButton.setHeight("40px");
		prefixesButton.setWidth("170px");
		prefixesButton.setStyleName("multiline");
		prefixesButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (shownTab.equals(accountButton)) {
					myAccountSaveConfirmation(prefixesButton, prefixesLayout);
				} else {
					if (shownTab.equals(notificationsButton)) {
						notificationSaveConfirmation(prefixesButton,
								prefixesLayout);
					} else {
						buttonPush(prefixesButton, prefixesLayout);
					}
				}
			}
		});

		tabsLayout.addComponent(prefixesButton);
		tabsLayout.setComponentAlignment(prefixesButton, Alignment.TOP_RIGHT);


		shownTab = accountButton;
		mainLayout.addComponent(tabsLayout, 0, 0);
		mainLayout.addComponent(accountLayout, 1, 0);
		mainLayout.setColumnExpandRatio(0, 0.15f);
		mainLayout.setColumnExpandRatio(1, 0.85f);

		return mainLayout;
	}

	/**
	 * Building Schedule notifications layout. Appear after pushing Schedule
	 * notifications tab
	 *
	 * @return notificationsLayout Layout with components of Schedule
	 * notifications.
	 */
	private VerticalLayout buildNotificationsLayout() {

		notificationsLayout = new VerticalLayout();
		notificationsLayout.setWidth("100%");
		notificationsLayout.setHeight("100%");

		notificationsLayout = emailNotifications.buildEmailNotificationsLayout();
		emailNotifications.getUserNotificationRecord(loggedUser);
		notificationsLayout.setStyleName("settings");

		HorizontalLayout buttonBarNotify = buildButtonBar();
		notificationsLayout.addComponent(buttonBarNotify);

		notificationsLayout.addComponent(new Label(
				"Default form of report about scheduled pipeline execution"), 0);
		notificationsLayout.addComponent(new Label(
				"(may be overriden in the particular schedulled event) :"), 1);

		return notificationsLayout;
	}

	/**
	 * Building My account layout. Appear after pushing My account tab
	 *
	 * @return accountLayout Layout with components of My account.
	 */
	private VerticalLayout buildMyAccountLayout() {

		accountLayout = new VerticalLayout();
		accountLayout.setMargin(true);
		accountLayout.setSpacing(true);
		accountLayout.setHeight("100%");
		accountLayout.setImmediate(true);
		accountLayout.setStyleName("settings");

		email = new EmailComponent();
		emailLayout = new GridLayout();
		emailLayout.setImmediate(true);

		emailLayout = email.initializeEmailList();

		email.getUserEmailNotification(loggedUser);

		HorizontalLayout buttonBarMyAcc = buildButtonBar();

		accountLayout.addComponent(emailLayout);
		accountLayout.addComponent(buttonBarMyAcc);
		accountLayout.addComponent(new Label("Email Notifications to:"), 0);

		return accountLayout;
	}

	/**
	 * Building layout with button Save for saving notifications
	 *
	 * @return buttonBar Layout with button
	 */
	private HorizontalLayout buildButtonBar() {

		//Layout with buttons Save and Cancel
		buttonBar = new HorizontalLayout();
		buttonBar.setWidth("380px");
		buttonBar.setStyleName("dpuDetailButtonBar");
		buttonBar.setMargin(new MarginInfo(true, false, false, false));

		Button saveButton = new Button("Save");
		saveButton.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				email.saveEditedTexts();
				saveEmailNotifications();

			}
		});
		buttonBar.addComponent(saveButton);
		buttonBar.setComponentAlignment(saveButton, Alignment.BOTTOM_RIGHT);

		return buttonBar;

	}

	/**
	 * Showing active tab.
	 *
	 * @param pressedButton Tab that was pressed.
	 * @param layoutShow Layaut will be shown.
	 */
	private void buttonPush(Button pressedButton, VerticalLayout layoutShow) {

		accountButton.setStyleName("multiline");
		usersButton.setStyleName("multiline");
		recordsButton.setStyleName("multiline");
		pipelinesButton.setStyleName("multiline");
		prefixesButton.setStyleName("multiline");
		notificationsButton.setStyleName("multiline");
		shownTab = pressedButton;
		shownTab.setStyleName("selectedtab");

		mainLayout.removeComponent(1, 0);
		mainLayout.addComponent(layoutShow, 1, 0);
		mainLayout.setColumnExpandRatio(1, 0.85f);
	}

	/**
	 * Saving changes that relating to Schedule Notification.
	 */
	private boolean saveEmailNotifications() {


		if (!emailValidationText().equals("")) {
			Notification.show("Failed to save settings, reason:",
					emailValidationText(), Notification.Type.ERROR_MESSAGE);
			return false;
		}

		UserNotificationRecord notification = loggedUser.getNotification();
		if (notification != null) {

			email.setUserEmailNotification(notification);
			emailNotifications.setUserNotificatonRecord(notification);
			loggedUser.setNotification(notification);
		} else {

			UserNotificationRecord userNotificationRecord = new UserNotificationRecord();
			userNotificationRecord.setUser(loggedUser);
			emailNotifications.setUserNotificatonRecord(userNotificationRecord);
			email.setUserEmailNotification(userNotificationRecord);
			loggedUser.setNotification(userNotificationRecord);
		}
		App.getApp().getUsers().save(loggedUser);

		if (shownTab.equals(accountButton)) {
			accountLayout = buildMyAccountLayout();
			mainLayout.removeComponent(1, 0);
			mainLayout.addComponent(accountLayout, 1, 0);
		}
		return true;
	}

	/**
	 * Show confirmation window in case if user make some changes in My account
	 * tab and push anoter tab. User can save changes or discard. After that
	 * will be shown another selected tab. If there was no changes, a
	 * confirmation window will not be shown.
	 *
	 * @param pressedButton New tab that was push.
	 * @param layoutShow Layout will be shown after save/discard changes.
	 */
	private void myAccountSaveConfirmation(final Button pressedButton,
			final VerticalLayout layoutShow) {
		if (isMyAccountModified()) {

			//open confirmation dialog
			ConfirmDialog.show(UI.getCurrent(), "Unsaved changes",
					"There are unsaved changes.\nDo you wish to save them or discard?",
					"Save", "Discard changes",
					new ConfirmDialog.Listener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClose(ConfirmDialog cd) {
					if (cd.isConfirmed()) {
						saveEmailNotifications();
						accountLayout = buildMyAccountLayout();
						buttonPush(pressedButton, layoutShow);
					} else {
						accountLayout = buildMyAccountLayout();
						buttonPush(pressedButton, layoutShow);
					}
				}
			});
		} else {
			accountLayout = buildMyAccountLayout();
			buttonPush(pressedButton, layoutShow);
		}

	}

	/**
	 * Show confirmation window in case if user make some changes in Schedule
	 * notifications tab and push anoter tab. User can save changes or discard.
	 * After that will be shown another selected tab. If there was no changes, a
	 * confirmation window will not be shown.
	 *
	 * @param pressedButton New tab that was push.
	 * @param layoutShow Layout will be shown after save/discard changes.
	 */
	private void notificationSaveConfirmation(final Button pressedButton,
			final VerticalLayout layoutShow) {
		if (areNotificationsModified()) {
			//open confirmation dialog
			ConfirmDialog.show(UI.getCurrent(), "Unsaved changes",
					"There are unsaved changes.\nDo you wish to save them or discard?",
					"Save", "Discard changes",
					new ConfirmDialog.Listener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClose(ConfirmDialog cd) {
					if (cd.isConfirmed()) {
						saveEmailNotifications();
						buttonPush(pressedButton, layoutShow);
					} else {
						notificationsLayout = buildNotificationsLayout();
						buttonPush(pressedButton, layoutShow);
					}
				}
			});
		} else {
			buttonPush(pressedButton, layoutShow);
		}

	}

	private boolean areNotificationsModified() {
		if (loggedUser.getNotification() == null) {
			return true;
		}
		NotificationRecordType aldSuccessEx = loggedUser.getNotification()
				.getTypeSuccess();
		NotificationRecordType aldErrorEx = loggedUser.getNotification()
				.getTypeError();
		UserNotificationRecord newNotification = new UserNotificationRecord();
		emailNotifications.setUserNotificatonRecord(newNotification);
		NotificationRecordType newSuccessEx = newNotification.getTypeSuccess();
		NotificationRecordType newErrorEx = newNotification.getTypeError();
		return !aldSuccessEx.equals(newSuccessEx) || !aldErrorEx.equals(newErrorEx);
	}

	private boolean isMyAccountModified() {
		email.saveEditedTexts();

		if (!emailValidationText().equals("")) {
			Notification.show("", emailValidationText(),
					Notification.Type.ERROR_MESSAGE);
			return true;
		}

		UserNotificationRecord record = loggedUser.getNotification();
		if (record == null) {
			return true;
		}
		Set<EmailAddress> aldEmails = record.getEmails();
		UserNotificationRecord newNotification = new UserNotificationRecord();
		email.setUserEmailNotification(newNotification);
		Set<EmailAddress> newEmails = newNotification.getEmails();
		return !aldEmails.equals(newEmails);
	}

	private String emailValidationText() {
		String errorText = "";
		String wrongFormat = "";
		boolean notEmpty = false;
		int errorNumber = 0;
		int fieldNumber = 0;
		for (TextField emailField : email.listedEditText) {
			if (!emailField.getValue().trim().isEmpty()) {
				notEmpty = true;
				break;
			}
		}

		if (notEmpty) {
			for (TextField emailField : email.listedEditText) {
				fieldNumber++;
				try {
					emailField.validate();

				} catch (Validator.InvalidValueException e) {

					if (e.getMessage().equals("wrong е-mail format")) {
						if (fieldNumber == 1) {
							wrongFormat = "\"" + emailField.getValue() + "\"";
						} else {
							wrongFormat = wrongFormat + ", " + "\"" + emailField
									.getValue() + "\"";
						}
						errorNumber++;
					}
				}
			}
			if (errorNumber == 1) {
				errorText = "Email " + wrongFormat + " has wrong format.";
			}
			if (errorNumber > 1) {
				errorText = "Emails " + wrongFormat + ", have wrong format.";
			}
		} else {
			errorText = "At least one mail has to be filled, so that the notification can be send.";
		}


		return errorText;

	}
}
