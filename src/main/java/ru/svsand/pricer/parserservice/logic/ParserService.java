package ru.svsand.pricer.parserservice.logic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.svsand.pricer.parserservice.db.ProductManager;
import ru.svsand.pricer.parserservice.db.SearchManager;
import ru.svsand.pricer.parserservice.db.SearchStatisticManager;
import ru.svsand.pricer.parserservice.logic.parser.Parser;
import ru.svsand.pricer.parserservice.logic.parser.ParserManager;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;

/**
 * Scheduled service that orchestrates periodic product price updates.
 * Every 60 seconds it fetches searches that are due for a refresh, invokes the appropriate
 * marketplace parser for each one, persists execution statistics, and saves the filtered results.
 *
 * @author sand <sve.snd@gmail.com>
 * @since 23.10.2025
 */
@Slf4j
@Service
public class ParserService {

	/** Maximum number of products retained per search after price filtering and sorting. */
	public static final int MAX_PRODUCTS_PER_SEARCH = 3;

	@Autowired
	private SearchManager searchManager;

	@Autowired
	private ProductManager productManager;

	@Autowired
	private SearchStatisticManager searchStatisticManager;

	/**
	 * Scheduled entry point that triggers product updates for all searches due for a refresh.
	 * Runs every 60 seconds. Each search is processed independently; errors are logged and
	 * do not prevent remaining searches from being processed.
	 */
	@Scheduled(fixedRate = 60*1000)
	public void updateProducts() {
		log.info("Updating products");

		List<Search> searches = searchManager.findAllForRequest();
		for (Search search : searches) {
			try (Parser parser = ParserManager.createParserByStore(search.getStore())) {
				updateProductsBySearch(parser, search);
			} catch (Exception e) {
				log.error("Error updating products", e);
			}
		}
		log.info("Products updated");
	}

	private void updateProductsBySearch(Parser parser, Search search) {
		log.info("Processing {}", search);

		Parser.Result result = parser.findProducts(search.getKeyWords());

		// Update statistics
		SearchStatistic statistic = SearchStatistic.builder()
				.search(search)
				.statusCode(result.code())
				.statusDescription(result.description())
				.count(result.products().size())
				.timestamp(new Timestamp(System.currentTimeMillis()))
				.build();

		searchStatisticManager.save(statistic);

		// Update search last request date
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		search.setLastRequestDate(timestamp);
		searchManager.save(search);

		// Log result
		if (result.code() != 200)
			log.error("Failed to find new products [{}]: {}", result.code(), result.description());
		else
			log.info("Products found {}", result.products().size());

		// Convert and save relevant products
		List<Product> relevantProducts = result.products().stream()
				.filter(product -> product.price() <= search.getTargetPrice())
				.sorted(Comparator.comparingDouble(Parser.ParsedProduct::price))
				.limit(MAX_PRODUCTS_PER_SEARCH)
				.map(product -> parsedProductToProduct(search, product))
				.toList();

		productManager.saveAll(relevantProducts);

		log.info("Products accepted {}", relevantProducts.size());
	}

	private Product parsedProductToProduct(Search search, Parser.ParsedProduct parsedProduct) {
		Product product = productManager.findByStoreProductId(search.getStore(), parsedProduct.id());
		if (product != null)
			return product;

		return ParserManager.fromParsedProduct(search, parsedProduct);
	}
}
