package ru.svsand.pricer.parserservice.logic;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 24.10.2025
 */

@Data
@Builder
@ToString(of = {"store", "name"})
public class Product {
	private Long id;

	private String name;
	private Store store;
	private Search search;
	private Long storeProductId;
	private String storeProductLink;
	private double price;
	private boolean userNotified;

	private Long version;

	public boolean isNew() {
		return id == null;
	}
}
