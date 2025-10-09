package com.vinicius.gerenciamento_financeiro.config;

import com.vinicius.gerenciamento_financeiro.port.in.NotificarUseCase;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@TestConfiguration
@Profile("test")
public class TestConfig {

    /**
     * Mock do NotificarUseCase para evitar dependência do RabbitMQ em testes
     */
    @Bean
    @Primary
    public NotificarUseCase notificarUseCase() {
        return Mockito.mock(NotificarUseCase.class);
    }

    /**
     * Clock fixo para testes determinísticos
     */
    @Bean
    @Primary
    public Clock fixedClock() {
        return Clock.fixed(Instant.parse("2024-01-01T12:00:00Z"), ZoneId.of("UTC"));
    }
}