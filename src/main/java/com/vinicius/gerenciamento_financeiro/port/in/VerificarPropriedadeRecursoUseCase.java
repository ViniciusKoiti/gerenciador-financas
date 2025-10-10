package com.vinicius.gerenciamento_financeiro.port.in;

import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;

public interface VerificarPropriedadeRecursoUseCase {

    boolean verificarPropriedadeCategoria(CategoriaId categoriaId);
}
