package com.vinicius.gerenciamento_financeiro.domain.model.pessoa;

import com.vinicius.gerenciamento_financeiro.domain.model.endereco.Endereco;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Pessoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private Cpf cpf;

    @Column(nullable = false, unique = true)
    private Email email;

    private String telefone;
    private LocalDate dataNascimento;

    @Embedded
    private Endereco endereco;
}