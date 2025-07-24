package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.cliente;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.AuditoriaResponseMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.cliente.ClienteResponse;

import com.vinicius.gerenciamento_financeiro.domain.model.cliente.Cliente;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.PixInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClienteResponseMapper {

    private final EnderecoResponseMapper enderecoMapper;
    private final AuditoriaResponseMapper auditoriaMapper;
    private final PixInfoResponseMapper pixInfoMapper;

    public ClienteResponse toResponse(Cliente cliente) {
        if (cliente == null) {
            return null;
        }

        return new ClienteResponse(
                cliente.getClienteId() != null ? cliente.getClienteId().getValue() : null,
                cliente.getNome(),
                cliente.getCpf().getNumero(),
                cliente.getEmail().getEndereco(),
                cliente.getTelefone(),
                cliente.getDataNascimento(),
                enderecoMapper.toResponse(cliente.getEndereco()),
                cliente.getUsuarioId().getValue(),
                pixInfoMapper.toResponse(cliente.getPixInfo()),
                cliente.isAtivo(),
                auditoriaMapper.toResponse(cliente.getAuditoria())
        );
    }

    public List<ClienteResponse> toResponseList(List<Cliente> clientes) {
        if (clientes == null) {
            return List.of();
        }

        return clientes.stream()
                .map(this::toResponse)
                .toList();
    }

}

