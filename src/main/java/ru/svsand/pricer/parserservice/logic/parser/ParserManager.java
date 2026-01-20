package ru.svsand.pricer.parserservice.logic.parser;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.svsand.pricer.parserservice.logic.Store;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 13.01.2026
 */

@Service
public class ParserManager {
	public Parser createParserByStore(@NotNull Store store) {
		return switch (store) {
			case WB -> new ParserWbApi();
			default -> throw new IllegalArgumentException("Unknown store: " + store);
		};
	}
}
