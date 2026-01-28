package ru.svsand.pricer.parserservice.logic;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.svsand.pricer.parserservice.Data;
import ru.svsand.pricer.parserservice.db.ProductManager;
import ru.svsand.pricer.parserservice.db.SearchManager;
import ru.svsand.pricer.parserservice.db.SearchStatisticManager;
import ru.svsand.pricer.parserservice.logic.parser.Parser;
import ru.svsand.pricer.parserservice.logic.parser.ParserManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParserServiceTest {

	@InjectMocks
	private ParserService parserService;

	@Mock
	private SearchManager searchManager;

	@Mock
	private ProductManager productManager;

	@Mock
	private SearchStatisticManager searchStatisticManager;

	@Mock
	private Parser parser;

	private static MockedStatic<ParserManager> mockedParserManager;

	@BeforeAll
	static void beforeAll() {
		mockedParserManager = Mockito.mockStatic(ParserManager.class);
	}

	@AfterAll
	static void afterAll() {
		mockedParserManager.close();
	}

	@Test
	void updateProducts() {
		Store store = Store.WB;
		Search search = Data.search(store, Data.user());
		SearchStatistic searchStatistic = Data.searchStatistic(search);
		searchStatistic.setCount(4);
		List<Product> products = Data.products(search);
		Parser.Result parserResult = new Parser.Result(200, "OK", Data.parsedProducts());

		// Arrange
		when(ParserManager.createParserByStore(any())).thenReturn(parser);
		when(searchManager.save(any())).thenReturn(search);
		when(searchStatisticManager.save(any())).thenReturn(Data.searchStatistic(search));
		when(productManager.saveAll(any())).thenReturn(products);

		when(searchManager.findAllForRequest()).thenReturn(List.of(search));
		when(parser.findProducts(anyString())).thenReturn(parserResult);

		for (Product product : products)
			when(productManager.findByStoreProductId(store, product.getStoreProductId())).thenReturn(product);

		// Act
		parserService.updateProducts();

		// Assert
		checkCallSearchManagerFindAllForRequest();
		checkCallSearchManagerSave(List.of(search));
		checkCallParserFindProducts("test product");
		checkCallProductManagerSaveAll(products);
		checkCallProductManagerFindByStoreProduct(store, products);
		checkCallSearchStatisticManagerSave(searchStatistic);
	}

	@Test
	void updateProductsNoResults() {
		// Arrange
		when(searchManager.findAllForRequest()).thenReturn(new ArrayList<>());

		// Act
		parserService.updateProducts();

		// Assert
		checkCallSearchManagerFindAllForRequest();
	}

	@Test
	void updateProductsParserException() {
		Store store = Store.WB;
		User user = Data.user();
		Search search = Data.search(store, user);

		// Arrange
		when(searchManager.findAllForRequest()).thenReturn(List.of(search));
		when(ParserManager.createParserByStore(any())).thenReturn(parser);
		when(parser.findProducts(anyString())).thenThrow(new RuntimeException("Test exception"));

		// Act
		parserService.updateProducts();

		// Assert
		checkCallSearchManagerFindAllForRequest();
	}

	@Test
	void updateProductsResult404() {
		Store store = Store.WB;
		Search search = Data.search(store, Data.user());
		SearchStatistic searchStatistic = new SearchStatistic(1L, search, 404, "Not found", 0, Data.currentTimeTillMinutes(), 101L);
		List<Product> products = new ArrayList<>();
		Parser.Result parserResult = new Parser.Result(404, "Not found", new ArrayList<>());

		// Arrange
		when(ParserManager.createParserByStore(any())).thenReturn(parser);
		when(searchManager.save(any())).thenReturn(search);
		when(searchStatisticManager.save(any())).thenReturn(Data.searchStatistic(search));
		when(productManager.saveAll(any())).thenReturn(products);

		when(searchManager.findAllForRequest()).thenReturn(List.of(search));
		when(parser.findProducts(anyString())).thenReturn(parserResult);

		// Act
		parserService.updateProducts();

		// Assert
		checkCallSearchManagerSave(List.of(search));
		checkCallSearchStatisticManagerSave(searchStatistic);
		checkCallProductManagerSaveAll(products);

		checkCallSearchManagerFindAllForRequest();
		checkCallParserFindProducts("test product");
		checkCallProductManagerFindByStoreProduct(store, products);
	}

	// Checks

	private void checkCallSearchManagerFindAllForRequest() {
		verify(searchManager, times(1)).findAllForRequest();
	}

	private void checkCallSearchManagerSave(List<Search> searches) {
		ArgumentCaptor<Search> captor = ArgumentCaptor.forClass(Search.class);
		verify(searchManager, times(1)).save(captor.capture());
		List<Search> savedSearches = captor.getAllValues();

		assertEquals(searches.size(), savedSearches.size());
		assertEquals(searches, savedSearches);
	}

	private void checkCallParserFindProducts(String productKeyWords) {
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(parser, times(1)).findProducts(captor.capture());

		assertEquals(productKeyWords, captor.getValue());
	}

	private void checkCallProductManagerSaveAll(List<Product> products) {
		@SuppressWarnings("unchecked")
		ArgumentCaptor<List<Product>> captor = ArgumentCaptor.forClass(List.class);
		verify(productManager, times(1)).saveAll(captor.capture());
		List<Product> savedProducts = captor.getValue();

		assertEquals(products.size(), savedProducts.size());

		for (int i = 0; i < products.size(); i++) {
			assertEquals(products.get(i).getId(), savedProducts.get(i).getId());
			assertEquals(products.get(i).getName(), savedProducts.get(i).getName());
			assertEquals(products.get(i).getStoreProductLink(), savedProducts.get(i).getStoreProductLink());
			assertEquals(products.get(i).getPrice(), savedProducts.get(i).getPrice());
		}
	}

	private void checkCallProductManagerFindByStoreProduct(Store store, List<Product> relevantProducts) {
		List<Long> storeProductIds = relevantProducts.stream()
				.map(Product::getStoreProductId)
				.toList();

		ArgumentCaptor<Store> storeCaptor = ArgumentCaptor.forClass(Store.class);
		ArgumentCaptor<Long> storeProductIdCaptor = ArgumentCaptor.forClass(Long.class);
		verify(productManager, times(relevantProducts.size()))
				.findByStoreProductId(storeCaptor.capture(), storeProductIdCaptor.capture());

		if (relevantProducts.isEmpty())
			return;

		assertEquals(store, storeCaptor.getValue());
		assertEquals(storeProductIds, storeProductIdCaptor.getAllValues());
	}

	private void checkCallSearchStatisticManagerSave(SearchStatistic searchStatistic) {
		ArgumentCaptor<SearchStatistic> captor = ArgumentCaptor.forClass(SearchStatistic.class);
		verify(searchStatisticManager, times(1)).save(captor.capture());

		assertEquals(searchStatistic.getSearch(), captor.getValue().getSearch());
		assertEquals(searchStatistic.getStatusCode(), captor.getValue().getStatusCode());
		assertEquals(searchStatistic.getStatusDescription(), captor.getValue().getStatusDescription());
		assertEquals(searchStatistic.getCount(), captor.getValue().getCount());
	}
}