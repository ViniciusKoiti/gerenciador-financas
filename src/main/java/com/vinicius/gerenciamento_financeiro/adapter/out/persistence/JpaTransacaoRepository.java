package com.vinicius.gerenciamento_financeiro.adapter.out.persistence;

import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTransacaoRepository extends JpaRepository<Transacao, Long> {
}
