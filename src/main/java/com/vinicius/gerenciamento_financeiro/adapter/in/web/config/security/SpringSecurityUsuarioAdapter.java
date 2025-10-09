package com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security;

import com.vinicius.gerenciamento_financeiro.domain.exception.UsuarioNaoAutenticadoException;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.ContextoUsuario;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.port.in.UsuarioAutenticadoPort;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
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

    private final JwtTokenService jwtService;
    private final CategoriaRepository categoriaRepository;

    @Override
    public UsuarioId obterUsuarioAtual() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                throw new UsuarioNaoAutenticadoException("Nenhuma autenticação encontrada");
            }

            // Opção 1: Usuário já carregado no SecurityContext
            if (authentication.getPrincipal() instanceof SpringUserDetails userDetails) {
                UsuarioId usuarioId = userDetails.getUsuario().getId();
                log.debug("Usuário obtido via SpringUserDetails: {}", usuarioId.getValue());
                return usuarioId;
            }

            // Opção 2: Usuário anônimo
            if ("anonymousUser".equals(authentication.getPrincipal())) {
                throw new UsuarioNaoAutenticadoException("Usuário anônimo");
            }

            // ✅ Opção 3: Fallback - extrair do token na requisição atual
            String token = extrairTokenDaRequisicao();
            if (token != null) {
                Long usuarioIdRaw = jwtService.extrairUsuarioId(token);
                if (usuarioIdRaw != null && usuarioIdRaw > 0) {
                    log.debug("Usuário obtido via token: {}", usuarioIdRaw);
                    return UsuarioId.of(usuarioIdRaw);
                }
            }

            throw new UsuarioNaoAutenticadoException("ID de usuário não encontrado");

        } catch (UsuarioNaoAutenticadoException e) {
            log.warn("Falha na obtenção do usuário: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao obter usuário autenticado", e);
            throw new UsuarioNaoAutenticadoException("Erro interno de autenticação");
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
            log.debug("Erro ao extrair token da requisição", e);
            return null;
        }
    }

    @Override
    public boolean temPermissao(String recurso, String acao) {
        try {
            if (recurso == null || recurso.isBlank() || acao == null || acao.isBlank()) {
                log.warn("Tentativa de verificar permissão com parâmetros inválidos: recurso={}, acao={}",
                        recurso, acao);
                return false;
            }

            UsuarioId usuarioAtual = obterUsuarioAtual();

            log.debug("Verificando permissão: usuário={}, recurso={}, acao={}",
                    usuarioAtual.getValue(), recurso, acao);

            boolean temPermissao = verificarPermissaoEspecifica(recurso.toLowerCase(), acao.toLowerCase());

            if (!temPermissao) {
                log.warn("Permissão negada: usuário={}, recurso={}, acao={}",
                        usuarioAtual.getValue(), recurso, acao);
            }

            return temPermissao;

        } catch (UsuarioNaoAutenticadoException e) {
            log.warn("Tentativa de verificar permissão sem usuário autenticado: recurso={}, acao={}",
                    recurso, acao);
            return false;
        } catch (Exception e) {
            log.error("Erro ao verificar permissão: recurso={}, acao={}", recurso, acao, e);
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
                log.warn("Tentativa de acesso a recurso de outro usuário: usuarioAtual={}, proprietarioRecurso={}",
                        usuarioAtual.getValue(), proprietarioId.getValue());
            } else {
                log.debug("Acesso autorizado ao próprio recurso: usuário={}", usuarioAtual.getValue());
            }

            return ehProprietario;

        } catch (UsuarioNaoAutenticadoException e) {
            log.warn("Tentativa de verificar propriedade sem usuário autenticado: proprietarioId={}",
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

            log.debug("Contexto de usuário criado: usuarioId={}, email={}, ip={}",
                    usuarioId.getValue(), email, ipOrigem);

            return contexto;

        } catch (UsuarioNaoAutenticadoException e) {
            log.debug("Contexto solicitado sem usuário autenticado");
            return ContextoUsuario.criar(null, "anonymous", obterIpOrigem());
        } catch (Exception e) {
            log.warn("Erro ao criar contexto de usuário para logs", e);
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
            log.debug("Erro ao obter email do usuário", e);
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

    @Override
    public boolean ehProprietarioDaCategoria(CategoriaId categoriaId) {
        try {
            if (categoriaId == null) {
                log.warn("Tentativa de verificar propriedade de categoria com ID nulo");
                return false;
            }

            UsuarioId usuarioAtual = obterUsuarioAtual();

            boolean ehProprietario = categoriaRepository
                    .existsByIdAndUsuarioId(categoriaId, usuarioAtual);

            if (!ehProprietario) {
                log.warn("Tentativa de acesso a categoria de outro usuário: usuarioId={}, categoriaId={}",
                        usuarioAtual.getValue(), categoriaId.getValue());
            }

            return ehProprietario;

        } catch (UsuarioNaoAutenticadoException e) {
            log.warn("Tentativa de verificar propriedade de categoria sem autenticação: categoriaId={}",
                    categoriaId.getValue());
            return false;
        } catch (Exception e) {
            log.error("Erro ao verificar propriedade da categoria: categoriaId={}",
                    categoriaId.getValue(), e);
            return false;
        }
    }
}