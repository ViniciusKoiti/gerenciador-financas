package com.vinicius.gerenciamento_financeiro.domain.model.cliente;

import com.vinicius.gerenciamento_financeiro.domain.model.PixInfo;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.pessoa.Pessoa;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cliente")
@Builder
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cliente extends Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private PixInfo pixInfo;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Transacao> transacoes = new ArrayList<>();

    @Embedded
    private Auditoria auditoria;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
