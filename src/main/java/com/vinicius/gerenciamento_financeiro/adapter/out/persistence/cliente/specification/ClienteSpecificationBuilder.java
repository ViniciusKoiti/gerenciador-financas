package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.specification;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.entity.ClienteJpaEntity;
import com.vinicius.gerenciamento_financeiro.application.command.cliente.BuscarClientesCommand;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder para criar Specifications dinâmicas de busca de clientes
 */
@Component
public class ClienteSpecificationBuilder {

    /**
     * Constrói Specification a partir de um comando de busca
     */
    public Specification<ClienteJpaEntity> build(Long usuarioId, BuscarClientesCommand command) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("usuarioId"), usuarioId));

            if (command.getBuscarGeral() != null && !command.getBuscarGeral().isBlank()) {
                String buscaGeral = "%" + command.getBuscarGeral().toLowerCase() + "%";

                Predicate nomeMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nome")),
                        buscaGeral
                );

                Predicate emailMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        buscaGeral
                );

                Predicate cpfMatch = criteriaBuilder.like(
                        root.get("cpf"),
                        buscaGeral
                );

                predicates.add(criteriaBuilder.or(nomeMatch, emailMatch, cpfMatch));
            }

            // Filtros específicos
            if (command.getNome() != null && !command.getNome().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nome")),
                        "%" + command.getNome().toLowerCase() + "%"
                ));
            }

            if (command.getEmail() != null && !command.getEmail().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        "%" + command.getEmail().toLowerCase() + "%"
                ));
            }

            if (command.getCpf() != null && !command.getCpf().isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("cpf"),
                        command.getCpf().replaceAll("[^0-9]", "")
                ));
            }

            if (command.getTelefone() != null && !command.getTelefone().isBlank()) {
                predicates.add(criteriaBuilder.like(
                        root.get("telefone"),
                        "%" + command.getTelefone().replaceAll("[^0-9]", "") + "%"
                ));
            }

            if (command.getAtivo() != null) {
                predicates.add(criteriaBuilder.equal(root.get("ativo"), command.getAtivo()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}