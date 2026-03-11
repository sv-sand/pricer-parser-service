package ru.svsand.pricer.parserservice.logic;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Domain model representing a user of the Pricer system.
 * Users are identified by their Telegram ID and own one or more {@link Search} configurations.
 *
 * @author sand <sve.snd@gmail.com>
 * @since 04.11.2025
 */
@Data
@Builder
@ToString(of = {"name", "id"})
@EqualsAndHashCode(of = {"id"})
public class User {
	private Long id;
	private String name;
	private Long tgId;

	private Long version;

	/**
	 * Returns {@code true} if this user has not been persisted yet (i.e. has no database ID).
	 *
	 * @return {@code true} for a transient user, {@code false} for a persisted one
	 */
	public boolean isNew() {
		return id == null;
	}
}
