package com.vinicius.gerenciamento_financeiro.adapter.out.criptografia;

import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.port.out.autorizacao.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService implements TokenService {

    private final JwtParseService jwtParseService;

    @Override
    public String extrairUsername(String token) {
        return jwtParseService.extrairSubject(token);
    }

    @Override
    public Long extrairUsuarioId(String token) {
        return jwtParseService.extrairClaim(token);
    }

    @Override
    public boolean validarToken(String token, String username) {
        try {
            String tokenUsername = extrairUsername(token);
            Date expiracao = jwtParseService.extrairExpiracao(token);
            boolean expirado = expiracao.before(new Date());

            return tokenUsername.equals(username) && !expirado;
        } catch (Exception e) {
            log.warn("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String gerarToken(Usuario usuario) {
        Map<String, Object> claims = Map.of("userId", usuario.getId().getValue());
        Date expiracao = new Date(System.currentTimeMillis() + jwtParseService.getTempoExpiracao());

        return jwtParseService.gerarToken(
                usuario.getEmail().getEndereco(),
                claims,
                expiracao
        );
    }
}