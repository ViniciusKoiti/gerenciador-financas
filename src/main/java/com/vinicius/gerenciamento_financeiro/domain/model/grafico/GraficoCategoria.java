package com.vinicius.gerenciamento_financeiro.domain.model.grafico;

import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MontanteMonetario;

/**
 * Value Object que representa dados de um gráfico por categoria
 */
public final class GraficoCategoria {
    
    private final CategoriaId categoriaId;
    private final String nomeCategoria;
    private final MontanteMonetario valor;
    private final Double percentual;
    
    public GraficoCategoria(CategoriaId categoriaId, String nomeCategoria, 
                           MontanteMonetario valor, Double percentual) {
        if (categoriaId == null) {
            throw new IllegalArgumentException("CategoriaId não pode ser nulo");
        }
        if (nomeCategoria == null || nomeCategoria.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da categoria não pode ser vazio");
        }
        if (valor == null) {
            throw new IllegalArgumentException("Valor não pode ser nulo");
        }
        if (percentual == null || percentual < 0 || percentual > 100) {
            throw new IllegalArgumentException("Percentual deve estar entre 0 e 100");
        }
        
        this.categoriaId = categoriaId;
        this.nomeCategoria = nomeCategoria;
        this.valor = valor;
        this.percentual = percentual;
    }
    
    public static GraficoCategoria criar(CategoriaId categoriaId, String nomeCategoria, 
                                        MontanteMonetario valor, Double percentual) {
        return new GraficoCategoria(categoriaId, nomeCategoria, valor, percentual);
    }
    
    public CategoriaId getCategoriaId() {
        return categoriaId;
    }
    
    public String getNomeCategoria() {
        return nomeCategoria;
    }
    
    public MontanteMonetario getValor() {
        return valor;
    }
    
    public Double getPercentual() {
        return percentual;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        GraficoCategoria that = (GraficoCategoria) o;
        return categoriaId.equals(that.categoriaId);
    }
    
    @Override
    public int hashCode() {
        return categoriaId.hashCode();
    }
    
    @Override
    public String toString() {
        return "GraficoCategoria{" +
                "categoria=" + nomeCategoria +
                ", valor=" + valor +
                ", percentual=" + percentual + "%" +
                '}';
    }
}