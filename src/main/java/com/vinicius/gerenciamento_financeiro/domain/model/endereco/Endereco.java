package com.vinicius.gerenciamento_financeiro.domain.model.endereco;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {
    private String logradouro;
    private String numeroEndereco;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
}