package com.vinicius.gerenciamento_financeiro.port.in;

import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Credencial;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.SessaoUsuario;

/**
 * Port de entrada para operações de autenticação.
 * Define contratos usando APENAS tipos de domínio.
 */
public interface LoginUseCase {
    
    /**
     * Autentica um usuário com suas credenciais
     */
    SessaoUsuario autenticar(Credencial credencial);
}



