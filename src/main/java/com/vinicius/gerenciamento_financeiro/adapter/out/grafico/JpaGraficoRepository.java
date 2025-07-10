package com.vinicius.gerenciamento_financeiro.adapter.out.grafico;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.ResumoFinanceiroResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.TransacaoPorPeriodoResponse;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.TransacaoJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.port.in.GerarGraficoUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.grafico.GraficoRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import java.time.ZonedDateTime;
@Repository
public interface JpaGraficoRepository extends CrudRepository<TransacaoJpaEntity, Long> {

    @Query("""
    SELECT new com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse(t.categoria.nome, SUM(t.valor)) 
    FROM TransacaoJpaEntity t 
    WHERE (:usuarioId IS NULL OR t.usuario.id = :usuarioId) 
      AND (:dataInicio IS NULL OR t.data >= :dataInicio) 
      AND (:dataFim IS NULL OR t.data <= :dataFim)
    GROUP BY t.categoria.nome
    ORDER BY SUM(t.valor) desc 
""")
    List<GraficoResponse> gerarGraficoPorCategoria(
            @Param("usuarioId") Long usuarioId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    @Query("""
SELECT new com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.TransacaoPorPeriodoResponse(
    CONCAT(FUNCTION('MONTH', t.data), '/', FUNCTION('YEAR', t.data)), 
    SUM(CASE WHEN t.tipo = com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoMovimentacao.RECEITA THEN t.valor ELSE 0 END),
    SUM(CASE WHEN t.tipo = com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoMovimentacao.DESPESA THEN t.valor ELSE 0 END))
FROM TransacaoJpaEntity t
WHERE t.usuario.id = :usuarioId
  AND t.data >= :dataInicio
  AND t.data <= :dataFim
GROUP BY FUNCTION('YEAR', t.data), FUNCTION('MONTH', t.data), CONCAT(FUNCTION('MONTH', t.data), '/', FUNCTION('YEAR', t.data))
ORDER BY FUNCTION('YEAR', t.data), FUNCTION('MONTH', t.data)
""")
    List<TransacaoPorPeriodoResponse> gerarEvolucaoFinanceiraMensal(
            @Param("usuarioId") Long usuarioId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    @Query("""
SELECT new com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.ResumoFinanceiroResponse(
    SUM(CASE WHEN t.tipo = com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoMovimentacao.RECEITA THEN t.valor ELSE 0 END),
    SUM(CASE WHEN t.tipo = com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoMovimentacao.DESPESA THEN t.valor ELSE 0 END),
    (SUM(CASE WHEN t.tipo = com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoMovimentacao.RECEITA THEN t.valor ELSE 0 END) - 
     SUM(CASE WHEN t.tipo = com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoMovimentacao.DESPESA THEN t.valor ELSE 0 END)))
FROM TransacaoJpaEntity t
WHERE t.usuario.id = :usuarioId
    AND (CAST(:dataInicio AS TIMESTAMP) IS NULL OR t.data >= :dataInicio)
    AND (CAST(:dataFim AS TIMESTAMP) IS NULL OR t.data >= :dataFim)
""")
    ResumoFinanceiroResponse gerarResumoFinanceiro(
            @Param("usuarioId") Long usuarioId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

}