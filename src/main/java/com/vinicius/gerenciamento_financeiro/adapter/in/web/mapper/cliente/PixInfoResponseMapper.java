package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.cliente;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.cliente.PixInfoResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.PixInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PixInfoResponseMapper {

    public PixInfoResponse toResponse(PixInfo pixInfo) {
        if (pixInfo == null) {
            return null;
        }

        return new PixInfoResponse(
                pixInfo.getTipo() != null ? pixInfo.getTipo().name() : null,
                pixInfo.getChave(),
                pixInfo.getBanco(),
                pixInfo.isAtivo()
        );
    }

    public List<PixInfoResponse> toResponseList(List<PixInfo> pixInfos) {
        if (pixInfos == null) {
            return List.of();
        }

        return pixInfos.stream()
                .map(this::toResponse)
                .toList();
    }

    public PixInfoResponse toMinimalResponse(PixInfo pixInfo) {
        if (pixInfo == null) {
            return null;
        }

        return new PixInfoResponse(
                pixInfo.getTipo() != null ? pixInfo.getTipo().name() : null,
                pixInfo.getChave(),
                pixInfo.isAtivo()
        );
    }
}