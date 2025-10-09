package com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security;

import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.port.in.UsuarioAutenticadoPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class CategoriaAcessoFilter extends OncePerRequestFilter {

    private final UsuarioAutenticadoPort usuarioAutenticadoPort;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (path.startsWith("/transacoes") && request.getParameter("categoriaId") != null) {
            CategoriaId categoriaId = CategoriaId.of(
                    Long.valueOf(request.getParameter("categoriaId"))
            );

            if (!usuarioAutenticadoPort.ehProprietarioDaCategoria(categoriaId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Acesso negado: Categoria não pertence ao usuário.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}