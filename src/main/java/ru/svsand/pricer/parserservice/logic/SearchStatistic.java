package ru.svsand.pricer.parserservice.logic;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * Domain model representing the outcome of a single parser execution for a {@link Search}.
 * Captures the HTTP status code returned by the marketplace API, a description, the number
 * of raw products found, and the time the request was made.
 *
 * @author sand <sve.snd@gmail.com>
 * @since 07.11.2025
 */
@Data
@Builder
@ToString(of = {"id", "search", "statusCode"})
@EqualsAndHashCode(of = {"id"})
public class SearchStatistic {
	private Long id;
	private Search search;
	private int statusCode;
	private String statusDescription;
	private int count;
	private Timestamp timestamp;

	private Long version;

	/**
	 * Returns {@code true} if this statistic record has not been persisted yet (i.e. has no database ID).
	 *
	 * @return {@code true} for a transient record, {@code false} for a persisted one
	 */
	public boolean isNew() {
		return id == null;
	}
}
