package it.nextworks.sol006_tmf_translator.sol006_tmf_translator.commons;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerDocumentationConfig {

    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("SOL006-TMF-Translator")
                .description("This is Swagger UI environment from the SOL006-TMF-Translator APIs.")
                .license("Apache License 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .termsOfServiceUrl("")
                .version("2.0")
                .contact(new Contact("","", ""))
                .build();
    }

    @Bean
    public Docket customImplementation() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors
                        .basePackage("it.nextworks.sol006_tmf_translator.sol006_tmf_translator.rest"))
                .build()
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo());
    }
}
