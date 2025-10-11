package com.vinicius.gerenciamento_financeiro.application.command.cliente;

import lombok.Builder;
import lombok.Value;

/**
 * Comando para buscar clientes com filtros
 */
@Value
@Builder
public class BuscarClientesCommand {
    String nome;
    String email;
    String cpf;
    String buscarGeral;
    String telefone;
    Boolean ativo;

    public static BuscarClientesCommand semFiltros() {
        return BuscarClientesCommand.builder().build();
    }

    public static BuscarClientesCommand ativos() {
        return BuscarClientesCommand.builder().ativo(true).build();
    }

    public boolean temAlgumFiltro() {
        return nome != null || email != null || cpf != null ||
                telefone != null || ativo != null;
    }


}