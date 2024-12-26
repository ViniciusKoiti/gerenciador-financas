package com.vinicius.gerenciamento_financeiro.adapter.out.persistence;

import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import com.vinicius.gerenciamento_financeiro.port.out.transacao.TransacaoRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("jpa")
public interface JpaTransacaoRepository extends JpaRepository<Transacao, Long>, TransacaoRepository {
}
