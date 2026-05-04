package cl.duoc.rednorte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "cl.duoc.rednorte")
@EntityScan(basePackages = "cl.duoc.rednorte")
@EnableJpaRepositories(basePackages = "cl.duoc.rednorte")
public class RedNorteApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedNorteApplication.class, args);
    }
}