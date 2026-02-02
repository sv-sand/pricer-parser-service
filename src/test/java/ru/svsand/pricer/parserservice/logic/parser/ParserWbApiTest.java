package ru.svsand.pricer.parserservice.logic.parser;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.svsand.pricer.parserservice.Data;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParserWbApiTest {

	@InjectMocks
	private ParserWbApi parser;

	@Mock
	private HttpClient client;

	@Mock
	private static HttpClient.Builder httpClientBuilder;
	private static MockedStatic<HttpClient> mockedHttpClient;

	@BeforeAll
	static void beforeAll() {
		mockedHttpClient = Mockito.mockStatic(HttpClient.class);
	}

	@AfterAll
	static void afterAll() {
		mockedHttpClient.close();
	}

	@Test
	void findProducts() throws IOException, InterruptedException {
		String responseBody = "{\"products\":[" +
				"{\"id\":1,\"name\":\"test\",\"sizes\":[{\"price\":{\"product\":10000.0}}]}" +
				"]}";
		HttpResponse<String> response = Data.httpResponse(200, responseBody);

		// Arrange
		when(HttpClient.newBuilder()).thenReturn(httpClientBuilder);
		when(httpClientBuilder.build()).thenReturn(client);
		when(client.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

		// Act
        Parser.Result result = parser.findProducts("test search");

		// Assert
		String urlString = String.format(parser.SEARCH_URL, "test+search");
		checkCall_Client_Send("GET", urlString);

		assertEquals(200, result.code());
		assertEquals("", result.description());
		assertEquals(1, result.products().size());

		Parser.ParsedProduct product = result.products().get(0);
		assertEquals(1, product.id());
        assertEquals("test", product.name());
        assertEquals("https://www.wildberries.ru/catalog/1/detail.aspx", product.link());
        assertEquals(100.0, product.price());
	}

	@Test
	void findProducts_Empty() throws IOException, InterruptedException {
		String responseBody = "{\"products\":[]}";
		HttpResponse<String> response = Data.httpResponse(200, responseBody);

		// Arrange
		when(HttpClient.newBuilder()).thenReturn(httpClientBuilder);
		when(httpClientBuilder.build()).thenReturn(client);
		when(client.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

		// Act
		Parser.Result result = parser.findProducts("test search");

		// Assert
		String urlString = String.format(parser.SEARCH_URL, "test+search");
		checkCall_Client_Send("GET", urlString);

		assertEquals(200, result.code());
		assertEquals("", result.description());
		assertEquals(0, result.products().size());
	}

	@Test
	void findProducts_Unavailable() throws IOException, InterruptedException {
		// Arrange
		when(HttpClient.newBuilder()).thenReturn(httpClientBuilder);
		when(httpClientBuilder.build()).thenReturn(client);
		doThrow(new IOException("Test exception")).when(client)
				.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));

		// Act
		Parser.Result result = parser.findProducts("test");

		// Assert
		assertEquals(0, result.code());
		assertEquals("Failed to perform API request: Test exception", result.description());
		assertEquals(0, result.products().size());
	}

	// Checks

	private void checkCall_Client_Send(String method, String uri) throws IOException, InterruptedException {
		ArgumentCaptor<HttpRequest> captorRequest = ArgumentCaptor.forClass(HttpRequest.class);
		ArgumentCaptor<HttpResponse.BodyHandler<String>> captorBodyHandler = ArgumentCaptor.forClass(HttpResponse.BodyHandler.class);
		verify(client, times(1)).send(captorRequest.capture(), captorBodyHandler.capture());

		assertEquals(uri, captorRequest.getValue().uri().toString());
		assertEquals(method, captorRequest.getValue().method());
	}
}