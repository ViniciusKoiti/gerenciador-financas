package com.vinicius.gerenciamento_financeiro.domain.model.transacao;

import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "transacoes")
public class Transacao {

    public enum Tipo {
        RECEITA, DESPESA, TRANSFERENCIA
    }
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter
    private String descricao;
    @Getter
    private BigDecimal valor;

    @Getter
    private Tipo tipo;
    @Getter
    private LocalDateTime data;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Transacao(Long id,
                     String descricao,
                     BigDecimal valor,
                     Tipo tipo,
                     LocalDateTime data,
                     Usuario usuario) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
        this.data = data;
        this.usuario = usuario;
    }

    public Transacao() {

    }

    public BigDecimal calcularValorComTaxa(BigDecimal taxa) {
        return valor.add(valor.multiply(taxa));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transacao transacao = (Transacao) o;
        return Objects.equals(id, transacao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
