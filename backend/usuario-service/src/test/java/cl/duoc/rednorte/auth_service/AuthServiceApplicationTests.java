package cl.duoc.rednorte.auth_service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requiere base de datos MySQL en Laragon — ejecutar con perfil 'test' para PostgreSQL en Docker")
class AuthServiceApplicationTests {

	@Test
	void contextLoads() {
	}
}
