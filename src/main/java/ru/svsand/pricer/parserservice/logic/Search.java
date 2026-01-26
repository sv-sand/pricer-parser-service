package ru.svsand.pricer.parserservice.logic;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.sql.Timestamp;

/**
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

	public boolean isNew() {
		return id == null;
	}
}
