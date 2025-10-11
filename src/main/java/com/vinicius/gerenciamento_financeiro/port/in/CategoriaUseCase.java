package com.vinicius.gerenciamento_financeiro.port.in;

import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Port de entrada para operações de Categoria.
 * Define contratos usando APENAS tipos de domínio.
 */
public interface CategoriaUseCase {
    
    /**
     * Cria uma nova categoria
     */
    Categoria criar(Categoria categoria);
    
    /**
     * Busca categoria por ID
     */
    Categoria buscarPorId(CategoriaId id);
    
    /**
     * Lista todas as categorias de um usuário
     */
    List<Categoria> listarPorUsuario(UsuarioId usuarioId);
    
    /**
     * Busca paginada de categorias de um usuário
     */
    Page<Categoria> listarPaginado(UsuarioId usuarioId, Pageable pageable);
    
    /**
     * Remove uma categoria
     */
    void remover(CategoriaId id);
}
