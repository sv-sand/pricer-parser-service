package ru.svsand.pricer.parserservice.logic.parser;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.svsand.pricer.parserservice.logic.Product;
import ru.svsand.pricer.parserservice.logic.Search;
import ru.svsand.pricer.parserservice.logic.Store;

/**
 * Factory and conversion utility for {@link Parser} implementations.
 * Centralises parser instantiation and the mapping from parser output to domain objects.
 *
 * @author sand <sve.snd@gmail.com>
 * @since 13.01.2026
 */
@Service
public class ParserManager {

	/**
	 * Creates the appropriate {@link Parser} implementation for the given store.
	 *
	 * @param store the target marketplace; must not be {@code null}
	 * @return a new parser instance ready for use
	 * @throws IllegalArgumentException if no parser is registered for the given store
	 */
	public static Parser createParserByStore(@NotNull Store store) {
		return switch (store) {
			case WB -> new ParserWbApi();
			default -> throw new IllegalArgumentException("Unknown store: " + store);
		};
	}

	/**
	 * Converts a raw {@link Parser.ParsedProduct} into a domain {@link Product},
	 * associating it with the originating {@link Search}.
	 *
	 * @param search        the search configuration that produced this product
	 * @param parsedProduct the raw product data from the parser
	 * @return a new, unsaved {@link Product} domain object
	 */
	public static Product fromParsedProduct(@NotNull Search search, @NotNull Parser.ParsedProduct parsedProduct) {
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
