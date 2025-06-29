package com.vinicius.gerenciamento_financeiro.domain.model.cliente;

import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.endereco.Endereco;
import com.vinicius.gerenciamento_financeiro.domain.model.pessoa.Cpf;
import com.vinicius.gerenciamento_financeiro.domain.model.pessoa.Email;
import com.vinicius.gerenciamento_financeiro.domain.model.pessoa.Pessoa;
import com.vinicius.gerenciamento_financeiro.domain.model.pessoa.PessoaId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;

import java.time.LocalDate;
import java.util.Objects;

public final class Cliente extends Pessoa {

    private final ClienteId clienteId;
    private final UsuarioId usuarioId;
    private final PixInfo pixInfo;
    private final boolean ativo;

    private Cliente(Builder builder) {
        super(builder);
        this.clienteId = builder.clienteId;
        this.usuarioId = Objects.requireNonNull(builder.usuarioId, "UsuarioId não pode ser nulo");
        this.pixInfo = builder.pixInfo;
        this.ativo = builder.ativo;
    }

    public static Cliente criarNovo(String nome, Cpf cpf, Email email, UsuarioId usuarioId) {
        return new Builder()
                .nome(nome)
                .cpf(cpf)
                .email(email)
                .usuarioId(usuarioId)
                .ativo(true)
                .build();
    }

    public static Cliente criarCompleto(String nome, Cpf cpf, Email email, String telefone,
                                        LocalDate dataNascimento, Endereco endereco,
                                        UsuarioId usuarioId, PixInfo pixInfo) {
        return new Builder()
                .nome(nome)
                .cpf(cpf)
                .email(email)
                .telefone(telefone)
                .dataNascimento(dataNascimento)
                .endereco(endereco)
                .usuarioId(usuarioId)
                .pixInfo(pixInfo)
                .ativo(true)
                .build();
    }

    public static Cliente reconstituir(Long id, String nome, Cpf cpf, Email email, String telefone,
                                       LocalDate dataNascimento, Endereco endereco, UsuarioId usuarioId,
                                       PixInfo pixInfo, boolean ativo, Auditoria auditoria) {
        return new Builder()
                .clienteId(ClienteId.of(id))
                .id(PessoaId.of(id))
                .nome(nome)
                .cpf(cpf)
                .email(email)
                .telefone(telefone)
                .dataNascimento(dataNascimento)
                .endereco(endereco)
                .usuarioId(usuarioId)
                .pixInfo(pixInfo)
                .ativo(ativo)
                .auditoria(auditoria)
                .build();
    }

    public Cliente adicionarPixInfo(PixInfo novaPixInfo) {
        if (this.pixInfo != null) {
            throw new IllegalStateException("Cliente já possui informações PIX configuradas");
        }

        return new Builder()
                .from(this)
                .pixInfo(novaPixInfo)
                .auditoria(this.auditoria.marcarComoAtualizado())
                .build();
    }

    public Cliente atualizarPixInfo(PixInfo novaPixInfo) {
        return new Builder()
                .from(this)
                .pixInfo(novaPixInfo)
                .auditoria(this.auditoria.marcarComoAtualizado())
                .build();
    }

    public Cliente ativar() {
        if (this.ativo) {
            return this;
        }

        return new Builder()
                .from(this)
                .ativo(true)
                .auditoria(this.auditoria.marcarComoAtualizado())
                .build();
    }

    public Cliente desativar() {
        if (!this.ativo) {
            return this;
        }

        return new Builder()
                .from(this)
                .ativo(false)
                .auditoria(this.auditoria.marcarComoAtualizado())
                .build();
    }

    @Override
    protected Pessoa criarComDadosAtualizados(String nome, Email email, String telefone, Endereco endereco) {
        return new Builder()
                .from(this)
                .nome(nome)
                .email(email)
                .telefone(telefone)
                .endereco(endereco)
                .auditoria(this.auditoria.marcarComoAtualizado())
                .build();
    }

    public boolean pertenceAoUsuario(UsuarioId usuarioId) {
        return this.usuarioId.equals(usuarioId);
    }

    public boolean possuiPix() {
        return this.pixInfo != null && this.pixInfo.isAtivo();
    }

    public boolean isNovo() {
        return this.clienteId == null;
    }
    public ClienteId getClienteId() { return clienteId; }
    public UsuarioId getUsuarioId() { return usuarioId; }
    public PixInfo getPixInfo() { return pixInfo; }
    public boolean isAtivo() { return ativo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;

        if (clienteId != null && cliente.clienteId != null) {
            return Objects.equals(clienteId, cliente.clienteId);
        }

        // Para clientes sem ID, usar comparação da superclasse (CPF)
        return super.equals(o) && Objects.equals(usuarioId, cliente.usuarioId);
    }

    @Override
    public int hashCode() {
        if (clienteId != null) {
            return Objects.hash(clienteId);
        }
        return Objects.hash(super.hashCode(), usuarioId);
    }

    @Override
    public String toString() {
        return String.format("Cliente{id=%s, nome='%s', cpf=%s, usuario=%s, ativo=%s}",
                clienteId != null ? clienteId.getValue() : "novo",
                getNome(),
                getCpf().getNumero(),
                usuarioId.getValue(),
                ativo);
    }
    public static class Builder extends Pessoa.Builder<Builder> {
        private ClienteId clienteId;
        private UsuarioId usuarioId;
        private PixInfo pixInfo;
        private boolean ativo = true;

        public Builder clienteId(ClienteId clienteId) {
            this.clienteId = clienteId;
            return this;
        }

        public Builder usuarioId(UsuarioId usuarioId) {
            this.usuarioId = usuarioId;
            return this;
        }

        public Builder pixInfo(PixInfo pixInfo) {
            this.pixInfo = pixInfo;
            return this;
        }

        public Builder ativo(boolean ativo) {
            this.ativo = ativo;
            return this;
        }

        public Builder from(Cliente cliente) {
            // Copiar dados específicos do Cliente
            this.clienteId = cliente.clienteId;
            this.usuarioId = cliente.usuarioId;
            this.pixInfo = cliente.pixInfo;
            this.ativo = cliente.ativo;

            // Copiar dados da Pessoa (usando campos protected da superclasse)
            this.id = cliente.id;
            this.nome = cliente.nome;
            this.cpf = cliente.cpf;
            this.email = cliente.email;
            this.telefone = cliente.telefone;
            this.dataNascimento = cliente.dataNascimento;
            this.endereco = cliente.endereco;
            this.auditoria = cliente.auditoria;

            return this;
        }

        @Override
        public Cliente build() {
            return new Cliente(this);
        }
    }
}