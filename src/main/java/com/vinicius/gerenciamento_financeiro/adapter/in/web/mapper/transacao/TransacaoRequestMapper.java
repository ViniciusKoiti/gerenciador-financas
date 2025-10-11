package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteId;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MoedaId;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MontanteMonetario;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import org.springframework.stereotype.Component;

@Component
public class TransacaoRequestMapper {

    public Transacao toDomain(TransacaoPost request, UsuarioId usuarioId) {
        MoedaId moedaId = MoedaId.of(request.getMoedaIdOuPadrao());
        MontanteMonetario montante = MontanteMonetario.of(request.valor(), moedaId);
        CategoriaId categoriaId = CategoriaId.of(request.categoriaId());
        ClienteId clienteId = request.clienteId() != null ? ClienteId.of(request.clienteId()) : null;

        return Transacao.criarNova(
                request.descricao(),
                montante,
                request.tipoMovimentacao(),
                request.data(),
                categoriaId,
                usuarioId,
                clienteId,
                null, // configuracao will use default
                request.observacoes()
        );
    }
}