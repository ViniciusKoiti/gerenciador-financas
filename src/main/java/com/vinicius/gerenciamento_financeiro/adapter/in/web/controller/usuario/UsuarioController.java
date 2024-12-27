package com.vinicius.gerenciamento_financeiro.adapter.in.web.controller.usuario;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.ApiResponseSistema;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.LoginRequest;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.UsuarioPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.response.AuthenticationResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.response.UsuarioResponse;
import com.vinicius.gerenciamento_financeiro.domain.service.usuario.UsuarioServiceImpl;
import com.vinicius.gerenciamento_financeiro.port.in.LoginUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioServiceImpl usuarioService;
    private final LoginUseCase loginUseCase;

    @PostMapping("/registrar")
    public ResponseEntity<ApiResponseSistema<UsuarioResponse>> registrar(@RequestBody @Valid UsuarioPost request) {
        UsuarioResponse response = usuarioService.save(request);
        return ResponseEntity.ok(ApiResponseSistema.success(response, "Usuário registrado com sucesso"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseSistema<AuthenticationResponse>> login(@RequestBody @Valid LoginRequest request) {
        AuthenticationResponse response = loginUseCase.autenticar(request);
        return ResponseEntity.ok(ApiResponseSistema.success(response, "Login realizado com sucesso"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseSistema<UsuarioResponse>> buscarPorId(@PathVariable Long id) {
        UsuarioResponse response = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(ApiResponseSistema.success(response));
    }
}
