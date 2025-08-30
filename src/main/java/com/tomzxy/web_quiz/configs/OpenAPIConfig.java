package com.tomzxy.web_quiz.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;

import java.util.Arrays;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Value("${tomzxy.openapi.dev-url}")
    private String devUrl;

    @Value("${tomzxy.openapi.prod-url}")
    private String prodUrl;

    @Value("${springdoc.packages-to-scan}")
    private String packageScan;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail("tomzxy@gmail.com");
        contact.setName("TomZxy");
        contact.setUrl("https://www.tomzxy.com");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
            .title("Web Quiz API")
            .version("1.0")
            .contact(contact)
            .description("This API exposes endpoints to manage Web Quiz.")
            .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(Arrays.asList(devServer, prodServer));
    }
    @Bean
    public GroupedOpenApi webQuizApiGroup(){
        return GroupedOpenApi.builder()
                .group("web_quiz")
                .packagesToScan(packageScan)
                .build();
    }
}
