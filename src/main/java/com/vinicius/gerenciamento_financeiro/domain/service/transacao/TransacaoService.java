package com.vinicius.gerenciamento_financeiro.domain.service.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtService;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.TransacaoMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponse;
import com.vinicius.gerenciamento_financeiro.domain.exception.InsufficientPermissionException;
import com.vinicius.gerenciamento_financeiro.domain.exception.ResourceNotFoundException;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoMovimentacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.vinicius.gerenciamento_financeiro.port.in.GerenciarTransacaoUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.transacao.TransacaoRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class TransacaoService implements GerenciarTransacaoUseCase {
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final TransacaoRepository transacaoRepository;
    private final NotificarTransacaoService notificarTransacaoService;
    private final TransacaoMapper transacaoMapper;

    public TransacaoService(CategoriaRepository categoriaRepository, UsuarioRepository usuarioRepository, JwtService jwtService, @Qualifier("transacaoPersistenceAdapter") TransacaoRepository transacaoRepository,
                            NotificarTransacaoService notificarTransacaoService, TransacaoMapper transacaoMapper) {
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.transacaoRepository = transacaoRepository;
        this.notificarTransacaoService = notificarTransacaoService;
        this.transacaoMapper = transacaoMapper;
    }
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void adicionarTransacao(TransacaoPost transacaoPost) {
        try {
            Long usuarioId = jwtService.getByAutenticaoUsuarioId();
            log.debug("Usuário autenticado: {}", usuarioId);

            Categoria categoria = categoriaRepository.findById(transacaoPost.categoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria", transacaoPost.categoriaId()));
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", usuarioId));
            if (!categoria.getUsuario().getId().equals(usuarioId)) {
                log.warn("Tentativa de acesso à categoria {} por usuário não autorizado: {}",
                        categoria.getId(), usuarioId);
                throw new InsufficientPermissionException("categoria", "criar transação");
            }
            Auditoria auditoria = new Auditoria();
            Transacao transacao = transacaoMapper.toEntity(transacaoPost, categoria, usuario, auditoria);
            transacaoRepository.salvarTransacao(transacao);
            notificarTransacaoService.notificarTransacaoAtrasada(transacao);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao processar transação: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransacaoResponse> obterTodasTransacoes() {
        Long usuarioId = jwtService.getByAutenticaoUsuarioId();
        return transacaoRepository.buscarTodasTransacoesPorUsuario(usuarioId)
                .stream()
                .map(transacaoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal calcularSaldo() {
        Long usuarioId = jwtService.getByAutenticaoUsuarioId();
        List<Transacao> transacoes = transacaoRepository.buscarTodasTransacoesPorUsuario(usuarioId);

        return transacoes.stream()
                .map(transacao -> {
                    if (transacao.getTipo() == TipoMovimentacao.RECEITA) {
                        return transacao.getValor();
                    } else if (transacao.getTipo() == TipoMovimentacao.DESPESA) {
                        return transacao.getValor().negate();
                    }
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void atualizarTransacaoCategoria(Long transacaoId, Long categoriaId) {
        Long usuarioId = jwtService.getByAutenticaoUsuarioId();

        Transacao transacao = transacaoRepository.buscarTransacaoPorIdEUsuario(transacaoId, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Transação não encontrada ou não pertence ao usuário."));

        Categoria categoria = categoriaRepository.findByIdAndUsuarioId(categoriaId, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Categoria não encontrada ou não pertence ao usuário."));

        Transacao transacaoAtualizada = transacao.atualizarCategoria(categoria, transacao.getAuditoria());
        transacaoRepository.salvarTransacao(transacaoAtualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransacaoResponse> buscarTransacoesPorCategoriaId(Long categoriaId) {
        Long usuarioId = jwtService.getByAutenticaoUsuarioId();

        if (!categoriaRepository.existsByIdAndUsuarioId(categoriaId, usuarioId)) {
            throw new SecurityException("Categoria não pertence ao usuário logado");
        }

        List<Transacao> transacoes = transacaoRepository.buscarTransacoesPorCategoriaIdEUsuario(categoriaId, usuarioId);
        return transacoes.stream()
                .map(transacaoMapper::toResponse)
                .collect(Collectors.toList());
    }

}
