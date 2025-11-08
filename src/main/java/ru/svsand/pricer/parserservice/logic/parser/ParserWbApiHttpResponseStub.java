package ru.svsand.pricer.parserservice.logic.parser;

import javax.net.ssl.SSLSession;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 09.11.2025
 * This is the stub, only for debug
 */

record ParserWbApiHttpResponseStub<T>(int statusCode, T body) implements HttpResponse<T> {
	@Override
	public HttpRequest request() {
		return null;
	}

	@Override
	public Optional<HttpResponse<T>> previousResponse() {
		return Optional.empty();
	}

	@Override
	public HttpHeaders headers() {
		return null;
	}

	@Override
	public Optional<SSLSession> sslSession() {
		return Optional.empty();
	}

	@Override
	public URI uri() {
		return null;
	}

	@Override
	public HttpClient.Version version() {
		return null;
	}

	public static ParserWbApiHttpResponseStub<String> requestProducts(String productKeyWords) throws IOException, InterruptedException  {
		FileReader fileReader = new FileReader("/Users/sand/Dev/pricer/project/doc/search.json");
		BufferedReader reader = new BufferedReader(fileReader);
		String body = reader.lines()
				.collect(Collectors.joining());

		return new ParserWbApiHttpResponseStub<>(200, body);
	}
}
