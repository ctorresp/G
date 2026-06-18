package cl.duoc.rednorte.pabellon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PabellonApplication {
    public static void main(String[] args) {
        SpringApplication.run(PabellonApplication.class, args);
    }
}
