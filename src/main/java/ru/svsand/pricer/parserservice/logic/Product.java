package ru.svsand.pricer.parserservice.logic;

import lombok.*;

/**
 * Domain model representing a marketplace product discovered during a price search.
 * Tracks the product's price, its origin store, the search that found it,
 * and whether the owning user has already been notified about it.
 *
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

	/**
	 * Returns {@code true} if this product has not been persisted yet (i.e. has no database ID).
	 *
	 * @return {@code true} for a transient product, {@code false} for a persisted one
	 */
	public boolean isNew() {
		return id == null;
	}
}
