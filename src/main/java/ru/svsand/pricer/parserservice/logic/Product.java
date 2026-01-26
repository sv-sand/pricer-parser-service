package ru.svsand.pricer.parserservice.logic;

import lombok.*;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 24.10.2025
 */

@Data
@Builder
@AllArgsConstructor()
@ToString(of = {"store", "name"})
@EqualsAndHashCode(of = {"id"})
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
