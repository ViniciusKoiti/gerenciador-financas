package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.moeda;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.moeda.entity.MoedaJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.Moeda;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MoedaId;
import com.vinicius.gerenciamento_financeiro.port.out.moeda.MoedaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MoedaRepositoryImpl implements MoedaRepository {

    private final MoedaJpaRepository jpaRepository;
    private final MoedaJpaMapper mapper;

    public MoedaRepositoryImpl(MoedaJpaRepository jpaRepository, MoedaJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Moeda save(Moeda moeda) {
        MoedaJpaEntity entity = mapper.toJpaEntity(moeda);
        MoedaJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<Moeda> findById(MoedaId moedaId) {
        return jpaRepository.findById(Long.valueOf(moedaId.getValor()))
                .map(mapper::toDomainEntity);
    }

    @Override
    public Optional<Moeda> findByCodigo(String codigo) {
        return jpaRepository.findByCodigo(codigo)
                .map(mapper::toDomainEntity);
    }

    @Override
    public List<Moeda> findAllActive() {
        return jpaRepository.findAllActiveOrderByCode()
                .stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Moeda> findAll() {
        return jpaRepository.findAllOrderByCode()
                .stream()
                .map(mapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(MoedaId moedaId) {
        return jpaRepository.existsById(Long.valueOf(moedaId.getValor()));
    }

    @Override
    public boolean existsByCodigo(String codigo) {
        return jpaRepository.existsByCodigo(codigo);
    }

    @Override
    public void deleteById(MoedaId moedaId) {
        jpaRepository.deleteById(Long.valueOf(moedaId.getValor()));
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public long countActive() {
        return jpaRepository.countByActiveTrue();
    }
}