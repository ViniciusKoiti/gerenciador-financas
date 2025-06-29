package com.vinicius.gerenciamento_financeiro.domain.model.pessoa;

import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.endereco.Endereco;

import java.time.LocalDate;
import java.util.Objects;

public abstract class Pessoa {

    protected final PessoaId id;
    protected final String nome;
    protected final Cpf cpf;
    protected final Email email;
    protected final String telefone;
    protected final LocalDate dataNascimento;
    protected final Endereco endereco;
    protected final Auditoria auditoria;

    protected Pessoa(Builder<?> builder) {
        this.id = builder.id;
        this.nome = validarNome(builder.nome);
        this.cpf = Objects.requireNonNull(builder.cpf, "CPF não pode ser nulo");
        this.email = Objects.requireNonNull(builder.email, "Email não pode ser nulo");
        this.telefone = validarTelefone(builder.telefone);
        this.dataNascimento = validarDataNascimento(builder.dataNascimento);
        this.endereco = builder.endereco; // Pode ser nulo
        this.auditoria = builder.auditoria != null ? builder.auditoria : Auditoria.criarNova();
    }

    public Pessoa atualizarDados(String novoNome, Email novoEmail, String novoTelefone) {
        return criarComDadosAtualizados(novoNome, novoEmail, novoTelefone, this.endereco);
    }

    public Pessoa atualizarEndereco(Endereco novoEndereco) {
        return criarComDadosAtualizados(this.nome, this.email, this.telefone, novoEndereco);
    }

    protected abstract Pessoa criarComDadosAtualizados(String nome, Email email, String telefone, Endereco endereco);

    public boolean isNova() {
        return this.id == null;
    }

    public boolean possuiEndereco() {
        return this.endereco != null && this.endereco.isCompleto();
    }

    public boolean isMaiorIdade() {
        if (dataNascimento == null) return false;
        return LocalDate.now().minusYears(18).isAfter(dataNascimento) ||
                LocalDate.now().minusYears(18).equals(dataNascimento);
    }
    private String validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        if (nome.length() < 2) {
            throw new IllegalArgumentException("Nome deve ter pelo menos 2 caracteres");
        }
        if (nome.length() > 100) {
            throw new IllegalArgumentException("Nome não pode ter mais de 100 caracteres");
        }
        return nome.trim();
    }

    private String validarTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            return null; // Telefone é opcional
        }

        String telefoneLimpo = telefone.replaceAll("[^0-9]", "");

        if (telefoneLimpo.length() < 10 || telefoneLimpo.length() > 11) {
            throw new IllegalArgumentException("Telefone deve ter 10 ou 11 dígitos");
        }

        return formatarTelefone(telefoneLimpo);
    }

    private String formatarTelefone(String telefone) {
        if (telefone.length() == 10) {
            return telefone.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        } else {
            return telefone.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        }
    }

    private LocalDate validarDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            return null; // Data pode ser opcional dependendo do contexto
        }

        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento não pode ser no futuro");
        }

        if (dataNascimento.isBefore(LocalDate.now().minusYears(150))) {
            throw new IllegalArgumentException("Data de nascimento muito antiga");
        }

        return dataNascimento;
    }

    // Getters
    public PessoaId getId() { return id; }
    public String getNome() { return nome; }
    public Cpf getCpf() { return cpf; }
    public Email getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public Endereco getEndereco() { return endereco; }
    public Auditoria getAuditoria() { return auditoria; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pessoa pessoa = (Pessoa) o;

        if (id != null && pessoa.id != null) {
            return Objects.equals(id, pessoa.id);
        }

        // Para pessoas sem ID, comparar por CPF (único)
        return Objects.equals(cpf, pessoa.cpf);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(cpf);
    }

    // Builder Base
    @SuppressWarnings("unchecked")
    public static abstract class Builder<T extends Builder<T>> {
        protected PessoaId id;
        protected String nome;
        protected Cpf cpf;
        protected Email email;
        protected String telefone;
        protected LocalDate dataNascimento;
        protected Endereco endereco;
        protected Auditoria auditoria;

        public T id(PessoaId id) { this.id = id; return (T) this; }
        public T nome(String nome) { this.nome = nome; return (T) this; }
        public T cpf(Cpf cpf) { this.cpf = cpf; return (T) this; }
        public T email(Email email) { this.email = email; return (T) this; }
        public T telefone(String telefone) { this.telefone = telefone; return (T) this; }
        public T dataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; return (T) this; }
        public T endereco(Endereco endereco) { this.endereco = endereco; return (T) this; }
        public T auditoria(Auditoria auditoria) { this.auditoria = auditoria; return (T) this; }

        public abstract Pessoa build();
    }
}