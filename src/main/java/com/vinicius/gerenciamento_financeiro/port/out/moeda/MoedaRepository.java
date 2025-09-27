package com.vinicius.gerenciamento_financeiro.port.out.moeda;

import com.vinicius.gerenciamento_financeiro.domain.model.moeda.Moeda;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MoedaId;

import java.util.List;
import java.util.Optional;

public interface MoedaRepository {

    Moeda save(Moeda moeda);

    Optional<Moeda> findById(MoedaId moedaId);

    Optional<Moeda> findByCodigo(String codigo);

    List<Moeda> findAllActive();

    List<Moeda> findAll();

    boolean existsById(MoedaId moedaId);

    boolean existsByCodigo(String codigo);

    void deleteById(MoedaId moedaId);

    long count();

    long countActive();
}