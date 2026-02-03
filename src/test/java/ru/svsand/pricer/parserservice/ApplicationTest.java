package ru.svsand.pricer.parserservice;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ApplicationTest {

	private static MockedStatic<SpringApplication> mockedSpringApplication;

	@BeforeAll
	static void beforeAll() {
		mockedSpringApplication = Mockito.mockStatic(SpringApplication.class);
	}

	@AfterAll
	static void afterAll() {
		mockedSpringApplication.close();
	}

	@Test
	void application() {
		// Act
		Application app = new Application();
		assertNotNull(app);
	}

	@Test
	void main() {
		// Arrange
		when(SpringApplication.run(any(Class.class), any(String[].class))).thenReturn(null);

		// Act
		Application.main(new String[]{});
	}
}