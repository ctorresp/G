package cl.duoc.rednorte.reasignacion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CorsConfig {

    @Bean
    public org.springframework.web.filter.CorsFilter corsFilter() {
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source =
                new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
        config.setAllowedOriginPatterns(java.util.List.of("*"));
        config.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(java.util.Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return new org.springframework.web.filter.CorsFilter(source);
    }
}
