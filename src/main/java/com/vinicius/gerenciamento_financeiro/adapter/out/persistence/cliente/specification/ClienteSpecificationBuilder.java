package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.specification;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.entity.ClienteJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteFiltro;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClienteSpecificationBuilder {

    public Specification<ClienteJpaEntity> build(Long usuarioId, ClienteFiltro filtro) {
        ClienteFiltro filtroEfetivo = filtro != null ? filtro : ClienteFiltro.vazio();

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("usuarioId"), usuarioId));

            if (temTexto(filtroEfetivo.getBuscaGeral())) {
                String buscaGeral = "%" + filtroEfetivo.getBuscaGeral().toLowerCase() + "%";

                Predicate nomeMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nome")),
                        buscaGeral
                );

                Predicate emailMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        buscaGeral
                );

                Predicate cpfMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("cpf")),
                        buscaGeral
                );

                predicates.add(criteriaBuilder.or(nomeMatch, emailMatch, cpfMatch));
            }

            if (temTexto(filtroEfetivo.getNome())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nome")),
                        "%" + filtroEfetivo.getNome().toLowerCase() + "%"
                ));
            }

            if (temTexto(filtroEfetivo.getEmail())) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        "%" + filtroEfetivo.getEmail().toLowerCase() + "%"
                ));
            }

            if (temTexto(filtroEfetivo.getCpf())) {
                predicates.add(criteriaBuilder.equal(
                        root.get("cpf"),
                        filtroEfetivo.getCpf().replaceAll("[^0-9]", "")
                ));
            }

            if (temTexto(filtroEfetivo.getTelefone())) {
                predicates.add(criteriaBuilder.like(
                        root.get("telefone"),
                        "%" + filtroEfetivo.getTelefone().replaceAll("[^0-9]", "") + "%"
                ));
            }

            if (filtroEfetivo.getAtivo() != null) {
                predicates.add(criteriaBuilder.equal(root.get("ativo"), filtroEfetivo.getAtivo()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private boolean temTexto(String valor) {
        return valor != null && !valor.isBlank();
    }
}