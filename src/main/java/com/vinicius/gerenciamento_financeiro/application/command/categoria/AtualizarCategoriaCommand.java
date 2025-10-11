package com.vinicius.gerenciamento_financeiro.application.command.categoria;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AtualizarCategoriaCommand {
    String nome;
    String descricao;
    String icone;
    Boolean ativa;
}