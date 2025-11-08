package ru.svsand.pricer.parserservice.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.svsand.pricer.parserservice.logic.Product;
import ru.svsand.pricer.parserservice.logic.Store;

import java.util.List;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 29.10.2025
 */

@Service
public class ProductManager {

	@Autowired
	private ProductRepository repository;

	@Transactional
	public List<Product> findAll() {
		return repository.findAll()
				.stream()
				.map(ProductManager::map)
				.toList();
	}

	@Transactional
	public Product findByStoreProductId(Store store, Long storeProductId) {
		List<ProductEntity> products = repository.findByStoreAndStoreProductId(store.name(), storeProductId);
		if (products.isEmpty())
			return null;

		return ProductManager.map(products.get(0));
	}

	@Transactional
	public void saveAll(List<Product> products) {
		List<ProductEntity> entities = products.stream()
				.map(ProductManager::map)
				.toList();
		repository.saveAll(entities);
	}

	@Transactional
	public void save(Product product) {
		repository.save(map(product));
	}

	// Conversion

	public static ProductEntity map(Product product) {
		SearchEntity searchEntity = SearchManager.map(product.getSearch());

		ProductEntity productEntity = new ProductEntity();
		if (!product.isNew())
			productEntity.setId(product.getId());

		productEntity.setName(product.getName());
		productEntity.setSearch(searchEntity);
		productEntity.setStore(product.getStore().name());
		productEntity.setStoreProductId(product.getStoreProductId());
		productEntity.setStoreProductLink(product.getStoreProductLink());
		productEntity.setPrice(product.getPrice());
		productEntity.setUserNotified(product.isUserNotified());
		productEntity.setVersion(product.getVersion());

		return productEntity;
	}

	public static Product map(ProductEntity productEntity) {
		if (productEntity == null)
			return null;

		return Product.builder()
				.id(productEntity.getId())
				.name(productEntity.getName())
				.search(SearchManager.map(productEntity.getSearch()))
				.store(Store.valueOf(productEntity.getStore()))
				.storeProductId(productEntity.getStoreProductId())
				.storeProductLink(productEntity.getStoreProductLink())
				.price(productEntity.getPrice())
				.userNotified(productEntity.isUserNotified())
				.version(productEntity.getVersion())
				.build();
	}
}
