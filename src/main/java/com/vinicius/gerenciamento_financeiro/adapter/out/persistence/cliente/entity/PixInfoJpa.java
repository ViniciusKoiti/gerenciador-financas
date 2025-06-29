package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PixInfoJpa {

    @Column(name = "pix_chave", length = 100)
    private String chave;

    @Enumerated(EnumType.STRING)
    @Column(name = "pix_tipo", length = 20)
    private TipoChavePixJpa tipo;

    @Column(name = "pix_banco", length = 100)
    private String banco;

    @Column(name = "pix_ativo")
    private Boolean ativo;

    public enum TipoChavePixJpa {
        CPF, EMAIL, TELEFONE, ALEATORIA
    }

    public boolean isAtivo() {
        return Boolean.TRUE.equals(ativo);
    }
}