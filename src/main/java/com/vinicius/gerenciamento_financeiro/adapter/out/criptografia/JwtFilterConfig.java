package com.vinicius.gerenciamento_financeiro.adapter.out.criptografia;

import com.vinicius.gerenciamento_financeiro.port.out.autorizacao.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class JwtFilterConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            TokenService jwtService,
            UserDetailsService userDetailsService
    ) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }
}
