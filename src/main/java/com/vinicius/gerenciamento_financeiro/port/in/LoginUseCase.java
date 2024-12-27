package com.vinicius.gerenciamento_financeiro.port.in;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.LoginRequest;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.response.AuthenticationResponse;

public interface LoginUseCase{


    AuthenticationResponse autenticar(LoginRequest loginRequest);



}



