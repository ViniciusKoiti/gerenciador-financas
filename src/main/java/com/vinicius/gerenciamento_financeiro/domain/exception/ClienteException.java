package com.vinicius.gerenciamento_financeiro.domain.exception;

import com.vinicius.gerenciamento_financeiro.domain.exception.DomainException;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;

import java.util.Map;

public class ClienteException extends DomainException {

    private ClienteException(String errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }

    public static ClienteException naoEncontrado(ClienteId clienteId) {
        return new ClienteException(
                "CLIENTE_NAO_ENCONTRADO",
                "Cliente não encontrado com o ID informado",
                Map.of("clienteId", clienteId.getValue())
        );
    }

    public static ClienteException naoEncontradoParaUsuario(ClienteId clienteId, UsuarioId usuarioId) {
        return new ClienteException(
                "CLIENTE_NAO_ENCONTRADO_PARA_USUARIO",
                "Cliente não encontrado ou você não tem permissão para acessá-lo",
                Map.of(
                        "clienteId", clienteId.getValue(),
                        "usuarioId", usuarioId.getValue()
                )
        );
    }

    public static ClienteException filtrosInvalidos(String motivo) {
        return new ClienteException(
                "FILTROS_INVALIDOS",
                "Filtros de busca inválidos: " + motivo,
                Map.of("motivo", motivo)
        );
    }

    public static ClienteException cpfJaExiste(String cpf) {
        return new ClienteException(
                "CPF_JA_EXISTE",
                "Já existe um cliente cadastrado com este CPF",
                Map.of("cpf", cpf)
        );
    }

    public static ClienteException emailJaExiste(String email) {
        return new ClienteException(
                "EMAIL_JA_EXISTE",
                "Já existe um cliente cadastrado com este email",
                Map.of("email", email)
        );
    }

    public static ClienteException dadosObrigatorios(String campo) {
        return new ClienteException(
                "DADOS_OBRIGATORIOS_AUSENTES",
                "Campo obrigatório não informado: " + campo,
                Map.of("campo", campo)
        );
    }
}