package ru.svsand.pricer.parserservice.logic.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.svsand.pricer.parserservice.logic.Store;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}