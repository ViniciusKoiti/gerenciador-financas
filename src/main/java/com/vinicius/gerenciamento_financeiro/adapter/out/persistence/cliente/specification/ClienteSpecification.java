package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.specification;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.entity.ClienteJpaEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ClienteSpecification {

    private ClienteSpecification() {
    }

    public static Specification<ClienteJpaEntity> temUsuarioId(Long usuarioId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("usuarioId"), usuarioId);
    }

    public static Specification<ClienteJpaEntity> nomeContains(String nome) {
        return (root, query, criteriaBuilder) -> {
            if (nome == null || nome.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("nome")),
                    "%" + nome.toLowerCase() + "%"
            );
        };
    }

    public static Specification<ClienteJpaEntity> cpfContains(String cpf) {
        return (root, query, criteriaBuilder) -> {
            if (cpf == null || cpf.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            // Remove formatação do CPF para busca
            String cpfLimpo = cpf.replaceAll("[^0-9]", "");
            return criteriaBuilder.like(
                    root.get("cpf"),
                    "%" + cpfLimpo + "%"
            );
        };
    }

    public static Specification<ClienteJpaEntity> emailContains(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null || email.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("email")),
                    "%" + email.toLowerCase() + "%"
            );
        };
    }

    public static Specification<ClienteJpaEntity> ativo(Boolean ativo) {
        return (root, query, criteriaBuilder) -> {
            if (ativo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("ativo"), ativo);
        };
    }

    public static Specification<ClienteJpaEntity> telefoneContains(String telefone) {
        return (root, query, criteriaBuilder) -> {
            if (telefone == null || telefone.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String telefoneLimpo = telefone.replaceAll("[^0-9]", "");
            return criteriaBuilder.like(
                    root.get("telefone"),
                    "%" + telefoneLimpo + "%"
            );
        };
    }
    public static Specification<ClienteJpaEntity> nomeOuEmail(String busca) {
        return (root, query, criteriaBuilder) -> {
            if (busca == null || busca.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String buscaLower = "%" + busca.toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), buscaLower),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), buscaLower)
            );
        };
    }

    public static Specification<ClienteJpaEntity> dataNascimentoEntre(LocalDate dataInicio, LocalDate dataFim) {
        return (root, query, criteriaBuilder) -> {
            if (dataInicio == null && dataFim == null) {
                return criteriaBuilder.conjunction();
            }

            if (dataInicio != null && dataFim != null) {
                return criteriaBuilder.between(root.get("dataNascimento"), dataInicio, dataFim);
            }

            if (dataInicio != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("dataNascimento"), dataInicio);
            }

            return criteriaBuilder.lessThanOrEqualTo(root.get("dataNascimento"), dataFim);
        };
    }
}