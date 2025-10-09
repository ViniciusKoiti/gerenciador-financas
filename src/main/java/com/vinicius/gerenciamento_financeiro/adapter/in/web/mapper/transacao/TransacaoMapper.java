package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponseMapper;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MoedaId;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MontanteMonetario;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.ConfiguracaoTransacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import org.springframework.stereotype.Component;

@Component
public class TransacaoMapper {

    private final TransacaoResponseMapper transacaoResponseMapper;


    private final ConfiguracaoTransacaoMapper configuracaoMapper;

    public TransacaoMapper(TransacaoResponseMapper transacaoResponseMapper, ConfiguracaoTransacaoMapper configuracaoMapper) {
        this.transacaoResponseMapper = transacaoResponseMapper;
        this.configuracaoMapper = configuracaoMapper;
    }

    /**
     * Converte TransacaoPost para entidade de domínio Transacao
     *
     * @param transacaoPost - dados da requisição HTTP
     * @param categoriaId - ID da categoria já validada
     * @param usuarioId - ID do usuário autenticado
     * @param moedaId - ID da moeda (pode vir do request ou ser padrão BRL)
     * @return Transacao - entidade de domínio criada
     */
    public Transacao toEntity(TransacaoPost transacaoPost,
                              CategoriaId categoriaId,
                              UsuarioId usuarioId,
                              MoedaId moedaId) {

        if (transacaoPost == null) {
            throw new IllegalArgumentException("TransacaoPost não pode ser nulo");
        }

        MontanteMonetario montante = MontanteMonetario.of(
                transacaoPost.valor(),
                moedaId
        );

        // Mapeia a configuração
        ConfiguracaoTransacao configuracao = configuracaoMapper.toDomain(
                transacaoPost.configuracao()
        );

        // Usa o método NOVO (não deprecated) para criar a transação
        return Transacao.criarNova(
                transacaoPost.descricao(),
                montante, // Agora usa MontanteMonetario
                transacaoPost.tipoMovimentacao(),
                transacaoPost.data(),
                categoriaId,
                usuarioId,
                null, // clienteId - se não vier no request
                configuracao,
                transacaoPost.observacoes()
        );
    }

    /**
     * Sobrecarga para facilitar uso quando moeda não é informada (usa BRL padrão)
     */
    public Transacao toEntity(TransacaoPost transacaoPost,
                              CategoriaId categoriaId,
                              UsuarioId usuarioId) {
        return toEntity(transacaoPost, categoriaId, usuarioId, MoedaId.of("BRL"));
    }

    /**
     * Converte entidade de domínio para resposta HTTP
     */
    public TransacaoResponse toResponse(Transacao transacao) {
        if (transacao == null) {
            throw new IllegalArgumentException("Transacao não pode ser nula");
        }
        return transacaoResponseMapper.toResponse(transacao);
    }
}