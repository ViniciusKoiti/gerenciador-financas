package com.vinicius.gerenciamento_financeiro.domain.model.moeda;

import java.util.Objects;

public final  class MoedaId {

    private final String valor;

    private MoedaId(String valor){
        this.valor = Objects.requireNonNull(valor);
    }

    public static MoedaId of(String valor){
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("MoedaId não pode ser vazio");
        }
        if (valor.length() != 3) {
            throw new IllegalArgumentException("MoedaId deve ter exatamente 3 caracteres");
        }
        return new MoedaId(valor.toUpperCase().trim());
    }

    public String getValor(){
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoedaId moedaId = (MoedaId) o;
        return Objects.equals(valor, moedaId.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return "MoedaId{" +
                "valor='" + valor + '\'' +
                '}';
    }
}
