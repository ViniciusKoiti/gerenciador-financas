package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.specification;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.cliente.ClienteFiltroRequest;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.entity.ClienteJpaEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

/**
 * Builder de Specifications - APENAS camada de infraestrutura.
 * Trabalha exclusivamente com DTOs e entidades JPA.
 * NÃO conhece objetos de domínio.
 */
@Component
public class ClienteSpecificationBuilder {

    public Specification<ClienteJpaEntity> build(Long usuarioId, ClienteFiltroRequest filtros) {
        // ✅ Validações básicas de infraestrutura
        if (usuarioId == null) {
            throw new IllegalArgumentException("UsuarioId não pode ser nulo");
        }

        // ✅ Sempre começar com filtro obrigatório do usuário
        Specification<ClienteJpaEntity> spec = ClienteSpecification.temUsuarioId(usuarioId);

        // Se não há filtros, retorna apenas o filtro de usuário
        if (filtros == null || !filtros.temFiltros()) {
            return spec;
        }

        return adicionarFiltros(spec, filtros);
    }


    private Specification<ClienteJpaEntity> adicionarFiltros(
            Specification<ClienteJpaEntity> spec,
            ClienteFiltroRequest filtros) {

        if (filtros.temBuscaGeral()) {
            spec = spec.and(ClienteSpecification.nomeOuEmail(filtros.buscaGeral()));
            spec = adicionarFiltrosComplementares(spec, filtros);
        } else {
            spec = aplicarTodosFiltros(spec, filtros);
        }

        return spec;
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

    private Specification<ClienteJpaEntity> adicionarFiltrosComplementares(
            Specification<ClienteJpaEntity> spec,
            ClienteFiltroRequest filtros) {

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

        if (temValor(filtros.telefone())) {
            spec = spec.and(ClienteSpecification.telefoneContains(filtros.telefone()));
        }

        return spec;
    }



    public Specification<ClienteJpaEntity> apenasUsuario(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("UsuarioId não pode ser nulo");
        }
        return ClienteSpecification.temUsuarioId(usuarioId);
    }

    public Specification<ClienteJpaEntity> buscaRapida(Long usuarioId, String texto) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("UsuarioId não pode ser nulo");
        }

        Specification<ClienteJpaEntity> spec = ClienteSpecification.temUsuarioId(usuarioId);

        if (texto != null && !texto.trim().isEmpty()) {
            spec = spec.and(ClienteSpecification.nomeOuEmail(texto));
        }

        return spec;
    }

    public Specification<ClienteJpaEntity> apenasAtivos(Long usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("UsuarioId não pode ser nulo");
        }

        return ClienteSpecification.temUsuarioId(usuarioId)
                .and(ClienteSpecification.ativo(true));
    }

    private boolean temValor(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

    private boolean temPeriodoNascimento(ClienteFiltroRequest filtros) {
        return filtros.dataNascimentoInicio() != null || filtros.dataNascimentoFim() != null;
    }
}