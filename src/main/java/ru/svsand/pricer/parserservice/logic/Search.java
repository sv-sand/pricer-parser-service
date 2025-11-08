package ru.svsand.pricer.parserservice.logic;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 29.10.2025
 */

@Data
@Builder
@ToString(of = {"store", "keyWords"})
public class Search {
	private Long id;

	private Store store;
	private String keyWords;
	private double targetPrice;
	private User user;

	private Long version;

	public boolean isNew() {
		return id == null;
	}
}
