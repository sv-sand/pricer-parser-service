package ru.svsand.pricer.parserservice;

import ru.svsand.pricer.parserservice.db.ProductDao;
import ru.svsand.pricer.parserservice.db.SearchDao;
import ru.svsand.pricer.parserservice.db.SearchStatisticDao;
import ru.svsand.pricer.parserservice.db.UserDao;
import ru.svsand.pricer.parserservice.logic.*;
import ru.svsand.pricer.parserservice.logic.parser.Parser;

import javax.net.ssl.SSLSession;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 26.01.2026
 */

public class Data {
	public static User user() {
		return User.builder()
				.id(1L)
				.name("Test user")
				.tgId(101L)
				.version(1001L)
				.build();
	}

	public static UserDao userDao() {
		UserDao user = new UserDao();
		user.setId(1L);
		user.setName("Test user");
		user.setTgId(101L);
		user.setVersion(1001L);

		return user;
	}

	public static Search search(Store store, User user) {
		return Search.builder()
				.id(1L)
				.keyWords("test product")
				.targetPrice(1000.0)
				.user(user)
				.store(store)
				.lastRequestDate(currentTimeTillMinutes())
				.version(1001L)
				.build();
	}

	public static SearchDao searchDao(Store store, UserDao user) {
		SearchDao searchDao = new SearchDao();
		searchDao.setId(1L);
		searchDao.setUser(user);
		searchDao.setStore(store.name());
		searchDao.setKeyWords("test product");
		searchDao.setTargetPrice(1000.0);
		searchDao.setLastRequestDate(currentTimeTillMinutes());
		searchDao.setVersion(1001L);
		return searchDao;
	}

	public static Product product(Search search) {
		return new Product(1L, "Product 1", Store.WB, search, 101L, "http://example.com/1", 500.0, false, 1001L);
	}

	public static ProductDao productDao(SearchDao searchDao, Store store) {
		ProductDao productDao = new ProductDao();
		productDao.setId(1L);
		productDao.setName("Product 1");
		productDao.setSearch(searchDao);
		productDao.setStore(store.name());
		productDao.setStoreProductId(101L);
		productDao.setStoreProductLink("http://example.com/1");
		productDao.setPrice(500.0);
		productDao.setUserNotified(false);
		productDao.setVersion(1001L);

		return productDao;
	}

	public static List<Product> products(Search search) {
		return List.of(
				new Product(1L, "Product 1", search.getStore(), search, 101L, "http://example.com/1", 500.0, false, 1001L),
				new Product(2L, "Product 2", search.getStore(), search, 102L, "http://example.com/2", 700.0, false, 1001L),
				new Product(4L, "Product 4", search.getStore(), search, 104L, "http://example.com/4", 1000.0, false, 1001L)
		);
	}

	public static List<Parser.ParsedProduct> parsedProducts() {
		return List.of(
				new Parser.ParsedProduct(101L, "Product 1", "http://example.com/1", 500.0),
				new Parser.ParsedProduct(102L, "Product 2", "http://example.com/2", 700.0),
				new Parser.ParsedProduct(103L, "Product 3", "http://example.com/3", 1500.0),
				new Parser.ParsedProduct(104L, "Product 4", "http://example.com/4", 1000.0)
		);
	}

	public static SearchStatistic searchStatistic(Search search) {
		return SearchStatistic.builder()
				.id(1L)
				.search(search)
				.statusCode(200)
				.statusDescription("OK")
				.count(1)
				.timestamp(currentTimeTillMinutes())
				.version(1001L)
				.build();
	}

	public static SearchStatisticDao searchStatisticDao(SearchDao searchDao) {
		SearchStatisticDao searchStatisticDao = new SearchStatisticDao();
		searchStatisticDao.setId(1L);
		searchStatisticDao.setSearch(searchDao);
		searchStatisticDao.setStatusCode(200);
		searchStatisticDao.setStatusDescription("OK");
		searchStatisticDao.setCount(1);
		searchStatisticDao.setTimestamp(currentTimeTillMinutes());
		searchStatisticDao.setVersion(1001L);

		return searchStatisticDao;
	}

	public static HttpResponse<String> httpResponse(int expectedStatusCode, String expectedBody) {
		return new HttpResponse<>() {
			@Override
			public int statusCode() {
				return expectedStatusCode;
			}

			@Override
			public HttpRequest request() {
				return null;
			}

			@Override
			public Optional<HttpResponse<String>> previousResponse() {
				return Optional.empty();
			}

			@Override
			public HttpHeaders headers() {
				return null;
			}

			@Override
			public String body() {
				return expectedBody;
			}

			@Override
			public Optional<SSLSession> sslSession() {
				return Optional.empty();
			}

			@Override
			public URI uri() {
				return null;
			}

			@Override
			public HttpClient.Version version() {
				return null;
			}
		};
	}

	// Helpful methods

	public static Timestamp currentTimeTillMinutes() {
		return new Timestamp(System.currentTimeMillis() / 60 * 60); // Current minute
	}
}
