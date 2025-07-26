package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.specification;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.cliente.ClienteFiltroRequest;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.entity.ClienteJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.exception.ClienteException;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ClienteSpecificationBuilder {

    public Specification<ClienteJpaEntity> buildSpecification(UsuarioId usuarioId, ClienteFiltroRequest filtros) {
        validarParametros(usuarioId, filtros);

        Specification<ClienteJpaEntity> spec = ClienteSpecification.temUsuarioId(usuarioId.getValue());


        if (filtros == null || !filtros.temFiltros()) {
            return spec;
        }

        return adicionarFiltros(spec, filtros);
    }

    private Specification<ClienteJpaEntity> adicionarFiltros(
            Specification<ClienteJpaEntity> spec,
            ClienteFiltroRequest filtros) {

        if (filtros.temBuscaGeral()) {
            return spec.and(ClienteSpecification.nomeOuEmail(filtros.buscaGeral()))
                    .and(aplicarFiltrosComplementares(filtros));
        }

        return aplicarTodosFiltros(spec, filtros);
    }

    private Specification<ClienteJpaEntity> aplicarTodosFiltros(
            Specification<ClienteJpaEntity> spec,
            ClienteFiltroRequest filtros) {

        if (filtros.temFiltroNome()) {
            spec = spec.and(ClienteSpecification.nomeContains(filtros.nome()));
        }

        if (temValor(filtros.email())) {
            spec = spec.and(ClienteSpecification.emailContains(filtros.email()));
        }

        if (temValor(filtros.cpf())) {
            spec = spec.and(ClienteSpecification.cpfContains(filtros.cpf()));
        }

        if (temValor(filtros.telefone())) {
            spec = spec.and(ClienteSpecification.telefoneContains(filtros.telefone()));
        }

        if (filtros.ativo() != null) {
            spec = spec.and(ClienteSpecification.ativo(filtros.ativo()));
        }

        if (temPeriodoNascimento(filtros)) {
            spec = spec.and(ClienteSpecification.dataNascimentoEntre(
                    filtros.dataNascimentoInicio(),
                    filtros.dataNascimentoFim()
            ));
        }

        return spec;
    }

    private Specification<ClienteJpaEntity> aplicarFiltrosComplementares(ClienteFiltroRequest filtros) {
        Specification<ClienteJpaEntity> spec = (root, query, builder) -> null;

        if (temValor(filtros.cpf())) {
            spec = spec.and(ClienteSpecification.cpfContains(filtros.cpf()));
        }

        if (filtros.ativo() != null) {
            spec = spec.and(ClienteSpecification.ativo(filtros.ativo()));
        }

        if (temPeriodoNascimento(filtros)) {
            spec = spec.and(ClienteSpecification.dataNascimentoEntre(
                    filtros.dataNascimentoInicio(),
                    filtros.dataNascimentoFim()
            ));
        }

        return spec;
    }

    private void validarParametros(UsuarioId usuarioId, ClienteFiltroRequest filtros) {
        if (usuarioId == null) {
            throw ClienteException.filtrosInvalidos("UsuarioId não pode ser nulo");
        }

        if (filtros != null && filtros.temBuscaGeral() && filtros.buscaGeral().trim().length() < 2) {
            throw ClienteException.filtrosInvalidos("Busca geral deve ter pelo menos 2 caracteres");
        }

        if (filtros != null && temPeriodoNascimento(filtros)) {
            validarPeriodoNascimento(filtros);
        }
    }

    private void validarPeriodoNascimento(ClienteFiltroRequest filtros) {
        if (filtros.dataNascimentoInicio() != null &&
                filtros.dataNascimentoFim() != null &&
                filtros.dataNascimentoInicio().isAfter(filtros.dataNascimentoFim())) {

            throw ClienteException.filtrosInvalidos(
                    "Data de início não pode ser posterior à data de fim"
            );
        }
    }

    private boolean temValor(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

    private boolean temPeriodoNascimento(ClienteFiltroRequest filtros) {
        return filtros.dataNascimentoInicio() != null || filtros.dataNascimentoFim() != null;
    }
}