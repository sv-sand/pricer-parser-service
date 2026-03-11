package ru.svsand.pricer.parserservice.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.svsand.pricer.parserservice.logic.Product;
import ru.svsand.pricer.parserservice.logic.Store;

import java.util.List;

/**
 * Service that manages persistence and retrieval of {@link Product} domain objects.
 * Handles conversion between the {@link Product} domain model and the {@link ProductDao} JPA entity.
 *
 * @author sand <sve.snd@gmail.com>
 * @since 29.10.2025
 */
@Service
public class ProductManager {

	@Autowired
	private ProductRepository repository;

	@Autowired
	private SearchManager searchManager;

	/**
	 * Returns all persisted products.
	 *
	 * @return list of all {@link Product} domain objects
	 */
	@Transactional
	public List<Product> findAll() {
		return repository.findAll()
				.stream()
				.map(ProductManager::fromDao)
				.toList();
	}

	/**
	 * Looks up an existing product by its store and store-specific product ID.
	 * Used for duplicate detection during parsing.
	 *
	 * @param store          the target marketplace
	 * @param storeProductId the marketplace-assigned product identifier
	 * @return the matching {@link Product}, or {@code null} if not found
	 */
	@Transactional
	public Product findByStoreProductId(Store store, Long storeProductId) {
		List<ProductDao> products = repository.findByStoreAndStoreProductId(store.name(), storeProductId);
		if (products.isEmpty())
			return null;

		return fromDao(products.get(0));
	}

	/**
	 * Persists a batch of products.
	 *
	 * @param products the products to save
	 * @return the saved products with their assigned database IDs
	 */
	@Transactional
	public List<Product> saveAll(List<Product> products) {
		List<ProductDao> productDaoList = products.stream()
				.map(ProductManager::toDao)
				.toList();

		return repository.saveAll(productDaoList).stream()
				.map(ProductManager::fromDao)
				.toList();
	}

	/**
	 * Persists a single product.
	 *
	 * @param product the product to save
	 * @return the saved product with its assigned database ID
	 */
	@Transactional
	public Product save(Product product) {
		ProductDao productDao = repository.save(toDao(product));
		return fromDao(productDao);
	}

	// Conversion

	/**
	 * Converts a {@link Product} domain object to a {@link ProductDao} JPA entity.
	 *
	 * @param product the domain object to convert
	 * @return the corresponding JPA entity
	 */
	public static ProductDao toDao(Product product) {
		SearchDao searchDao = SearchManager.toDao(product.getSearch());

		ProductDao productDao = new ProductDao();
		if (!product.isNew())
			productDao.setId(product.getId());

		productDao.setName(product.getName());
		productDao.setSearch(searchDao);
		productDao.setStore(product.getStore().name());
		productDao.setStoreProductId(product.getStoreProductId());
		productDao.setStoreProductLink(product.getStoreProductLink());
		productDao.setPrice(product.getPrice());
		productDao.setUserNotified(product.isUserNotified());
		productDao.setVersion(product.getVersion());

		return productDao;
	}

	/**
	 * Converts a {@link ProductDao} JPA entity to a {@link Product} domain object.
	 *
	 * @param productDao the entity to convert; may be {@code null}
	 * @return the corresponding domain object, or {@code null} if the input is {@code null}
	 */
	public static Product fromDao(ProductDao productDao) {
		if (productDao == null)
			return null;

		return Product.builder()
				.id(productDao.getId())
				.name(productDao.getName())
				.search(SearchManager.fromDao(productDao.getSearch()))
				.store(Store.valueOf(productDao.getStore()))
				.storeProductId(productDao.getStoreProductId())
				.storeProductLink(productDao.getStoreProductLink())
				.price(productDao.getPrice())
				.userNotified(productDao.isUserNotified())
				.version(productDao.getVersion())
				.build();
	}
}
