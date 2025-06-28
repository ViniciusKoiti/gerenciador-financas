package com.vinicius.gerenciamento_financeiro.domain.service.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtService;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.TransacaoMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponse;
import com.vinicius.gerenciamento_financeiro.domain.exception.BusinessRuleViolationException;
import com.vinicius.gerenciamento_financeiro.domain.exception.InsufficientPermissionException;
import com.vinicius.gerenciamento_financeiro.domain.exception.ResourceNotFoundException;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.auditoria.AuditoriaJpa;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.categoria.entity.CategoriaJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoMovimentacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.vinicius.gerenciamento_financeiro.port.in.GerenciarTransacaoUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.transacao.TransacaoRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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

            CategoriaJpaEntity categoriaJpaEntity = categoriaRepository.findById(transacaoPost.categoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("CategoriaJpaEntity", transacaoPost.categoriaId()));
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", usuarioId));
            if (!categoriaJpaEntity.getUsuario().getId().equals(usuarioId)) {
                log.warn("Tentativa de acesso à categoriaJpaEntity {} por usuário não autorizado: {}",
                        categoriaJpaEntity.getId(), usuarioId);
                throw new InsufficientPermissionException("categoriaJpaEntity", "criar transação");
            }
            AuditoriaJpa auditoria = new AuditoriaJpa();
            Transacao transacao = transacaoMapper.toEntity(transacaoPost, categoriaJpaEntity, usuario, auditoria);
            transacaoRepository.salvarTransacao(transacao);
            notificarTransacaoService.notificarTransacaoAtrasada(transacao);
        } catch (ResourceNotFoundException | InsufficientPermissionException | BusinessRuleViolationException e) {
            log.error("Erro de domínio ao adicionar transação: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao adicionar transação", e);
            throw new BusinessRuleViolationException("TRANSACTION_CREATION_ERROR",
                    "Erro interno ao processar transação: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransacaoResponse> obterTodasTransacoes() {
        log.debug("Buscando todas as transações do usuário");

        Long usuarioId = jwtService.getByAutenticaoUsuarioId();

        List<Transacao> transacoes = transacaoRepository.buscarTodasTransacoesPorUsuario(usuarioId);
        log.debug("Encontradas {} transações para o usuário {}", transacoes.size(), usuarioId);

        return transacoes.stream()
                .map(transacaoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularSaldo() {
        log.debug("Calculando saldo do usuário");

        Long usuarioId = jwtService.getByAutenticaoUsuarioId();
        List<Transacao> transacoes = transacaoRepository.buscarTodasTransacoesPorUsuario(usuarioId);

        BigDecimal saldo = transacoes.stream()
                .map(transacao -> {
                    if (transacao.getTipo() == TipoMovimentacao.RECEITA) {
                        return transacao.getValor();
                    } else if (transacao.getTipo() == TipoMovimentacao.DESPESA) {
                        return transacao.getValor().negate();
                    }
                    return BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.debug("Saldo calculado: {}", saldo);
        return saldo;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void atualizarTransacaoCategoria(Long transacaoId, Long categoriaId) {
        log.debug("Atualizando categoriaJpaEntity da transação {} para categoriaJpaEntity {}", transacaoId, categoriaId);

        Long usuarioId = jwtService.getByAutenticaoUsuarioId();

        Transacao transacao = transacaoRepository.buscarTransacaoPorIdEUsuario(transacaoId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Transacao", transacaoId));

        CategoriaJpaEntity categoriaJpaEntity = categoriaRepository.findByIdAndUsuarioId(categoriaId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("CategoriaJpaEntity", categoriaId));

        // TODO CRIAR VALIDAÇÃO PARA REGRAS
        Transacao transacaoAtualizada = transacao.atualizarCategoria(categoriaJpaEntity, transacao.getAuditoria());
        transacaoRepository.salvarTransacao(transacaoAtualizada);

        log.info("CategoriaJpaEntity da transação {} atualizada para {}", transacaoId, categoriaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransacaoResponse> buscarTransacoesPorCategoriaId(Long categoriaId) {
        Long usuarioId = jwtService.getByAutenticaoUsuarioId();

        if (!categoriaRepository.existsByIdAndUsuarioId(categoriaId, usuarioId)) {
            throw new SecurityException("CategoriaJpaEntity não pertence ao usuário logado");
        }

        List<Transacao> transacoes = transacaoRepository.buscarTransacoesPorCategoriaIdEUsuario(categoriaId, usuarioId);
        return transacoes.stream()
                .map(transacaoMapper::toResponse)
                .collect(Collectors.toList());
    }

}
