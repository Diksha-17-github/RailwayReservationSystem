package com.railwayReservationPayment.Payment_Gateway.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Railway Reservation Payment Service API")
                        .version("1.0")
                        .description("Payment gateway manages the pending, confirmed transactions")
                        .contact(new Contact()
                                .name("Diksha Nandakumar Pimple")
                                .email("dikshapimple@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Local development server")
                ));
    }
}
