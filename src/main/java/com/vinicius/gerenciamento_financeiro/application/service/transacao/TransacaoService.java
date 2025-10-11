package com.vinicius.gerenciamento_financeiro.application.service.transacao;

import com.vinicius.gerenciamento_financeiro.domain.exception.InsufficientPermissionException;
import com.vinicius.gerenciamento_financeiro.domain.exception.ResourceNotFoundException;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MontanteMonetario;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.TransacaoId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.port.in.GerenciarTransacaoUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import com.vinicius.gerenciamento_financeiro.port.out.transacao.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TransacaoService implements GerenciarTransacaoUseCase {

    private final CategoriaRepository categoriaRepository;
    private final TransacaoRepository transacaoRepository;

    @Override
    public Transacao adicionarTransacao(Transacao transacao) {
        log.debug("Adicionando transação: {}", transacao.getDescricao());
        
        // Validar se a categoria pertence ao usuário
        if (!categoriaRepository.existsByIdAndUsuarioId(transacao.getCategoriaId(), transacao.getUsuarioId())) {
            log.warn("Tentativa de acesso à categoria {} por usuário não autorizado: {}",
                    transacao.getCategoriaId().getValue(), transacao.getUsuarioId().getValue());
            throw new InsufficientPermissionException("categoria", "criar transação");
        }
        
        transacaoRepository.salvarTransacao(transacao);
        return transacao; // Repository void method doesn't return the saved entity
    }

    @Override
    public List<Transacao> obterTodasTransacoes(UsuarioId usuarioId) {
        log.debug("Obtendo todas as transações do usuário: {}", usuarioId.getValue());
        return transacaoRepository.buscarTodasTransacoesPorUsuario(usuarioId.getValue());
    }

    @Override
    public MontanteMonetario calcularSaldo(UsuarioId usuarioId) {
        log.debug("Calculando saldo do usuário: {}", usuarioId.getValue());
        
        List<Transacao> transacoes = transacaoRepository.buscarTodasTransacoesPorUsuario(usuarioId.getValue());
        
        // Calcular saldo assumindo BRL como moeda padrão
        // TODO: Implementar cálculo multi-moeda no futuro
        var moedaPadrao = com.vinicius.gerenciamento_financeiro.domain.model.moeda.MoedaId.of("BRL");
        var saldoTotal = MontanteMonetario.zero(moedaPadrao);
        
        for (Transacao transacao : transacoes) {
            switch (transacao.getTipo()) {
                case RECEITA -> saldoTotal = saldoTotal.add(transacao.getMontante());
                case DESPESA -> saldoTotal = saldoTotal.subtract(transacao.getMontante());
                // TRANSFERENCIA não altera o saldo total
            }
        }
        
        return saldoTotal;
    }

    @Override
    public void atualizarTransacaoCategoria(CategoriaId categoriaId, TransacaoId transacaoId) {
        log.debug("Atualizando categoria da transação {} para categoria {}", 
                transacaoId.getValue(), categoriaId.getValue());
        
        // Buscar transação usando método legado
        Transacao transacao = transacaoRepository.buscarTransacaoPorId(transacaoId.getValue())
                .orElseThrow(() -> new ResourceNotFoundException("Transação", transacaoId.getValue()));
        
        // Validar se a categoria pertence ao usuário
        if (!categoriaRepository.existsByIdAndUsuarioId(categoriaId, transacao.getUsuarioId())) {
            throw new InsufficientPermissionException("categoria", "atualizar transação");
        }
        
        Transacao transacaoAtualizada = transacao.atualizarCategoria(categoriaId);
        transacaoRepository.salvarTransacao(transacaoAtualizada);
    }

    @Override
    public List<Transacao> buscarTransacoesPorCategoriaId(CategoriaId categoriaId) {
        log.debug("Buscando transações da categoria: {}", categoriaId.getValue());
        return transacaoRepository.buscarTransacoesPorCategoriaId(categoriaId.getValue());
    }
}