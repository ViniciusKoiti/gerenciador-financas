package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.cliente;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.cliente.EnderecoResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.endereco.Endereco;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EnderecoResponseMapper {

    public EnderecoResponse toResponse(Endereco endereco) {
        if (endereco == null) {
            return null;
        }

        return new EnderecoResponse(
                endereco.getCep(),
                endereco.getLogradouro(),
                endereco.getNumeroEndereco(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getEstado(),
                endereco.getEstado() //TODO ADD PAIS
        );
    }

    public List<EnderecoResponse> toResponseList(List<Endereco> enderecos) {
        if (enderecos == null) {
            return List.of();
        }

        return enderecos.stream()
                .map(this::toResponse)
                .toList();
    }

}