package ru.svsand.pricer.parserservice.logic.parser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sand <sve.snd@gmail.com>
 * @since 24.10.2025
 */

public class ParserWbPage implements Parser {
	final private String SEARCH_URL = "https://www.wildberries.ru/catalog/0/search.aspx?page=1&sort=priceup&search=%s";
	final private WebDriver driver;

	public ParserWbPage() {
		ChromeOptions options = new ChromeOptions();
		this.driver = new ChromeDriver(options);
		this.driver.manage()
				.timeouts()
				.implicitlyWait(Duration.ofSeconds(10));
	}

	@Override
	public void close() throws IOException {
		driver.close();
		driver.quit();
	}

	public Result findProducts(String productKeyWords) {
		List<ParsedProduct> products = new ArrayList<>();
		String url = String.format(SEARCH_URL, URLEncoder.encode(productKeyWords, StandardCharsets.UTF_8));
		driver.get(url);

		// Navigate by page
		List<WebElement> elementsCard = driver.findElements(By.className("product-card__wrapper"));

		for (WebElement elementCard : elementsCard) {
			WebElement elementName = elementCard.findElement(By.className("product-card__brand-wrap"));
			WebElement elementLink = elementCard.findElement(By.className("product-card__link"));
			WebElement elementPrice = elementCard.findElement(By.className("price__lower-price"));

			ParsedProduct product = new ParsedProduct(
					0L,
					elementName.getText(),
					elementLink.getDomAttribute("href"),
					recognizePrice(elementPrice.getText())
			);
			products.add(product);
		}
		return new Result(200, "", products);
	}

	private double recognizePrice(String priceString) {
		priceString = priceString
				.replace(" ", "")
				.replace("₽", "");

		return Double.parseDouble(priceString);
	}
}
