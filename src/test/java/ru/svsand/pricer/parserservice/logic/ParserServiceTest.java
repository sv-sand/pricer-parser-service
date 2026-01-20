package ru.svsand.pricer.parserservice.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.svsand.pricer.parserservice.db.ProductManager;
import ru.svsand.pricer.parserservice.db.SearchManager;
import ru.svsand.pricer.parserservice.db.SearchStatisticManager;
import ru.svsand.pricer.parserservice.logic.parser.Parser;
import ru.svsand.pricer.parserservice.logic.parser.ParserManager;
import ru.svsand.pricer.parserservice.logic.parser.ParserWbApi;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParserServiceTest {

	@InjectMocks
	private ParserService parserService;

	@Mock
	private ParserManager parserManager;

	@Mock
	private SearchManager searchManager;

	@Mock
	private ProductManager productManager;

	@Mock
	private SearchStatisticManager searchStatisticManager;

	@Mock
	private ParserWbApi parser;

	@BeforeEach
	void setUp() {

	}

	@Test
	void updateProductsData_Positive() throws Exception {
		Store store = Store.WB;
		Search search = search(store);
		List<Product> products = products(search);
		List<Parser.ParsedProduct> parsedProducts = parsedProducts();
		Parser.Result result = new Parser.Result(200, "OK", parsedProducts);

		// Arrange
		when(parserManager.createParserByStore(store)).thenReturn(parser);
		when(parser.findProducts(anyString())).thenReturn(result);

		doNothing().when(productManager).saveAll(any());
		when(productManager.findByStoreProductId(store, 101L)).thenReturn(products.get(0));
		when(productManager.findByStoreProductId(store, 102L)).thenReturn(products.get(1));
		when(productManager.findByStoreProductId(store, 104L)).thenReturn(products.get(3)); // product with id=2 is expensive

		when(searchManager.findAllForRequest()).thenReturn(List.of(search));
		doNothing().when(searchManager).save(any());

		doNothing().when(searchStatisticManager).save(any());

		// Act
		parserService.updateProducts();

		// Assert
		Timestamp currentTime = new Timestamp(System.currentTimeMillis());

		ArgumentCaptor<Search> searchCaptor = ArgumentCaptor.forClass(Search.class);
		verify(searchManager, times(1)).save(searchCaptor.capture());
		List<Search> savedSearches = searchCaptor.getAllValues();
		assertEquals(truncateToMinutes(currentTime),
				truncateToMinutes(savedSearches.get(0).getLastRequestDate()),
				"Check last request date");

		ArgumentCaptor<List<Product>> productsCaptor = ArgumentCaptor.forClass(List.class);
		verify(productManager, times(1)).saveAll(productsCaptor.capture());
		List<Product> savedProducts = productsCaptor.getValue();
		checkProduct(savedProducts.get(0), 1L, "Product 1", "http://example.com/1", 500.0);
		checkProduct(savedProducts.get(1), 2L, "Product 2", "http://example.com/2", 700.0);
		checkProduct(savedProducts.get(2), 4L, "Product 4", "http://example.com/4", 1000.0);

		//verify(searchStatisticManager).save(any());
	}

	// Checks

	private void checkProduct(Product product, long id, String name, String link, double price) {
		assertEquals(id, product.getId());
		assertEquals(name, product.getName());
		assertEquals(link, product.getStoreProductLink());
		assertEquals(price, product.getPrice());
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
				new Product(3L, "Product 3", search.getStore(), search, 103L, "http://example.com/3", 1500.0, false, 0L),
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