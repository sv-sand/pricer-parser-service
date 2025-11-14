package ru.svsand.pricer.parserservice.logic.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 01.11.2025
 */

public class ParserWbApi implements Parser {

	final String SEARCH_URL = "https://www.wildberries.ru/__internal/u-search/exactmatch/ru/common/v18/search?ab_testing=false&appType=1&curr=rub&dest=-1257786&hide_dtype=11&inheritFilters=false&lang=ru&page=1&query=%s&resultset=catalog&sort=priceup&spp=30&suppressSpellcheck=false"; // Example API endpoint
	final String PRODUCT_LINK = "https://www.wildberries.ru/catalog/%d/detail.aspx";

	@Override
	public void close() throws IOException {

	}

	@Override
	public Result findProducts(String productKeyWords) {
		List<ParsedProduct> products = new ArrayList<>();
		HttpResponse<String> response;
		try {
			response = requestProducts(productKeyWords);
		} catch (IOException | InterruptedException e) {
			return new Result(0, String.format("Failed to perform API request: %s", e.getMessage()), products);
		}

		if (response.statusCode() != 200)
			return new Result(response.statusCode(), response.body(), products);

		products = parseApiResponse(response);
		return new Result(response.statusCode(), "", products);
	}

	private HttpResponse<String> requestProducts(String productKeyWords) throws IOException, InterruptedException {
		String keyWords = Arrays.stream(productKeyWords.split("\\s+"))
				.filter(word -> !word.isEmpty())
				.map(word -> URLEncoder.encode(word, StandardCharsets.UTF_8))
				.collect(Collectors.joining("+"));
		String urlString = String.format(SEARCH_URL, keyWords);
		URI uri = URI.create(urlString);

		HttpClient client = HttpClient.newBuilder().build();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(uri)
				.GET()
				.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36")
				.header("Accept", "*/*")
				.header("Accept-Language", "en-GB,en-US;q=0.9,en;q=0.8,ru;q=0.7")
				.header("Origin", "https://www.wildberries.ru")
				.header("Referer", String.format("https://www.wildberries.ru/catalog/0/search.aspx?search=%s", keyWords))
				.build();

		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}

	private List<ParsedProduct> parseApiResponse(HttpResponse<String> response) {
		List<ParsedProduct> products = new ArrayList<>();
		if (response.body().isEmpty())
			return products;

		JSONObject jsonResponse = new JSONObject(response.body());
		JSONArray jsonProducts = jsonResponse.getJSONArray("products");

		for (int i = 0; i < jsonProducts.length(); i++) {
			JSONObject jsonProduct = jsonProducts.getJSONObject(i);
			JSONArray jsonSizes = jsonProduct.getJSONArray("sizes");
			for (int j = 0; j < jsonSizes.length(); j++) {
				JSONObject jsonSize = jsonSizes.getJSONObject(j);
				ParsedProduct product = new ParsedProduct(
						jsonProduct.getLong("id"),
						jsonProduct.getString("name"),
						String.format(PRODUCT_LINK, jsonProduct.getInt("id")),
						jsonSize.getJSONObject("price").getDouble("product") / 100
				);
				products.add(product);
			}
		}
		return products;
	}
}
