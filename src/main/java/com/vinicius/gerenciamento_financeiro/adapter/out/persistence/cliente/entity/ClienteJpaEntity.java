package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.auditoria.AuditoriaJpa;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.usuario.entity.UsuarioJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.PixInfo;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.pessoa.entity.PessoaJpaEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "cliente")
@Builder
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClienteJpaEntity extends PessoaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private PixInfoJpa pixInfo;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private UsuarioJpaEntity usuario;

    @Embedded
    private AuditoriaJpa auditoria;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClienteJpaEntity cliente = (ClienteJpaEntity) o;
        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
