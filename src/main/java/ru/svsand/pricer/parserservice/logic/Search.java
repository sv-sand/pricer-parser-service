package ru.svsand.pricer.parserservice.logic;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * Domain model representing a user-defined price search configuration.
 * Defines the marketplace, search keywords, and the maximum acceptable price.
 * The {@code lastRequestDate} field is updated after each parsing run to implement
 * a one-hour cooldown before the same search is processed again.
 *
 * @author sand <sve.snd@gmail.com>
 * @since 29.10.2025
 */
@Data
@Builder
@ToString(of = {"store", "keyWords"})
@EqualsAndHashCode(of = {"id"})
public class Search {
	private Long id;

	private Store store;
	private String keyWords;
	private double targetPrice;
	private User user;
	private Timestamp lastRequestDate;

	private Long version;

	/**
	 * Returns {@code true} if this search has not been persisted yet (i.e. has no database ID).
	 *
	 * @return {@code true} for a transient search, {@code false} for a persisted one
	 */
	public boolean isNew() {
		return id == null;
	}
}
