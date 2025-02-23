package com.vinicius.gerenciamento_financeiro.adapter.out.transacao;

import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JpaTransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findAllByCategoria_id(Long id);
}
