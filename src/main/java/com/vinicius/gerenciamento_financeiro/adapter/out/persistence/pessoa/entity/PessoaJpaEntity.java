package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.pessoa.entity;

import com.vinicius.gerenciamento_financeiro.domain.model.pessoa.Cpf;
import com.vinicius.gerenciamento_financeiro.domain.model.pessoa.Email;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "pessoa")
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class PessoaJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String telefone;

    @Column
    private LocalDate dataNascimento;

    @Embedded
    private EnderecoJpaEntity enderecoJpaEntity;
}