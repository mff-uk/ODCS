package cz.cuni.mff.xrg.odcs.commons.app.user;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * Global notification settings used as default for all user schedules. This
 * configuration can be overridden for specific
 * {@link cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule} by
 * {@link cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleNotificationRecord}.
 *
 * @author Jan Vojt
 */
@Entity
@Table(name = "sch_usr_notification")
public class UserNotificationRecord extends NotificationRecord {
	
	/**
	 * User owning configuration.
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	/**
	 * E-mails the notification will be sent to.
	 * 
	 * <p>
	 * Emails are initialized as {@link LinkedHashSet} to preserve insertion
	 * order.
	 */
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "sch_usr_notification_email",
			joinColumns = @JoinColumn(name = "notification_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "email_id", referencedColumnName = "id"))
	private Set<EmailAddress> emails = new LinkedHashSet<>();
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * @return defensive copy of a set of emails to send notification to
	 */
	@Override
	public Set<EmailAddress> getEmails() {
		return new LinkedHashSet<>(emails);
	}

	@Override
	public void setEmails(Set<EmailAddress> emails) {
		this.emails = new LinkedHashSet<>(emails);
	}
	
	@Override
	public void addEmail(EmailAddress email) {
		this.emails.add(email);
	}
	
	@Override
	public void removeEmail(EmailAddress email) {
		this.emails.remove(email);
	}

}
