package com.vinicius.gerenciamento_financeiro.application.command.categoria;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CriarCategoriaCommand {
    String nome;
    String descricao;
    String icone;

    public void validar() {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome da categoria é obrigatório");
        }
        if (nome.length() > 50) {
            throw new IllegalArgumentException("Nome da categoria não pode ter mais de 50 caracteres");
        }
    }
}