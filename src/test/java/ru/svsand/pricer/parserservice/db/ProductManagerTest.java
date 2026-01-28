package ru.svsand.pricer.parserservice.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.svsand.pricer.parserservice.Data;
import ru.svsand.pricer.parserservice.logic.Product;
import ru.svsand.pricer.parserservice.logic.Search;
import ru.svsand.pricer.parserservice.logic.Store;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductManagerTest {

	@InjectMocks
	private ProductManager manager;

	@Mock
	private ProductRepository repository;

	@Test
	void findAll() {
		Store store = Store.WB;
		Search search = Data.search(store, Data.user());
		SearchDao searchDao = Data.searchDao(store, Data.userDao());
		Product product = Data.product(search);
		ProductDao productDao = Data.productDao(searchDao, store);

		// Arrange
		when(repository.findAll()).thenReturn(List.of(productDao));

		// Act
		List<Product> result = manager.findAll();

		// Assert
		assertEquals(1, result.size());
		compareProduct(product, result.get(0));
	}

	@Test
	void findAllEmpty() {
		// Arrange
		when(repository.findAll()).thenReturn(List.of());

		// Act
		List<Product> result = manager.findAll();

		// Assert
		assertEquals(0, result.size());
	}

	@Test
	void findByStoreProductId() {
		Store store = Store.WB;
		Search search = Data.search(store, Data.user());
		SearchDao searchDao = Data.searchDao(store, Data.userDao());
		Product product = Data.product(search);
		ProductDao productDao = Data.productDao(searchDao, store);

		// Arrange
		when(repository.findByStoreAndStoreProductId(any(), any())).thenReturn(List.of(productDao));

		// Act
		Product result = manager.findByStoreProductId(store, product.getStoreProductId());

		// Assert
		checkCallRepositoryFindByStoreProductId("WB", 101L);
		compareProduct(product, result);
	}

	@Test
	void findByStoreProductIdEmpty() {
		// Arrange
		when(repository.findByStoreAndStoreProductId(any(), any())).thenReturn(List.of());

		// Act
		Product result = manager.findByStoreProductId(Store.WB, 101L);

		// Assert
		assertNull(result);
		checkCallRepositoryFindByStoreProductId("WB", 101L);
	}

	@Test
	void saveAll() {
		Store store = Store.WB;
		Search search = Data.search(store, Data.user());
		SearchDao searchDao = Data.searchDao(store, Data.userDao());
		Product product = Data.product(search);
		ProductDao productDao = Data.productDao(searchDao, store);

		// Arrange
		when(repository.saveAll(any())).thenReturn(List.of(productDao));

		// Act
		List<Product> result = manager.saveAll(List.of(product));

		// Assert
		checkCallRepositorySaveAll(List.of(productDao));
		compareProduct(product, result.get(0));
	}

	@Test
	void save() {
		Store store = Store.WB;
		Search search = Data.search(store, Data.user());
		SearchDao searchDao = Data.searchDao(store, Data.userDao());
		Product product = Data.product(search);
		ProductDao productDao = Data.productDao(searchDao, store);

		// Arrange
		when(repository.save(any())).thenReturn(productDao);

		// Act
		Product result = manager.save(product);

		// Assert
		checkCallRepositorySave(productDao);
		compareProduct(product, result);
	}

	// Checks

	private void checkCallRepositorySave(ProductDao productDao) {
		ArgumentCaptor<ProductDao> captor = ArgumentCaptor.forClass(ProductDao.class);
		verify(repository, times(1)).save(captor.capture());

		compareProductDao(productDao, captor.getValue());
	}

	private void checkCallRepositorySaveAll(List<ProductDao> productDaoList) {
		@SuppressWarnings("unchecked")
		ArgumentCaptor<List<ProductDao>> captor = ArgumentCaptor.forClass(List.class);
		verify(repository, times(1)).saveAll(captor.capture());

		assertEquals(productDaoList.size(), captor.getValue().size());
		for (int i = 0; i < productDaoList.size(); i++)
			compareProductDao(productDaoList.get(i), captor.getValue().get(i));
	}

	private void checkCallRepositoryFindByStoreProductId(String store, Long storeProductId) {
		ArgumentCaptor<String> storeCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
		verify(repository, times(1))
				.findByStoreAndStoreProductId(storeCaptor.capture(), idCaptor.capture());

		assertEquals(store, storeCaptor.getValue());
		assertEquals(storeProductId, idCaptor.getValue());
	}

	private void compareProduct(Product expected, Product actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getStore(), actual.getStore());
		assertEquals(expected.getSearch(), actual.getSearch());
		assertEquals(expected.getStoreProductId(), actual.getStoreProductId());
		assertEquals(expected.getStoreProductLink(), actual.getStoreProductLink());
		assertEquals(expected.getPrice(), actual.getPrice());
		assertEquals(expected.isUserNotified(), actual.isUserNotified());
		assertEquals(expected.getVersion(), actual.getVersion());
	}

	private void compareProductDao(ProductDao expected, ProductDao actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getStore(), actual.getStore());
		assertEquals(expected.getSearch(), actual.getSearch());
		assertEquals(expected.getStoreProductId(), actual.getStoreProductId());
		assertEquals(expected.getStoreProductLink(), actual.getStoreProductLink());
		assertEquals(expected.getPrice(), actual.getPrice());
		assertEquals(expected.isUserNotified(), actual.isUserNotified());
		assertEquals(expected.getVersion(), actual.getVersion());
	}
}