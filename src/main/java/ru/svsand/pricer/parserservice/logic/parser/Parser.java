package ru.svsand.pricer.parserservice.logic.parser;

import java.io.Closeable;
import java.util.List;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 29.10.2025
 */

public interface Parser extends Closeable {

	record Result(
			int code,
			String description,
			List<ParsedProduct> products
	){}

	record ParsedProduct(
			Long id,
			String name,
			String link,
			Double price
	) { }

	Result findProducts(String productKeyWords);
}
