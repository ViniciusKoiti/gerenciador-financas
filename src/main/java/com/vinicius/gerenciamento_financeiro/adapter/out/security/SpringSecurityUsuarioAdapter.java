package com.vinicius.gerenciamento_financeiro.adapter.out.security;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.SpringUserDetails;
import com.vinicius.gerenciamento_financeiro.port.out.autorizacao.TokenService;
import com.vinicius.gerenciamento_financeiro.domain.exception.UsuarioNaoAutenticadoException;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.ContextoUsuario;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.port.in.UsuarioAutenticadoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringSecurityUsuarioAdapter implements UsuarioAutenticadoPort {

    private final TokenService tokenService;

    @Override
    public UsuarioId obterUsuarioAtual() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                throw new UsuarioNaoAutenticadoException("Nenhuma autenticaÃ§Ã£o encontrada");
            }

            // OpÃ§Ã£o 1: UsuÃ¡rio jÃ¡ carregado no SecurityContext
            if (authentication.getPrincipal() instanceof SpringUserDetails userDetails) {
                UsuarioId usuarioId = userDetails.getUsuario().getId();
                log.debug("UsuÃ¡rio obtido via SpringUserDetails: {}", usuarioId.getValue());
                return usuarioId;
            }

            // OpÃ§Ã£o 2: UsuÃ¡rio anÃ´nimo
            if ("anonymousUser".equals(authentication.getPrincipal())) {
                throw new UsuarioNaoAutenticadoException("UsuÃ¡rio anÃ´nimo");
            }

            // âœ… OpÃ§Ã£o 3: Fallback - extrair do token na requisiÃ§Ã£o atual
            String token = extrairTokenDaRequisicao();
            if (token != null) {
                Long usuarioIdRaw = tokenService.extrairUsuarioId(token);
                if (usuarioIdRaw != null && usuarioIdRaw > 0) {
                    log.debug("UsuÃ¡rio obtido via token: {}", usuarioIdRaw);
                    return UsuarioId.of(usuarioIdRaw);
                }
            }

            throw new UsuarioNaoAutenticadoException("ID de usuÃ¡rio nÃ£o encontrado");

        } catch (UsuarioNaoAutenticadoException e) {
            log.warn("Falha na obtenÃ§Ã£o do usuÃ¡rio: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao obter usuÃ¡rio autenticado", e);
            throw new UsuarioNaoAutenticadoException("Erro interno de autenticaÃ§Ã£o");
        }
    }

    private String extrairTokenDaRequisicao() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authHeader = request.getHeader("Authorization");

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    return authHeader.substring(7);
                }
            }

            return null;
        } catch (Exception e) {
            log.debug("Erro ao extrair token da requisiÃ§Ã£o", e);
            return null;
        }
    }

    @Override
    public boolean temPermissao(String recurso, String acao) {
        try {
            if (recurso == null || recurso.isBlank() || acao == null || acao.isBlank()) {
                log.warn("Tentativa de verificar permissÃ£o com parÃ¢metros invÃ¡lidos: recurso={}, acao={}",
                        recurso, acao);
                return false;
            }

            UsuarioId usuarioAtual = obterUsuarioAtual();

            log.debug("Verificando permissÃ£o: usuÃ¡rio={}, recurso={}, acao={}",
                    usuarioAtual.getValue(), recurso, acao);

            boolean temPermissao = verificarPermissaoEspecifica(recurso.toLowerCase(), acao.toLowerCase());

            if (!temPermissao) {
                log.warn("PermissÃ£o negada: usuÃ¡rio={}, recurso={}, acao={}",
                        usuarioAtual.getValue(), recurso, acao);
            }

            return temPermissao;

        } catch (UsuarioNaoAutenticadoException e) {
            log.warn("Tentativa de verificar permissÃ£o sem usuÃ¡rio autenticado: recurso={}, acao={}",
                    recurso, acao);
            return false;
        } catch (Exception e) {
            log.error("Erro ao verificar permissÃ£o: recurso={}, acao={}", recurso, acao, e);
            return false;
        }
    }

    @Override
    public boolean ehProprietario(UsuarioId proprietarioId) {
        try {
            if (proprietarioId == null) {
                log.warn("Tentativa de verificar propriedade com proprietarioId nulo");
                return false;
            }

            UsuarioId usuarioAtual = obterUsuarioAtual();

            boolean ehProprietario = usuarioAtual.equals(proprietarioId);

            if (!ehProprietario) {
                log.warn("Tentativa de acesso a recurso de outro usuÃ¡rio: usuarioAtual={}, proprietarioRecurso={}",
                        usuarioAtual.getValue(), proprietarioId.getValue());
            } else {
                log.debug("Acesso autorizado ao prÃ³prio recurso: usuÃ¡rio={}", usuarioAtual.getValue());
            }

            return ehProprietario;

        } catch (UsuarioNaoAutenticadoException e) {
            log.warn("Tentativa de verificar propriedade sem usuÃ¡rio autenticado: proprietarioId={}",
                    proprietarioId != null ? proprietarioId.getValue() : "null");
            return false;
        } catch (Exception e) {
            log.error("Erro ao verificar propriedade: proprietarioId={}",
                    proprietarioId != null ? proprietarioId.getValue() : "null", e);
            return false;
        }
    }

    @Override
    public ContextoUsuario obterContextoParaLogs() {
        try {
            UsuarioId usuarioId = obterUsuarioAtual();
            String email = obterEmailUsuarioAtual();
            String ipOrigem = obterIpOrigem();

            ContextoUsuario contexto = ContextoUsuario.criar(usuarioId, email, ipOrigem);

            log.debug("Contexto de usuÃ¡rio criado: usuarioId={}, email={}, ip={}",
                    usuarioId.getValue(), email, ipOrigem);

            return contexto;

        } catch (UsuarioNaoAutenticadoException e) {
            log.debug("Contexto solicitado sem usuÃ¡rio autenticado");
            return ContextoUsuario.criar(null, "anonymous", obterIpOrigem());
        } catch (Exception e) {
            log.warn("Erro ao criar contexto de usuÃ¡rio para logs", e);
            return ContextoUsuario.criar(null, "unknown", "unknown");
        }
    }
    private boolean verificarPermissaoEspecifica(String recurso, String acao) {
        return switch (recurso) {
            case "categoria" -> {
                yield switch (acao) {
                    case "criar", "editar", "deletar", "visualizar" -> true;
                    default -> false;
                };
            }
            case "transacao" -> {
                yield switch (acao) {
                    case "criar", "editar", "deletar", "visualizar", "pagar" -> true;
                    default -> false;
                };
            }
            case "usuario" -> {
                yield switch (acao) {
                    case "visualizar", "editar" -> true;
                    case "deletar", "criar" -> false;
                    default -> false;
                };
            }
            case "relatorio" -> {
                yield "visualizar".equals(acao);
            }
            default -> {
                log.warn("Recurso desconhecido solicitado: {}", recurso);
                yield false;
            }
        };
    }


    private String obterEmailUsuarioAtual() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.getPrincipal() instanceof SpringUserDetails userDetails) {
                return userDetails.getUsuario().getEmail().getEndereco();
            }

            if (auth != null && auth.getName() != null) {
                return auth.getName();
            }

            return "unknown";
        } catch (Exception e) {
            log.debug("Erro ao obter email do usuÃ¡rio", e);
            return "unknown";
        }
    }
    private String obterIpOrigem() {
        try {
            ServletRequestAttributes requestAttributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (requestAttributes != null) {
                HttpServletRequest request = requestAttributes.getRequest();

                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }

                String xRealIp = request.getHeader("X-Real-IP");
                if (xRealIp != null && !xRealIp.isEmpty()) {
                    return xRealIp;
                }

                return request.getRemoteAddr();
            }

            return "unknown";
        } catch (Exception e) {
            log.debug("Erro ao obter IP de origem", e);
            return "unknown";
        }
    }
}

