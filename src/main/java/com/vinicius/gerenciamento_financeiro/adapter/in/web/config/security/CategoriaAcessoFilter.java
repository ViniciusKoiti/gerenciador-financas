package com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security;

import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.port.in.VerificarPropriedadeRecursoUseCase;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class CategoriaAcessoFilter extends OncePerRequestFilter {

    private final VerificarPropriedadeRecursoUseCase useCase;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        if (HttpMethod.OPTIONS.matches(request.getMethod()) ||
                HttpMethod.GET.matches(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        final String uri = normalize(request.getRequestURI());

        if (matchesTransacaoMutavel(uri, request.getMethod())) {
            Long categoriaIdRaw = extrairCategoriaId(request);
            if (categoriaIdRaw == null || categoriaIdRaw <= 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "categoriaId inválido ou ausente");
                return;
            }

            boolean permitido = useCase.verificarPropriedadeCategoria(CategoriaId.of(categoriaIdRaw));
            if (!permitido) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Acesso negado: categoria não pertence ao usuário");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean matchesTransacaoMutavel(String uri, String method) {
        boolean mutavel = HttpMethod.POST.matches(method)
                || HttpMethod.PUT.matches(method)
                || HttpMethod.PATCH.matches(method)
                || HttpMethod.DELETE.matches(method);

        if (!mutavel) return false;

        return pathMatcher.match("/transacoes/**", uri);
    }

    private Long extrairCategoriaId(HttpServletRequest request) {
        String qp = request.getParameter("categoriaId");
        if (qp != null && !qp.isBlank()) {
            try { return Long.valueOf(qp.trim()); } catch (NumberFormatException ignored) {}
        }

        String hdr = request.getHeader("X-Categoria-Id");
        if (hdr != null && !hdr.isBlank()) {
            try { return Long.valueOf(hdr.trim()); } catch (NumberFormatException ignored) {}
        }

        return null;
    }

    private String normalize(String uri) {
        return uri.replaceAll("/{2,}", "/");
    }
}
