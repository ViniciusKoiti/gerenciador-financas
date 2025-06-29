package com.vinicius.gerenciamento_financeiro.domain.model.cliente;

import java.util.Objects;

public final class PixInfo {

    private final String chave;
    private final TipoChavePix tipo;
    private final String banco;
    private final boolean ativo;

    public enum TipoChavePix {
        CPF, EMAIL, TELEFONE, ALEATORIA
    }

    private PixInfo(String chave, TipoChavePix tipo, String banco, boolean ativo) {
        this.chave = validarChave(chave, tipo);
        this.tipo = Objects.requireNonNull(tipo, "Tipo da chave PIX não pode ser nulo");
        this.banco = banco;
        this.ativo = ativo;
    }

    public static PixInfo criar(String chave, TipoChavePix tipo, String banco) {
        return new PixInfo(chave, tipo, banco, true);
    }

    public static PixInfo desativado(String chave, TipoChavePix tipo, String banco) {
        return new PixInfo(chave, tipo, banco, false);
    }

    public PixInfo ativar() {
        return new PixInfo(this.chave, this.tipo, this.banco, true);
    }

    public PixInfo desativar() {
        return new PixInfo(this.chave, this.tipo, this.banco, false);
    }

    private String validarChave(String chave, TipoChavePix tipo) {
        if (chave == null || chave.trim().isEmpty()) {
            throw new IllegalArgumentException("Chave PIX não pode ser vazia");
        }

        String chaveLimpa = chave.trim();

        switch (tipo) {
            case CPF -> {
                String cpfLimpo = chaveLimpa.replaceAll("[^0-9]", "");
                if (cpfLimpo.length() != 11) {
                    throw new IllegalArgumentException("Chave PIX CPF deve ter 11 dígitos");
                }
                return cpfLimpo;
            }
            case EMAIL -> {
                if (!chaveLimpa.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                    throw new IllegalArgumentException("Chave PIX email inválida");
                }
                return chaveLimpa.toLowerCase();
            }
            case TELEFONE -> {
                String telefoneLimpo = chaveLimpa.replaceAll("[^0-9]", "");
                if (telefoneLimpo.length() < 10 || telefoneLimpo.length() > 11) {
                    throw new IllegalArgumentException("Chave PIX telefone deve ter 10 ou 11 dígitos");
                }
                return telefoneLimpo;
            }
            case ALEATORIA -> {
                if (chaveLimpa.length() != 32) {
                    throw new IllegalArgumentException("Chave PIX aleatória deve ter 32 caracteres");
                }
                return chaveLimpa;
            }
        }

        return chaveLimpa;
    }

    // Getters
    public String getChave() { return chave; }
    public TipoChavePix getTipo() { return tipo; }
    public String getBanco() { return banco; }
    public boolean isAtivo() { return ativo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PixInfo pixInfo = (PixInfo) o;
        return Objects.equals(chave, pixInfo.chave) && tipo == pixInfo.tipo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(chave, tipo);
    }

    @Override
    public String toString() {
        return String.format("PixInfo{tipo=%s, chave=%s, ativo=%s}", tipo, chave, ativo);
    }
}