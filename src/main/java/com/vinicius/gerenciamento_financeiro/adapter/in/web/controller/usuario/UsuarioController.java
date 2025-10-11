package com.vinicius.gerenciamento_financeiro.adapter.in.web.controller.usuario;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.ApiResponseSistema;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.LoginRequest;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.UsuarioPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.AuthenticationResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.UsuarioResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.service.usuario.UsuarioWebService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioWebService usuarioWebService;

    @PostMapping("/registrar")
    public ResponseEntity<ApiResponseSistema<AuthenticationResponse>> registrar(@RequestBody @Valid UsuarioPost request) {
        AuthenticationResponse response = usuarioWebService.registrarUsuario(request);
        return ResponseEntity.ok(ApiResponseSistema.success(response, "UsuÃ¡rio registrado com sucesso"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseSistema<AuthenticationResponse>> login(@RequestBody @Valid LoginRequest request) {
        AuthenticationResponse response = usuarioWebService.autenticar(request);
        return ResponseEntity.ok(ApiResponseSistema.success(response, "Login realizado com sucesso"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseSistema<UsuarioResponse>> buscarPorId(@PathVariable Long id) {
        UsuarioResponse response = usuarioWebService.buscarPorId(id);
        return ResponseEntity.ok(ApiResponseSistema.success(response));
    }
}

