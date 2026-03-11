package ru.svsand.pricer.parserservice.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link ProductDao} entities.
 *
 * @author sand <sve.snd@gmail.com>
 * @since 28.10.2025
 */
public interface ProductRepository extends JpaRepository<ProductDao, Long> {

	/**
	 * Finds all products that match the given store and store-specific product ID.
	 * Used for duplicate detection before saving newly parsed products.
	 *
	 * @param store          the store name (e.g. {@code "WB"})
	 * @param storeProductId the marketplace-assigned product identifier
	 * @return list of matching products; normally contains at most one element
	 */
	List<ProductDao> findByStoreAndStoreProductId(String store, Long storeProductId);
}
