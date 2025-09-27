package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.moeda;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.moeda.entity.MoedaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MoedaJpaRepository extends JpaRepository<MoedaJpaEntity, Long> {

    @Query("SELECT m FROM MoedaJpaEntity m WHERE m.ativo = true ORDER BY m.codigo")
    List<MoedaJpaEntity> findAllActiveOrderByCode();

    @Query("SELECT m FROM MoedaJpaEntity m WHERE m.codigo = :codigo")
    Optional<MoedaJpaEntity> findByCodigo(String codigo);

    @Query("SELECT m FROM MoedaJpaEntity m WHERE m.codigo = :codigo AND m.ativo = true")
    Optional<MoedaJpaEntity> findByCodigoAndActive(String codigo);

    @Query("SELECT COUNT(m) FROM MoedaJpaEntity m WHERE m.ativo = true")
    long countByActiveTrue();

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM MoedaJpaEntity m WHERE m.codigo = :codigo")
    boolean existsByCodigo(String codigo);

    @Query("SELECT m FROM MoedaJpaEntity m ORDER BY m.codigo")
    List<MoedaJpaEntity> findAllOrderByCode();
}