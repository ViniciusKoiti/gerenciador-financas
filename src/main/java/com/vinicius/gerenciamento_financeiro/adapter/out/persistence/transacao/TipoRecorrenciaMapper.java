package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoRecorrenciaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoRecorrencia;
import org.springframework.stereotype.Component;

@Component
public class TipoRecorrenciaMapper {



    public TipoRecorrenciaEntity toEntity(TipoRecorrencia d) {
        return d == null ? null : TipoRecorrenciaEntity.valueOf(d.name());
    }
    public TipoRecorrencia toDomain(TipoRecorrenciaEntity e) {
        return e == null ? null : TipoRecorrencia.valueOf(e.name());
    }




}
