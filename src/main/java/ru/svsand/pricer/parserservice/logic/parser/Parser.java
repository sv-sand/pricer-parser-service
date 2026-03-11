package ru.svsand.pricer.parserservice.logic.parser;

import java.io.Closeable;
import java.util.List;

/**
 * Contract for marketplace product parsers.
 * Implementations are {@link Closeable} to allow resource cleanup after use
 * (e.g., HTTP client connections).
 *
 * @author sand <sve.snd@gmail.com>
 * @since 29.10.2025
 */
public interface Parser extends Closeable {

	/**
	 * Encapsulates the outcome of a single {@link #findProducts} call.
	 *
	 * @param code        HTTP-style status code (200 on success, 0 on network failure)
	 * @param description human-readable status message; empty on success
	 * @param products    list of products found; may be empty on failure
	 */
	record Result(
			int code,
			String description,
			List<ParsedProduct> products
	) {}

	/**
	 * Lightweight value object representing a product returned by the marketplace API.
	 *
	 * @param id    marketplace-specific product identifier
	 * @param name  product display name
	 * @param link  direct URL to the product page
	 * @param price current price in rubles
	 */
	record ParsedProduct(
			Long id,
			String name,
			String link,
			Double price
	) {}

	/**
	 * Queries the marketplace for products matching the given keywords.
	 *
	 * @param productKeyWords space-separated search terms
	 * @return a {@link Result} containing the status and matched products
	 */
	Result findProducts(String productKeyWords);
}
