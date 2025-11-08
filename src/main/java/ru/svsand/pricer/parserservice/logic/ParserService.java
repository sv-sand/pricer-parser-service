package ru.svsand.pricer.parserservice.logic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.svsand.pricer.parserservice.db.ProductManager;
import ru.svsand.pricer.parserservice.db.SearchManager;
import ru.svsand.pricer.parserservice.db.SearchStatisticManager;
import ru.svsand.pricer.parserservice.logic.parser.Parser;
import ru.svsand.pricer.parserservice.logic.parser.ParserWbApi;

import java.util.Comparator;
import java.util.List;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 23.10.2025
 */

@Slf4j
@Service
public class ParserService {

	@Autowired
	SearchManager searchManager;

	@Autowired
	ProductManager productManager;

	@Autowired
	SearchStatisticManager searchStatisticManager;

	@Scheduled(fixedRate = 3600000)
	private void updateProductsData() {
		log.info("Updating products");

		try (Parser parser = new ParserWbApi()) {

			List<Search> searches = searchManager.findAll();
			for (Search search : searches) {
				log.info("Processing {}", search);
				Parser.Result result = parser.findProducts(search.getKeyWords());
				saveStatistic(search, result);

				if (result.code() != 200)
					log.error("Failed to find new products [{}]: {}", result.code(), result.description());

				List<Parser.ParsedProduct> parsedProducts = filterProducts(search, result.products());
				saveProducts(search, parsedProducts);
				log.info("Products found {}, accepted {}", result.products().size(), parsedProducts.size());
			}
		}
		catch (Exception e) {
			log.error("Error updating products", e);
		}
		log.info("Products updated");
	}

	private void saveStatistic(Search search, Parser.Result result) {
		SearchStatistic statistic = SearchStatistic.builder()
				.search(search)
				.statusCode(result.code())
				.statusDescription(result.description())
				.count(result.products().size())
				.build();

		searchStatisticManager.save(statistic);
	}

	private List<Parser.ParsedProduct> filterProducts(Search search, List<Parser.ParsedProduct> parsedProducts) {
		return parsedProducts.stream()
				.filter(product -> product.price() <= search.getTargetPrice())
				.sorted(Comparator.comparingDouble(Parser.ParsedProduct::price))
				.limit(3)
				.toList();
	}

	private void saveProducts(Search search, List<Parser.ParsedProduct> parsedProducts) {
		List<Product> products = parsedProducts.stream()
				.map(product -> parsedProductToProduct(product, search))
				.toList();

		productManager.saveAll(products);
	}

	private Product parsedProductToProduct(Parser.ParsedProduct parsedProduct, Search search) {
		Product product = productManager.findByStoreProductId(search.getStore(), parsedProduct.id());
		if (product != null)
			return product;

		return Product.builder()
				.name(parsedProduct.name())
				.search(search)
				.store(search.getStore())
				.storeProductId(parsedProduct.id())
				.storeProductLink(parsedProduct.link())
				.price(parsedProduct.price())
				.userNotified(false)
				.build();
	}
}
