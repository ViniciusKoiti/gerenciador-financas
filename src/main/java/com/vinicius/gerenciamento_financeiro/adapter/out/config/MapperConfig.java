package com.vinicius.gerenciamento_financeiro.adapter.out.config;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.categoria.CategoriaJpaMapper;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.TransacaoJpaMapper;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.usuario.UsuarioJpaMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public UsuarioJpaMapper usuarioJpaMapper() {
        return new UsuarioJpaMapper();
    }

    @Bean
    public CategoriaJpaMapper categoriaJpaMapper() {
        return new CategoriaJpaMapper();
    }

    @Bean
    public TransacaoJpaMapper transacaoJpaMapper() {
        return new TransacaoJpaMapper();
    }


}