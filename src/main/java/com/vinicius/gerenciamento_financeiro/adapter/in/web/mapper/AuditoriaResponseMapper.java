package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.cliente.AuditoriaResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuditoriaResponseMapper {

    public AuditoriaResponse toResponse(Auditoria auditoria) {
        if (auditoria == null) {
            return null;
        }

        return new AuditoriaResponse(
                auditoria.getCriadoEm(),
                auditoria.getAtualizadoEm()
        );
    }

    public List<AuditoriaResponse> toResponseList(List<Auditoria> auditorias) {
        if (auditorias == null) {
            return List.of();
        }

        return auditorias.stream()
                .map(this::toResponse)
                .toList();
    }
}