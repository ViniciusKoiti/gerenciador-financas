package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.auditoria.AuditoriaJpa;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.categoria.entity.CategoriaJpaEntity;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.usuario.entity.UsuarioJpaEntity;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoMovimentacao;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoRecorrencia;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transacoes")
@EntityListeners(AuditingEntityListener.class)
public class TransacaoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentacao tipo;

    @Column(nullable = false)
    private LocalDateTime data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioJpaEntity usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaJpaEntity categoria;

    @Column(name = "pago")
    private Boolean pago = false;

    @Column(name = "recorrente")
    private Boolean recorrente = false;

    @Column(name = "periodicidade")
    private Integer periodicidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_recorrencia")
    private TipoRecorrencia tipoRecorrencia;

    @Column(name = "ignorar_limite_categoria")
    private Boolean ignorarLimiteCategoria = false;

    @Column(name = "ignorar_orcamento")
    private Boolean ignorarOrcamento = false;

    @Column(name = "parcelado")
    private Boolean parcelado = false;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @Embedded
    private AuditoriaJpa auditoria;

    public void marcarComoPaga() {
        this.pago = true;
        this.dataPagamento = LocalDateTime.now();
    }

    public void desmarcarPagamento() {
        this.pago = false;
        this.dataPagamento = null;
    }

    public boolean estaVencida() {
        if (pago || dataVencimento == null) {
            return false;
        }
        return LocalDate.now().isAfter(dataVencimento);
    }

    public boolean isRecorrente() {
        return Boolean.TRUE.equals(recorrente);
    }

    public boolean isParcelada() {
        return Boolean.TRUE.equals(parcelado);
    }

    public boolean isPaga() {
        return Boolean.TRUE.equals(pago);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransacaoJpaEntity that = (TransacaoJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("TransacaoJpaEntity{id=%d, descricao='%s', valor=%s, tipo=%s}",
                id, descricao, valor, tipo);
    }
}