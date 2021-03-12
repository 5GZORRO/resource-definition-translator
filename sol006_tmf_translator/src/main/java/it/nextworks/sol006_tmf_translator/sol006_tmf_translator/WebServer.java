package it.nextworks.sol006_tmf_translator.sol006_tmf_translator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebServer {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WebServer.class);
        app.setWebApplicationType(WebApplicationType.SERVLET);
        app.run(args);
    }
}
