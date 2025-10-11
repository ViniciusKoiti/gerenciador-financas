package com.vinicius.gerenciamento_financeiro.application.service.categoria;

import com.vinicius.gerenciamento_financeiro.domain.exception.BusinessRuleViolationException;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.port.in.CategoriaUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço de domínio que implementa as regras de negócio de Categoria.
 * Trabalha APENAS com entidades de domínio - sem DTOs.
 * Não utiliza anotações Spring para manter pureza do domínio.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CategoriaApplicationService implements CategoriaUseCase {

    private final CategoriaRepository categoriaRepository;

    @Override
    public Categoria criar(Categoria categoria) {
        log.debug("Criando categoria no domínio: {}", categoria.getNome());
        
        // Regras de negócio
        validarCategoriaUnica(categoria);
        
        return categoriaRepository.save(categoria);
    }

    @Override
    public Categoria buscarPorId(CategoriaId id) {
        log.debug("Buscando categoria por ID: {}", id.getValue());
        
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleViolationException(
                    "CATEGORIA_NAO_ENCONTRADA",
                    "Categoria não encontrada: " + id.getValue()
                ));
    }

    @Override
    public List<Categoria> listarPorUsuario(UsuarioId usuarioId) {
        log.debug("Listando categorias do usuário: {}", usuarioId.getValue());
        return categoriaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Page<Categoria> listarPaginado(UsuarioId usuarioId, Pageable pageable) {
        log.debug("Listando categorias paginadas do usuário: {}", usuarioId.getValue());
        return categoriaRepository.findByUsuarioId(usuarioId, pageable);
    }

    @Override
    public void remover(CategoriaId id) {
        log.debug("Removendo categoria: {}", id.getValue());
        
        // Validar se existe
        buscarPorId(id);
        
        // Regras de negócio antes de remover
        validarRemocao(id);
        
        categoriaRepository.deleteById(id.getValue());
    }

    // Regras de negócio privadas
    private void validarCategoriaUnica(Categoria categoria) {
        // Implementar validação se necessário
    }

    private void validarRemocao(CategoriaId id) {
        // Validar se categoria tem transações vinculadas, etc.
    }
}