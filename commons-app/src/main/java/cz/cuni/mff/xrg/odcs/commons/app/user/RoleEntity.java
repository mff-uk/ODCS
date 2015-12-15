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
package cz.cuni.mff.xrg.odcs.commons.app.user;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "role")
public class RoleEntity implements DataObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Primary key for entity.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_role")
	@SequenceGenerator(name = "seq_role", allocationSize = 1)
	@Column(name = "id")
	private Long id;

	@Column(name = "name", nullable = false, unique = true, length = 142)
	private String name;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_role_permission",
			joinColumns = { @JoinColumn(name = "role_id", referencedColumnName = "id") },
			inverseJoinColumns = { @JoinColumn(name = "permission_id", referencedColumnName = "id") })
	private Set<Permission> permissions = new HashSet<>();

	@ManyToMany(fetch = FetchType.EAGER, mappedBy="roles")
	private Set<User> users = new HashSet<>();

	@PreRemove
	public void preRemove() {
		for (User user : users) {
			user.getRoles().remove(this);
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

	public void addPermission(Permission permission) {
		if (permission != null) {
			permissions.add(permission);
			permission.getRoles().add(this);
		}
	}

	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}
}
