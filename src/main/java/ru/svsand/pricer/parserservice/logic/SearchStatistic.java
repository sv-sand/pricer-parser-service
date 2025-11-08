package ru.svsand.pricer.parserservice.logic;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 07.11.2025
 */

@Data
@Builder
@ToString(of = {"id", "search", "statusCode"})
public class SearchStatistic {
	private Long id;
	private Search search;
	private int statusCode;
	private String statusDescription;
	private int count;

	private Long version;

	public boolean isNew() {
		return id == null;
	}
}
