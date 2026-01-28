package ru.svsand.pricer.parserservice.logic.parser;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.svsand.pricer.parserservice.logic.Product;
import ru.svsand.pricer.parserservice.logic.Search;
import ru.svsand.pricer.parserservice.logic.Store;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 13.01.2026
 */

@Service
public class ParserManager {
	public static Parser createParserByStore(@NotNull Store store) {
		return switch (store) {
			case WB -> new ParserWbApi();
			default -> throw new IllegalArgumentException("Unknown store: " + store);
		};
	}

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
