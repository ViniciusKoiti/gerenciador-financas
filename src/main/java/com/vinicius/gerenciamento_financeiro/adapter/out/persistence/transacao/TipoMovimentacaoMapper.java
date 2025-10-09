package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoMovimentacaoEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoMovimentacao;
import org.springframework.stereotype.Component;

@Component
public class TipoMovimentacaoMapper {

    public TipoMovimentacaoEntity toEntity(TipoMovimentacao d) {
        return d == null ? null : TipoMovimentacaoEntity.valueOf(d.name());
    }
    public TipoMovimentacao toDomain(TipoMovimentacaoEntity e) {
        return e == null ? null : TipoMovimentacao.valueOf(e.name());
    }
}
