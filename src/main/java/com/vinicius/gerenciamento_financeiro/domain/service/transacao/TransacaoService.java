package com.vinicius.gerenciamento_financeiro.domain.service.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtService;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.TransacaoMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponse;
import com.vinicius.gerenciamento_financeiro.domain.exception.BusinessRuleViolationException;
import com.vinicius.gerenciamento_financeiro.domain.exception.InsufficientPermissionException;
import com.vinicius.gerenciamento_financeiro.domain.exception.ResourceNotFoundException;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
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

    public TransacaoService(
            CategoriaRepository categoriaRepository,
            UsuarioRepository usuarioRepository,
            JwtService jwtService,
            @Qualifier("transacaoPersistenceAdapter") TransacaoRepository transacaoRepository,
            NotificarTransacaoService notificarTransacaoService,
            TransacaoMapper transacaoMapper) {
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
            Long usuarioIdRaw = jwtService.getByAutenticaoUsuarioId();
            UsuarioId usuarioId = UsuarioId.of(usuarioIdRaw);
            log.debug("Usuário autenticado: {}", usuarioId.getValue());

            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", usuarioId.getValue()));

            CategoriaId categoriaId = CategoriaId.of(transacaoPost.categoriaId());
            if (!categoriaRepository.existsByIdAndUsuarioId(categoriaId, usuarioId)) {
                log.warn("Tentativa de acesso à categoria {} por usuário não autorizado: {}",
                        categoriaId.getValue(), usuarioId.getValue());
                throw new InsufficientPermissionException("categoria", "criar transação");
            }

            Transacao transacao = criarTransacaoDominio(transacaoPost, categoriaId, usuario);

            transacaoRepository.salvarTransacao(transacao);

            notificarTransacaoService.notificarTransacaoAtrasada(transacao);

            log.info("Transação criada com sucesso para usuário: {}", usuarioId.getValue());

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

        Long usuarioIdRaw = jwtService.getByAutenticaoUsuarioId();
        List<Transacao> transacoes = transacaoRepository.buscarTodasTransacoesPorUsuario(usuarioIdRaw);

        log.debug("Encontradas {} transações para o usuário {}", transacoes.size(), usuarioIdRaw);

        return transacoes.stream()
                .map(transacaoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularSaldo() {
        log.debug("Calculando saldo do usuário");

        Long usuarioIdRaw = jwtService.getByAutenticaoUsuarioId();
        List<Transacao> transacoes = transacaoRepository.buscarTodasTransacoesPorUsuario(usuarioIdRaw);

        BigDecimal saldo = transacoes.stream()
                .map(this::calcularValorTransacao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.debug("Saldo calculado: {}", saldo);
        return saldo;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void atualizarTransacaoCategoria(Long transacaoId, Long categoriaId) {
        log.debug("Atualizando categoria da transação {} para categoria {}", transacaoId, categoriaId);

        try {
            Long usuarioIdRaw = jwtService.getByAutenticaoUsuarioId();
            UsuarioId usuarioId = UsuarioId.of(usuarioIdRaw);

            Transacao transacao = transacaoRepository.buscarTransacaoPorIdEUsuario(transacaoId, usuarioIdRaw)
                    .orElseThrow(() -> new ResourceNotFoundException("Transacao", transacaoId));

            CategoriaId novaCategoriaId = CategoriaId.of(categoriaId);
            if (!categoriaRepository.existsByIdAndUsuarioId(novaCategoriaId, usuarioId)) {
                throw new ResourceNotFoundException("Categoria", categoriaId);
            }

            Transacao transacaoAtualizada = transacao.atualizarCategoria(novaCategoriaId);

            transacaoRepository.salvarTransacao(transacaoAtualizada);

            log.info("Categoria da transação {} atualizada para {}", transacaoId, categoriaId);

        } catch (Exception e) {
            log.error("Erro ao atualizar categoria da transação: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransacaoResponse> buscarTransacoesPorCategoriaId(Long id) {
        Long usuarioIdRaw = jwtService.getByAutenticaoUsuarioId();
        UsuarioId usuarioId = UsuarioId.of(usuarioIdRaw);
        CategoriaId categoriaId = CategoriaId.of(usuarioIdRaw);
        if (!categoriaRepository.existsByIdAndUsuarioId(categoriaId, usuarioId)) {
            throw new InsufficientPermissionException("categoria", "listar transações");
        }

        List<Transacao> transacoes = transacaoRepository.buscarTransacoesPorCategoriaIdEUsuario(id, usuarioIdRaw);

        return transacoes.stream()
                .map(transacaoMapper::toResponse)
                .collect(Collectors.toList());
    }


    private Transacao criarTransacaoDominio(TransacaoPost transacaoPost, CategoriaId categoriaId, Usuario usuario) {

        return Transacao.criarNova(
                transacaoPost.descricao(),
                transacaoPost.valor(),
                transacaoPost.tipoMovimentacao(),
                transacaoPost.data(),
                categoriaId,
                usuario.getId()
        );
    }

    private BigDecimal calcularValorTransacao(Transacao transacao) {
        return switch (transacao.getTipo()) {
            case RECEITA -> transacao.getValor();
            case DESPESA -> transacao.getValor().negate();
            case TRANSFERENCIA -> BigDecimal.ZERO; // Lógica específica se necessário
        };
    }
}