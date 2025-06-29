package com.vinicius.gerenciamento_financeiro.adapter.out.memory;

import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.TransacaoId;
import com.vinicius.gerenciamento_financeiro.port.out.transacao.TransacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@Profile("dev")
public class MemoryTransacaoRepository implements TransacaoRepository {

    private static final Logger log = LoggerFactory.getLogger(MemoryTransacaoRepository.class);
    private final Map<Long, Transacao> transacoes = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public void salvarTransacao(Transacao transacao) {
        Transacao transacaoParaSalvar;

        if (transacao.isNova()) {
            Long novoId = idGenerator.getAndIncrement();

            transacaoParaSalvar = Transacao.reconstituir(
                    novoId,                              // ✅ ID gerado
                    transacao.getDescricao(),            // Dados existentes
                    transacao.getValor(),
                    transacao.getTipo(),
                    transacao.getData(),
                    transacao.getUsuarioId(),
                    transacao.getCategoriaId(),
                    transacao.getConfiguracao(),
                    transacao.getAuditoria()
            );

            log.debug("Nova transação criada com ID: {}", novoId);
        } else {
            transacaoParaSalvar = transacao;
            log.debug("Transação existente atualizada: ID {}", transacao.getId().getValue());
        }

        transacoes.put(transacaoParaSalvar.getId().getValue(), transacaoParaSalvar);
        log.debug("Transação salva em memória: ID {}", transacaoParaSalvar.getId().getValue());
    }

    @Override
    public List<Transacao> buscarTodasTransacoesPorUsuario(Long usuarioId) {
        log.debug("Buscando todas as transações para usuário: {} (total em memória: {})", usuarioId, transacoes.size());

        return transacoes.values().stream()
                .filter(transacao -> Objects.equals(transacao.getUsuarioId().getValue(), usuarioId))
                .sorted((t1, t2) -> t2.getData().compareTo(t1.getData()))
                .collect(Collectors.toList());
    }

    @Override
    public Page<Transacao> buscarTransacoesPorUsuarioPaginado(Long usuarioId, Pageable pageable) {
        log.debug("Buscando transações paginadas para usuário: {}, página: {}", usuarioId, pageable.getPageNumber());

        List<Transacao> todasTransacoes = buscarTodasTransacoesPorUsuario(usuarioId);
        return paginarLista(todasTransacoes, pageable);
    }

    @Override
    public Optional<Transacao> buscarTransacaoPorIdEUsuario(Long transacaoId, Long usuarioId) {
        log.debug("Buscando transação {} para usuário: {}", transacaoId, usuarioId);

        return Optional.ofNullable(transacoes.get(transacaoId))
                .filter(transacao -> Objects.equals(transacao.getUsuarioId().getValue(), usuarioId));
    }

    @Override
    public List<Transacao> buscarTransacoesPorCategoriaIdEUsuario(Long categoriaId, Long usuarioId) {
        log.debug("Buscando transações da categoria {} para usuário: {}", categoriaId, usuarioId);

        return transacoes.values().stream()
                .filter(transacao -> Objects.equals(transacao.getUsuarioId().getValue(), usuarioId))
                .filter(transacao -> Objects.equals(transacao.getCategoriaId().getValue(), categoriaId))
                .sorted((t1, t2) -> t2.getData().compareTo(t1.getData()))
                .collect(Collectors.toList());
    }

    @Override
    public Page<Transacao> buscarTransacoesPorCategoriaIdEUsuarioPaginado(Long categoriaId, Long usuarioId, Pageable pageable) {
        log.debug("Buscando transações paginadas da categoria {} para usuário: {}", categoriaId, usuarioId);

        List<Transacao> todasTransacoes = buscarTransacoesPorCategoriaIdEUsuario(categoriaId, usuarioId);
        return paginarLista(todasTransacoes, pageable);
    }

