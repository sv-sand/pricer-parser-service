package ru.svsand.pricer.parserservice.db;

import jakarta.persistence.*;
import lombok.Data;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 28.10.2025
 */

@Entity
@Data
@Table(name = "products")
public class ProductEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "search_id")
	private SearchEntity search;

	@Column(name = "store")
	private String store;

	@Column(name = "store_product_id")
	private Long storeProductId;

	@Column(name = "store_product_link")
	private String storeProductLink;

	@Column(name = "price")
	private Double price;

	@Column(name = "user_notified")
	private boolean userNotified;

	@Version
	private Long version;
}
