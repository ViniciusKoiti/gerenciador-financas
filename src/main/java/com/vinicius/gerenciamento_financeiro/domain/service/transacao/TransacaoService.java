package com.vinicius.gerenciamento_financeiro.domain.service.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtService;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.TransacaoMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoMovimentacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.vinicius.gerenciamento_financeiro.port.in.GerenciarTransacaoUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.transacao.TransacaoRepository;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
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
    public void adicionarTransacao(TransacaoPost transacaoPost) {
        Long usuarioId = jwtService.getByAutenticaoUsuarioId();
        Categoria categoria = categoriaRepository.findById(transacaoPost.categoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        Auditoria auditoria = new Auditoria();
        Transacao transacao = transacaoMapper.toEntity(transacaoPost, categoria,usuario, auditoria);
        transacaoRepository.salvarTransacao(transacao);
        notificarTransacaoService.notificarTransacaoAtrasada(transacao);
    }

    @Override
    public List<TransacaoResponse> obterTodasTransacoes() {
        return transacaoRepository.buscarTodasTransacoes().stream().map(transacaoMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public BigDecimal calcularSaldo() {
        List<Transacao> transacoes = transacaoRepository.buscarTodasTransacoes();
        BigDecimal saldo = BigDecimal.ZERO;
        for (Transacao transacao : transacoes) {
            if (transacao.getTipo() == TipoMovimentacao.RECEITA) {
                saldo = saldo.add(transacao.getValor());
            } else if (transacao.getTipo() == TipoMovimentacao.DESPESA) {
                saldo = saldo.subtract(transacao.getValor());
            }
        }
        return saldo;
    }

    @Override
    public void atualizarTransacaoCategoria(Long transacaoId, Long categoriaId) {
        Transacao transacao = transacaoRepository.buscarTransacaoPorId(transacaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Transação não encontrada."));
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não  encontrada."));
        Transacao transacaoAtualizada = transacao.atualizarCategoria(categoria, transacao.getAuditoria());
        transacaoRepository.salvarTransacao(transacaoAtualizada);
    }

}
