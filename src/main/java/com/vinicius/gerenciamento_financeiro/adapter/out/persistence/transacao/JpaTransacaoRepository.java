package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.TransacaoJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoMovimentacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaTransacaoRepository extends JpaRepository<TransacaoJpaEntity, Long> {

    List<TransacaoJpaEntity> findAllByUsuario_Id(Long usuarioId);

    Page<TransacaoJpaEntity> findAllByUsuario_Id(Long usuarioId, Pageable pageable);

    Optional<TransacaoJpaEntity> findByIdAndUsuario_Id(Long transacaoId, Long usuarioId);

    List<TransacaoJpaEntity> findAllByCategoria_IdAndUsuario_Id(Long categoriaId, Long usuarioId);

    Page<TransacaoJpaEntity> findAllByCategoria_IdAndUsuario_Id(Long categoriaId, Long usuarioId, Pageable pageable);

    @Query("SELECT t FROM TransacaoJpaEntity t " +
            "JOIN FETCH t.categoria " +
            "JOIN FETCH t.usuario " +
            "WHERE t.usuario.id = :usuarioId " +
            "ORDER BY t.data DESC")
    List<TransacaoJpaEntity> findAllByUsuarioIdWithRelations(@Param("usuarioId") Long usuarioId);

    @Query("SELECT t FROM TransacaoJpaEntity t " +
            "JOIN FETCH t.categoria " +
            "JOIN FETCH t.usuario " +
            "WHERE t.usuario.id = :usuarioId")
    Page<TransacaoJpaEntity> findAllByUsuarioIdWithRelations(@Param("usuarioId") Long usuarioId, Pageable pageable);

    @Query("SELECT t FROM TransacaoJpaEntity t " +
            "JOIN FETCH t.categoria " +
            "JOIN FETCH t.usuario " +
            "WHERE t.categoria.id = :categoriaId AND t.usuario.id = :usuarioId")
    List<TransacaoJpaEntity> findAllByCategoriaIdAndUsuarioIdWithRelations(
            @Param("categoriaId") Long categoriaId,
            @Param("usuarioId") Long usuarioId);

    long countByUsuario_Id(Long usuarioId);

    long countByCategoria_IdAndUsuario_Id(Long categoriaId, Long usuarioId);

    boolean existsByIdAndUsuario_Id(Long transacaoId, Long usuarioId);

    @Query("SELECT t FROM TransacaoJpaEntity t " +
            "WHERE t.usuario.id = :usuarioId " +
            "ORDER BY t.data DESC")
    Page<TransacaoJpaEntity> findRecentByUsuarioId(@Param("usuarioId") Long usuarioId, Pageable pageable);

    @Query("SELECT SUM(t.valor) FROM TransacaoJpaEntity t " +
            "WHERE t.usuario.id = :usuarioId AND t.tipo = :tipo")
    BigDecimal sumValorByUsuarioIdAndTipo(@Param("usuarioId") Long usuarioId, @Param("tipo") TipoMovimentacao tipo);

    @Query("SELECT t FROM TransacaoJpaEntity t " +
            "WHERE t.usuario.id = :usuarioId " +
            "AND t.data BETWEEN :dataInicio AND :dataFim")
    List<TransacaoJpaEntity> findByUsuarioIdAndDataBetween(
            @Param("usuarioId") Long usuarioId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT t FROM TransacaoJpaEntity t " +
            "WHERE t.dataVencimento <= :data AND t.pago = false")
    List<TransacaoJpaEntity> findTransacoesVencidas(@Param("data") java.time.LocalDate data);

    @Query("SELECT t FROM TransacaoJpaEntity t " +
            "WHERE t.recorrente = true AND t.usuario.id = :usuarioId")
    List<TransacaoJpaEntity> findTransacoesRecorrentes(@Param("usuarioId") Long usuarioId);

    @Deprecated
    List<TransacaoJpaEntity> findAllByCategoria_Id(Long categoriaId);
}