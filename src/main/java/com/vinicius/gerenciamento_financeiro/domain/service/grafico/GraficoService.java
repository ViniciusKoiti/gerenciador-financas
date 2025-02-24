package com.vinicius.gerenciamento_financeiro.domain.service.grafico;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtService;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse;
import com.vinicius.gerenciamento_financeiro.port.in.GerarGraficoUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.grafico.GraficoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class GraficoService implements GerarGraficoUseCase {

    private final GraficoRepository graficoRepository;
    private final JwtService jwtService;


    @Override
    public List<GraficoResponse> gerarGraficoPorCategoria(ZonedDateTime dataInicio, ZonedDateTime dataFim) {
        Long usuarioId = jwtService.getByAutenticaoUsuarioId();
        if (usuarioId == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário desconhecido");
        }
        return graficoRepository.gerarGraficoPorCategoria(usuarioId,dataInicio, dataInicio );
    }
}