    @Override
    @Deprecated
    public List<Transacao> buscarTodasTransacoes() {
        log.warn("Método deprecated sendo usado: buscarTodasTransacoes() - Total: {}", transacoes.size());
        return new ArrayList<>(transacoes.values());
    }

    @Override
    @Deprecated
    public Optional<Transacao> buscarTransacaoPorId(Long id) {
        log.warn("Método deprecated sendo usado: buscarTransacaoPorId({})", id);
        return Optional.ofNullable(transacoes.get(id));
    }

    @Override
    @Deprecated
    public List<Transacao> buscarTransacoesPorCategoriaId(Long categoriaId) {
        log.warn("Método deprecated sendo usado: buscarTransacoesPorCategoriaId({})", categoriaId);

        if (categoriaId == null) {
            return Collections.emptyList();
        }

        return transacoes.values().stream()
                .filter(transacao -> transacao.getCategoriaId() != null)
                .filter(transacao -> Objects.equals(transacao.getCategoriaId().getValue(), categoriaId))
                .collect(Collectors.toList());
    }
    private Page<Transacao> paginarLista(List<Transacao> lista, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), lista.size());

        List<Transacao> paginatedList = start >= lista.size() ?
                Collections.emptyList() :
                lista.subList(start, end);

        return new PageImpl<>(paginatedList, pageable, lista.size());
    }
    public void limparTodas() {
        transacoes.clear();
        idGenerator.set(1);
        log.debug("Todas as transações foram removidas da memória");
    }
    public long contarTodas() {
        return transacoes.size();
    }
    public boolean existeTransacaoParaUsuario(Long transacaoId, Long usuarioId) {
        return buscarTransacaoPorIdEUsuario(transacaoId, usuarioId).isPresent();
    }
    public long contarTransacoesPorUsuario(Long usuarioId) {
        return transacoes.values().stream()
                .filter(transacao -> Objects.equals(transacao.getUsuarioId().getValue(), usuarioId))
                .count();
    }
    public long contarTransacoesPorCategoriaEUsuario(Long categoriaId, Long usuarioId) {
        return transacoes.values().stream()
                .filter(transacao -> Objects.equals(transacao.getUsuarioId().getValue(), usuarioId))
                .filter(transacao -> Objects.equals(transacao.getCategoriaId().getValue(), categoriaId))
                .count();
    }
    public boolean removerTransacao(Long transacaoId) {
        boolean removed = transacoes.remove(transacaoId) != null;
        if (removed) {
            log.debug("Transação {} removida da memória", transacaoId);
        }
        return removed;
    }
    public List<Transacao> buscarPorPeriodoEUsuario(Long usuarioId,
                                                    java.time.LocalDateTime dataInicio,
                                                    java.time.LocalDateTime dataFim) {
        return transacoes.values().stream()
                .filter(transacao -> Objects.equals(transacao.getUsuarioId().getValue(), usuarioId))
                .filter(transacao -> transacao.getData().isAfter(dataInicio) || transacao.getData().isEqual(dataInicio))
                .filter(transacao -> transacao.getData().isBefore(dataFim) || transacao.getData().isEqual(dataFim))
                .sorted((t1, t2) -> t2.getData().compareTo(t1.getData()))
                .collect(Collectors.toList());
    }
    public Map<String, Object> obterEstatisticas() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTransacoes", transacoes.size());
        stats.put("proximoId", idGenerator.get());

        // Agrupa transações por usuário
        Map<Long, Long> transacoesPorUsuario = transacoes.values().stream()
                .collect(Collectors.groupingBy(
                        t -> t.getUsuarioId().getValue(),
                        Collectors.counting()
                ));
        stats.put("transacoesPorUsuario", transacoesPorUsuario);

        return stats;
    }
    public void definirProximoId(Long proximoId) {
        idGenerator.set(proximoId);
        log.debug("Próximo ID definido como: {}", proximoId);
    }
    public Long obterProximoId() {
        return idGenerator.get();
    }
}