package ru.svsand.pricer.parserservice.logic;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.svsand.pricer.parserservice.db.*;
import ru.svsand.pricer.parserservice.logic.parser.Parser;
import ru.svsand.pricer.parserservice.logic.parser.ParserManager;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
		Search search = search(store);
		List<Product> products = products(search);
		Parser.Result parserResult = new Parser.Result(200, "OK", parsedProducts());

		// Arrange
		when(ParserManager.createParserByStore(any())).thenReturn(parser);
		doNothing().when(searchManager).save(any());
		doNothing().when(searchStatisticManager).save(any());
		doNothing().when(productManager).saveAll(any());

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
		checkCallSearchStatisticManagerSave(search, 200, "OK", 4);
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
		Search search = search(store);

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
		Search search = search(store);
		List<Product> products = new ArrayList<>();
		Parser.Result parserResult = new Parser.Result(404, "Not found", new ArrayList<>());

		// Arrange
		when(ParserManager.createParserByStore(any())).thenReturn(parser);
		doNothing().when(searchManager).save(any());
		doNothing().when(searchStatisticManager).save(any());
		doNothing().when(productManager).saveAll(any());

		when(searchManager.findAllForRequest()).thenReturn(List.of(search));
		when(parser.findProducts(anyString())).thenReturn(parserResult);

		// Act
		parserService.updateProducts();

		// Assert
		checkCallSearchManagerSave(List.of(search));
		checkCallSearchStatisticManagerSave(search, 404, "Not found", 0);
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
		ArgumentCaptor<Search> searchCaptor = ArgumentCaptor.forClass(Search.class);
		verify(searchManager, times(1)).save(searchCaptor.capture());
		List<Search> savedSearches = searchCaptor.getAllValues();

		assertEquals(searches.size(), savedSearches.size());
		assertEquals(searches, savedSearches);
	}

	private void checkCallParserFindProducts(String productKeyWords) {
		ArgumentCaptor<String> productKeyWordsCaptor = ArgumentCaptor.forClass(String.class);
		verify(parser, times(1)).findProducts(productKeyWordsCaptor.capture());
		String capturedProductKeyWords = productKeyWordsCaptor.getValue();

		assertEquals(productKeyWords, capturedProductKeyWords);
	}

	private void checkCallProductManagerSaveAll(List<Product> products) {
		@SuppressWarnings("unchecked")
		ArgumentCaptor<List<Product>> productsCaptor = ArgumentCaptor.forClass(List.class);
		verify(productManager, times(1)).saveAll(productsCaptor.capture());
		List<Product> savedProducts = productsCaptor.getValue();

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
		verify(productManager, times(relevantProducts.size())).findByStoreProductId(storeCaptor.capture(), storeProductIdCaptor.capture());

		if (relevantProducts.isEmpty())
			return;

		assertEquals(store, storeCaptor.getValue());
		assertEquals(storeProductIds, storeProductIdCaptor.getAllValues());
	}

	private void checkCallSearchStatisticManagerSave(Search search, int statusCode, String description, int count) {
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());

		ArgumentCaptor<SearchStatistic> searchStatisticCaptor = ArgumentCaptor.forClass(SearchStatistic.class);
		verify(searchStatisticManager, times(1)).save(searchStatisticCaptor.capture());
		SearchStatistic savedStatistic = searchStatisticCaptor.getValue();

		assertEquals(search, savedStatistic.getSearch());
		assertEquals(statusCode, savedStatistic.getStatusCode());
		assertEquals(description, savedStatistic.getStatusDescription());
		assertEquals(count, savedStatistic.getCount());
		assertEquals(truncateToMinutes(currentTime),
				truncateToMinutes(savedStatistic.getTimestamp()),
				"Check statistic timestamp");
	}


	// Support methods

	private Timestamp truncateToMinutes(Timestamp timestamp) {
		if (timestamp == null) {
			return null;
		}
		LocalDateTime dateTime = timestamp.toLocalDateTime()
				.truncatedTo(ChronoUnit.MINUTES);
		return Timestamp.valueOf(dateTime);
	}

	private Search search(Store store) {
		return Search.builder()
				.id(1L)
				.keyWords("test product")
				.targetPrice(1000.0)
				.store(store)
				.build();
	}

	private List<Product> products(Search search) {
		return List.of(
				new Product(1L, "Product 1", search.getStore(), search, 101L, "http://example.com/1", 500.0, false, 0L),
				new Product(2L, "Product 2", search.getStore(), search, 102L, "http://example.com/2", 700.0, false, 0L),
				new Product(4L, "Product 4", search.getStore(), search, 104L, "http://example.com/4", 1000.0, false, 0L)
		);
	}

	private List<Parser.ParsedProduct> parsedProducts() {
		return List.of(
				new Parser.ParsedProduct(101L, "Product 1", "http://example.com/1", 500.0),
				new Parser.ParsedProduct(102L, "Product 2", "http://example.com/2", 700.0),
				new Parser.ParsedProduct(103L, "Product 3", "http://example.com/3", 1500.0),
				new Parser.ParsedProduct(104L, "Product 4", "http://example.com/4", 1000.0)
		);
	}
}