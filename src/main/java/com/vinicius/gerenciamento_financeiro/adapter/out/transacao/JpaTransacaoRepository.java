package com.vinicius.gerenciamento_financeiro.adapter.out.transacao;

import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaTransacaoRepository extends JpaRepository<Transacao, Long> {

    // Métodos existentes (legados)
    List<Transacao> findAllByCategoria_id(Long id);

    // Novos métodos seguros que consideram o usuário
    List<Transacao> findAllByUsuario_id(Long usuarioId);

    Page<Transacao> findAllByUsuario_id(Long usuarioId, Pageable pageable);

    Optional<Transacao> findByIdAndUsuario_id(Long transacaoId, Long usuarioId);

    List<Transacao> findAllByCategoria_idAndUsuario_id(Long categoriaId, Long usuarioId);

    Page<Transacao> findAllByCategoria_idAndUsuario_id(Long categoriaId, Long usuarioId, Pageable pageable);

    // Métodos com Query personalizada para melhor performance
    @Query("SELECT t FROM Transacao t JOIN FETCH t.categoria WHERE t.usuario.id = :usuarioId ORDER BY t.data DESC")
    List<Transacao> findAllByUsuarioIdWithCategoria(@Param("usuarioId") Long usuarioId);

    @Query("SELECT t FROM Transacao t JOIN FETCH t.categoria WHERE t.usuario.id = :usuarioId")
    Page<Transacao> findAllByUsuarioIdWithCategoria(@Param("usuarioId") Long usuarioId, Pageable pageable);

    @Query("SELECT t FROM Transacao t JOIN FETCH t.categoria WHERE t.categoria.id = :categoriaId AND t.usuario.id = :usuarioId")
    List<Transacao> findAllByCategoriaIdAndUsuarioIdWithCategoria(@Param("categoriaId") Long categoriaId, @Param("usuarioId") Long usuarioId);

    @Query("SELECT t FROM Transacao t JOIN FETCH t.categoria WHERE t.categoria.id = :categoriaId AND t.usuario.id = :usuarioId")
    Page<Transacao> findAllByCategoriaIdAndUsuarioIdWithCategoria(@Param("categoriaId") Long categoriaId, @Param("usuarioId") Long usuarioId, Pageable pageable);

    // Métodos para contagem e estatísticas
    long countByUsuario_id(Long usuarioId);

    long countByCategoria_idAndUsuario_id(Long categoriaId, Long usuarioId);

    // Verificação de existência
    boolean existsByIdAndUsuario_id(Long transacaoId, Long usuarioId);

    // Método para buscar transações recentes (útil para dashboard)
    @Query("SELECT t FROM Transacao t WHERE t.usuario.id = :usuarioId ORDER BY t.data DESC")
    Page<Transacao> findRecentByUsuarioId(@Param("usuarioId") Long usuarioId, Pageable pageable);
}