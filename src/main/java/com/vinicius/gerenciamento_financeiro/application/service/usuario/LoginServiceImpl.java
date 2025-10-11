package com.vinicius.gerenciamento_financeiro.application.service.usuario;

import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Credencial;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.SessaoUsuario;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.port.in.LoginUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.autorizacao.TokenService;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginUseCase {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    @Override
    public SessaoUsuario autenticar(Credencial credencial) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credencial.getEmail().getEndereco(),
                        credencial.getSenha()
                )
        );

        if (!authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("Falha ao autenticar usuario");
        }

        Usuario usuario = usuarioRepository.findByEmail(credencial.getEmail().getEndereco())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado para autenticacao"));

        String token = tokenService.gerarToken(usuario);
        LocalDateTime dataExpiracao = LocalDateTime.now().plusHours(24);

        return SessaoUsuario.criar(usuario, token, dataExpiracao);
    }
}
