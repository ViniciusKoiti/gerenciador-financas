package com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security;

import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.NonNullApi;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CategoriaAcessoFilter extends OncePerRequestFilter {

    private final CategoriaRepository categoriaRepository;

    private final JwtService jwtService;

    public CategoriaAcessoFilter(CategoriaRepository categoriaRepository, JwtService jwtService) {
        this.categoriaRepository = categoriaRepository;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/transacoes") && request.getParameter("categoriaId") != null) {
            UsuarioId usuarioId = UsuarioId.of(jwtService.getByAutenticaoUsuarioId());
            CategoriaId categoriaId = CategoriaId.of(Long.valueOf(request.getParameter("categoriaId")));
            boolean pertence = categoriaRepository.existsByIdAndUsuarioId(categoriaId, usuarioId);
            if (!pertence) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado: CategoriaJpaEntity não pertence ao usuário.");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
