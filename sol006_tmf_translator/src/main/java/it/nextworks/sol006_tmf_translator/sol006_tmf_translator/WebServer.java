package it.nextworks.sol006_tmf_translator.sol006_tmf_translator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
@EnableSwagger2
@ComponentScan(basePackages = { "it.nextworks.sol006_tmf_translator.information_models",
        "it.nextworks.sol006_tmf_translator.interfaces",
        "it.nextworks.sol006_tmf_translator.sol006_tmf_translator" })
@EntityScan("it.nextworks.sol006_tmf_translator.information_models.persistence")
public class WebServer {

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Collections.singletonList("*"));
        config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PATCH"));

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WebServer.class);
        app.setWebApplicationType(WebApplicationType.SERVLET);
        app.run(args);
    }
}
