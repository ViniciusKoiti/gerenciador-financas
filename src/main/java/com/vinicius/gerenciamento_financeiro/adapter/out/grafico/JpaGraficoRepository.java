package com.vinicius.gerenciamento_financeiro.adapter.out.grafico;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.port.in.GerarGraficoUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.grafico.GraficoRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

import java.time.ZonedDateTime;

public interface JpaGraficoRepository extends CrudRepository<Transacao, Long> {

    @Query("""
        SELECT t.categoria.nome as label, SUM(t.valor) as value 
        FROM Transacao t 
        WHERE (:usuarioId IS NULL OR t.usuario.id = :usuarioId) 
          AND (:dataInicio IS NULL OR t.data >= :dataInicio) 
          AND (:dataFim IS NULL OR t.data <= :dataFim)
        GROUP BY t.categoria.nome
    """)
    List<GraficoResponse> gerarGraficoPorCategoria(Long usuarioId, ZonedDateTime dataInicio, ZonedDateTime dataFim);
}
