package com.vinicius.gerenciamento_financeiro.adapter.in.web.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.*;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI projetoOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("API de Transações Financeiras")
                        .description("Documentação da API do projeto de controle financeiro")
                        .version("v1.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Repositório no GitHub")
                        .url("https://github.com/ViniciusKoiti/"));
    }
}
