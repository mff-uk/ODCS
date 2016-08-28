package cz.cuni.mff.xrg.odcs.commons.app.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;

/**
 * An abstract representation of an email address.
 *
 * @author Jan Vojt
 */
@Entity
@Table(name = "sch_email")
public class EmailAddress implements DataObject, Comparable<Object> {
	
	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_sch_email")
	@SequenceGenerator(name = "seq_sch_email", allocationSize = 1)
	private Long id;

	/**
	 * Email address
	 */
	@Column(name = "email")
	private String email;
	
	/**
	 * Default constructor for JPA.
	 */
	public EmailAddress() {
	}
	
	/**
	 * Create an <code>EmailAddress</code>.
	 * 
	 * @param addressAsText a full email address
	 * @throws MalformedEmailAddressException if the parameter is not a full
	 * email address.
	 */
	public EmailAddress(String addressAsText) throws MalformedEmailAddressException {
		email = addressAsText;
	}

	/**
	 * @return the email in the <code>EmailAddress</code>
	 */
	public String getEmail() {
		return email;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(1715616541, 3455617).append(id).append(email).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		EmailAddress rhs = (EmailAddress) obj;
		return new EqualsBuilder().append(id, rhs.getId())
				.append(email, rhs.getEmail()).isEquals();
	}

	/**
	 * <b>Part of
	 * <code>Comparable</code> interface. Sorts alphabetically.
	 * 
	 * @param obj
	 */
	@Override
	public int compareTo(Object obj) {
		EmailAddress ea = (EmailAddress) obj;
		return new CompareToBuilder().append(this.email, ea.getEmail())
				.append(this.id, ea.getId()).toComparison();
	}

	@Override
	public String toString() {
		return email;
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}