package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.cliente;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.exception.RequestInvalidaException;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.cliente.ClienteFiltroRequest;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteFiltro;
import org.springframework.stereotype.Component;

/**
 * Mapper responsável por converter entre ClienteFiltroRequest (DTO) e ClienteFiltro (Domain).
 * Segue o padrão de separação entre camadas da Clean Architecture.
 */
@Component
public class ClienteFiltroMapper {


    public ClienteFiltro toDomain(ClienteFiltroRequest request) {
        if (request == null) {
            throw new RequestInvalidaException("Filtros não podem ser nulos");
        }

        ClienteFiltroRequest requestLimpo = request.limpar();

        try {
            return ClienteFiltro.builder()
                    .nome(requestLimpo.nome())
                    .cpf(requestLimpo.cpf())
                    .email(requestLimpo.email())
                    .telefone(requestLimpo.telefone())
                    .ativo(requestLimpo.ativo())
                    .buscaGeral(requestLimpo.buscaGeral())
                    .dataNascimentoInicio(requestLimpo.dataNascimentoInicio())
                    .dataNascimentoFim(requestLimpo.dataNascimentoFim())
                    .build();

        } catch (IllegalArgumentException ex) {
            throw new RequestInvalidaException("Filtros inválidos: " + ex.getMessage());
        }
    }

    public ClienteFiltroRequest toRequest(ClienteFiltro filtro) {
        if (filtro == null) {
            return new ClienteFiltroRequest(null, null, null, null, null, null, null, null);
        }

        return new ClienteFiltroRequest(
                filtro.nome().orElse(null),
                filtro.cpf().orElse(null),
                filtro.email().orElse(null),
                filtro.telefone().orElse(null),
                filtro.ativo().orElse(null),
                filtro.buscaGeral().orElse(null),
                filtro.dataNascimentoInicio().orElse(null),
                filtro.dataNascimentoFim().orElse(null)
        );
    }

    public ClienteFiltro comBuscaGeral(String buscaGeral) {
        if (buscaGeral == null || buscaGeral.trim().isEmpty()) {
            return ClienteFiltro.vazio();
        }

        ClienteFiltroRequest request = ClienteFiltroRequest.builder()
                .buscaGeral(buscaGeral)
                .build();

        return toDomain(request);
    }

    public ClienteFiltro comStatusAtivo(Boolean ativo) {
        if (ativo == null) {
            return ClienteFiltro.vazio();
        }

        ClienteFiltroRequest request = ClienteFiltroRequest.builder()
                .ativo(ativo)
                .build();

        return toDomain(request);
    }
}
