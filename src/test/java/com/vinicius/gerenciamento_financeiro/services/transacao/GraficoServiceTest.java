package com.vinicius.gerenciamento_financeiro.services.transacao;

import com.vinicius.gerenciamento_financeiro.port.in.GerarGraficoUseCase;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GraficoServiceTest {

    @InjectMocks
    private GerarGraficoUseCase gerarGraficoUseCase;


    public void deveRetornar_TotalDasTransacoesPorCategoria_QuandoUsuarioValido() {

    }
}
