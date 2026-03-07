package ru.svsand.pricer.parserservice.logic.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.svsand.pricer.parserservice.Data;
import ru.svsand.pricer.parserservice.logic.Product;
import ru.svsand.pricer.parserservice.logic.Search;
import ru.svsand.pricer.parserservice.logic.Store;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ParserManagerTest {
	@InjectMocks
	private ParserManager parserManager;

	@Test
	void createParserByStoreWb() {
		// Act
		Parser parser = parserManager.createParserByStore(Store.WB);

		// Assert
		assertNotNull(parser);
		assertInstanceOf(ParserWbApi.class, parser);
	}

	@Test
	void fromParsedProduct() {
		// Arrange
		Search search = Data.search(Store.WB, Data.user());
		Parser.ParsedProduct parsedProduct = Data.parsedProduct();

		// Act
		Product product = parserManager.fromParsedProduct(search, parsedProduct);

		// Assert
		assertNotNull(product);
		assertEquals(product.getStoreProductId(), parsedProduct.id());
		assertEquals(product.getName(), parsedProduct.name());
		assertEquals(product.getStoreProductLink(), parsedProduct.link());
		assertEquals(product.getPrice(), parsedProduct.price());
		assertEquals(product.getSearch(), search);
	}
}