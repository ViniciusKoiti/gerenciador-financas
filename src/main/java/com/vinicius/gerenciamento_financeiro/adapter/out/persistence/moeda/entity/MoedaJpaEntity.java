package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.moeda.entity;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.auditoria.AuditoriaJpa;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.Moeda;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MoedaId;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "moeda")
@EntityListeners(AuditingEntityListener.class)
public class MoedaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 3)
    private String codigo;

    @Column(name = "simbolo", nullable = false, length = 10)
    private String simbolo;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "casas_decimais", nullable = false)
    private Integer casasDecimais;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    @Embedded
    private AuditoriaJpa auditoria;



    public boolean isEuro() {
        return "EUR".equals(this.codigo);
    }

    public boolean isDolar() {
        return "USD".equals(this.codigo);
    }

    public boolean isReal() {
        return "BRL".equals(this.codigo);
    }

    public boolean isAtiva() {
        return Boolean.TRUE.equals(ativo);
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoedaJpaEntity that = (MoedaJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("MoedaJpaEntity{id=%d, codigo='%s', simbolo='%s', nome='%s', ativo=%s}",
                id, codigo, simbolo, nome, ativo);
    }
}