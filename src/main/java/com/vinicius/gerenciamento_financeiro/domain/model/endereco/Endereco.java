package com.vinicius.gerenciamento_financeiro.domain.model.endereco;

import java.util.Objects;

public final class Endereco {

    private final String logradouro;
    private final String numeroEndereco;
    private final String complemento;
    private final String bairro;
    private final String cidade;
    private final String estado;
    private final String cep;

    private Endereco(Builder builder) {
        this.logradouro = validarLogradouro(builder.logradouro);
        this.numeroEndereco = builder.numeroEndereco;
        this.complemento = builder.complemento;
        this.bairro = validarBairro(builder.bairro);
        this.cidade = validarCidade(builder.cidade);
        this.estado = validarEstado(builder.estado);
        this.cep = validarCep(builder.cep);
    }

    public static Endereco criar(String logradouro, String numeroEndereco, String bairro,
                                 String cidade, String estado, String cep) {
        return new Builder()
                .logradouro(logradouro)
                .numeroEndereco(numeroEndereco)
                .bairro(bairro)
                .cidade(cidade)
                .estado(estado)
                .cep(cep)
                .build();
    }

    public static Endereco criarCompleto(String logradouro, String numeroEndereco, String complemento,
                                         String bairro, String cidade, String estado, String cep) {
        return new Builder()
                .logradouro(logradouro)
                .numeroEndereco(numeroEndereco)
                .complemento(complemento)
                .bairro(bairro)
                .cidade(cidade)
                .estado(estado)
                .cep(cep)
                .build();
    }

    public Endereco alterarComplemento(String novoComplemento) {
        return new Builder()
                .from(this)
                .complemento(novoComplemento)
                .build();
    }

    public String getEnderecoCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append(logradouro);

        if (numeroEndereco != null && !numeroEndereco.trim().isEmpty()) {
            sb.append(", ").append(numeroEndereco);
        }

        if (complemento != null && !complemento.trim().isEmpty()) {
            sb.append(", ").append(complemento);
        }

        sb.append(" - ").append(bairro)
                .append(", ").append(cidade)
                .append(" - ").append(estado)
                .append(" ").append(cep);

        return sb.toString();
    }

    public boolean isCompleto() {
        return logradouro != null && !logradouro.trim().isEmpty() &&
                bairro != null && !bairro.trim().isEmpty() &&
                cidade != null && !cidade.trim().isEmpty() &&
                estado != null && !estado.trim().isEmpty() &&
                cep != null && !cep.trim().isEmpty();
    }

    // Validações
    private String validarLogradouro(String logradouro) {
        if (logradouro == null || logradouro.trim().isEmpty()) {
            throw new IllegalArgumentException("Logradouro é obrigatório");
        }
        if (logradouro.length() > 200) {
            throw new IllegalArgumentException("Logradouro não pode ter mais de 200 caracteres");
        }
        return logradouro.trim();
    }

    private String validarBairro(String bairro) {
        if (bairro == null || bairro.trim().isEmpty()) {
            throw new IllegalArgumentException("Bairro é obrigatório");
        }
        if (bairro.length() > 100) {
            throw new IllegalArgumentException("Bairro não pode ter mais de 100 caracteres");
        }
        return bairro.trim();
    }

    private String validarCidade(String cidade) {
        if (cidade == null || cidade.trim().isEmpty()) {
            throw new IllegalArgumentException("Cidade é obrigatória");
        }
        if (cidade.length() > 100) {
            throw new IllegalArgumentException("Cidade não pode ter mais de 100 caracteres");
        }
        return cidade.trim();
    }

    private String validarEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("Estado é obrigatório");
        }
        if (estado.length() != 2) {
            throw new IllegalArgumentException("Estado deve ter exatamente 2 caracteres (UF)");
        }
        return estado.trim().toUpperCase();
    }

    private String validarCep(String cep) {
        if (cep == null || cep.trim().isEmpty()) {
            throw new IllegalArgumentException("CEP é obrigatório");
        }

        String cepLimpo = cep.replaceAll("[^0-9]", "");

        if (cepLimpo.length() != 8) {
            throw new IllegalArgumentException("CEP deve ter 8 dígitos");
        }

        return formatarCep(cepLimpo);
    }

    private String formatarCep(String cep) {
        return cep.replaceAll("(\\d{5})(\\d{3})", "$1-$2");
    }

    // Getters
    public String getLogradouro() { return logradouro; }
    public String getNumeroEndereco() { return numeroEndereco; }
    public String getComplemento() { return complemento; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public String getCep() { return cep; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endereco endereco = (Endereco) o;
        return Objects.equals(logradouro, endereco.logradouro) &&
                Objects.equals(numeroEndereco, endereco.numeroEndereco) &&
                Objects.equals(complemento, endereco.complemento) &&
                Objects.equals(bairro, endereco.bairro) &&
                Objects.equals(cidade, endereco.cidade) &&
                Objects.equals(estado, endereco.estado) &&
                Objects.equals(cep, endereco.cep);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logradouro, numeroEndereco, complemento, bairro, cidade, estado, cep);
    }

    @Override
    public String toString() {
        return getEnderecoCompleto();
    }

    // Builder Pattern
    public static class Builder {
        private String logradouro;
        private String numeroEndereco;
        private String complemento;
        private String bairro;
        private String cidade;
        private String estado;
        private String cep;

        public Builder logradouro(String logradouro) { this.logradouro = logradouro; return this; }
        public Builder numeroEndereco(String numeroEndereco) { this.numeroEndereco = numeroEndereco; return this; }
        public Builder complemento(String complemento) { this.complemento = complemento; return this; }
        public Builder bairro(String bairro) { this.bairro = bairro; return this; }
        public Builder cidade(String cidade) { this.cidade = cidade; return this; }
        public Builder estado(String estado) { this.estado = estado; return this; }
        public Builder cep(String cep) { this.cep = cep; return this; }

        public Builder from(Endereco endereco) {
            this.logradouro = endereco.logradouro;
            this.numeroEndereco = endereco.numeroEndereco;
            this.complemento = endereco.complemento;
            this.bairro = endereco.bairro;
            this.cidade = endereco.cidade;
            this.estado = endereco.estado;
            this.cep = endereco.cep;
            return this;
        }

        public Endereco build() {
            return new Endereco(this);
        }
    }
}
