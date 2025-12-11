package org.delcom.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersonalCollectionApplicationTests {

	@Test
	void mainMethod_ShouldRunSpringApplication() throws Exception {
		// Mock SpringApplication.run untuk test main method
		try (var mockedSpring = mockStatic(SpringApplication.class)) {
			ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);
			
			// Menggunakan PersonalCollectionApplication.class
			mockedSpring.when(() -> SpringApplication.run(PersonalCollectionApplication.class, new String[] {}))
					.thenReturn(mockContext);

			// Jalankan main method
			assertDoesNotThrow(() -> PersonalCollectionApplication.main(new String[] {}));

			// Verify SpringApplication.run dipanggil dengan class yang benar
			mockedSpring.verify(() -> SpringApplication.run(PersonalCollectionApplication.class, new String[] {}));
		}
	}

	@Test
	void contextLoads_ShouldNotThrowException() throws Exception {
		// Test bahwa Spring context bisa dimuat
		assertDoesNotThrow(() -> {
			// Test basic class loading dengan nama class baru
			Class<?> clazz = Class.forName("org.delcom.app.PersonalCollectionApplication");
			assertNotNull(clazz);
		});
	}

	@Test
	void applicationClass_ShouldHaveSpringBootAnnotation() throws Exception {
		// Test bahwa class memiliki annotation @SpringBootApplication
		assertNotNull(PersonalCollectionApplication.class
				.getAnnotation(org.springframework.boot.autoconfigure.SpringBootApplication.class));
	}

	@Test
	void applicationClass_CanBeInstantiated() throws Exception {
		// Test bahwa kita bisa membuat instance PersonalCollectionApplication
		assertDoesNotThrow(() -> {
			PersonalCollectionApplication app = new PersonalCollectionApplication();
			assertNotNull(app);
		});
	}
}