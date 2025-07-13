package com.vinicius.gerenciamento_financeiro.services.grafico;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.ResumoFinanceiroResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.TransacaoPorPeriodoResponse;
import com.vinicius.gerenciamento_financeiro.domain.exception.UsuarioNaoAutenticadoException;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.domain.service.grafico.GraficoService;
import com.vinicius.gerenciamento_financeiro.port.out.grafico.GraficoRepository;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioAutenticadoPort;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do GraficoService - Refatorado com UsuarioAutenticadoPort")
class GraficoServiceTest {

    @Mock
    private GraficoRepository graficoRepository;

    @Mock
    private UsuarioAutenticadoPort usuarioAutenticadoPort;

    @InjectMocks
    private GraficoService graficoService;

    private UsuarioId usuarioId;
    private ZonedDateTime dataInicio;
    private ZonedDateTime dataFim;
    private LocalDateTime dataInicial;
    private LocalDateTime dataFinal;

    @BeforeEach
    void setUp() {
        usuarioId = UsuarioId.of(1L);
        dataInicio = ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault());
        dataFim = ZonedDateTime.of(2024, 12, 31, 23, 59, 59, 0, ZoneId.systemDefault());
        dataInicial = dataInicio.toLocalDateTime();
        dataFinal = dataFim.toLocalDateTime();
    }

    @Nested
    @DisplayName("Testes do método gerarGraficoPorCategoria")
    class GerarGraficoPorCategoriaTests {

        @Test
        @DisplayName("Deve gerar gráfico por categoria com sucesso")
        void deveGerarGraficoPorCategoriaComSucesso() {
            // Arrange
            List<GraficoResponse> graficoResponseEsperado = Arrays.asList(
                    new GraficoResponse("Alimentação", new BigDecimal("500.00")),
                    new GraficoResponse("Transporte", new BigDecimal("300.00")),
                    new GraficoResponse("Lazer", new BigDecimal("200.00"))
            );

            when(usuarioAutenticadoPort.obterUsuarioAtual()).thenReturn(usuarioId);
            when(graficoRepository.gerarGraficoPorCategoria(usuarioId.getValue(), dataInicial, dataFinal))
                    .thenReturn(graficoResponseEsperado);

            // Act
            List<GraficoResponse> resultado = graficoService.gerarGraficoTotalPorCategoria(dataInicio, dataFim);

            // Assert
            assertNotNull(resultado);
            assertEquals(3, resultado.size());
            assertEquals("Alimentação", resultado.get(0).name());
            assertEquals(new BigDecimal("500.00"), resultado.get(0).value());

            verify(usuarioAutenticadoPort).obterUsuarioAtual();
            verify(graficoRepository).gerarGraficoPorCategoria(usuarioId.getValue(), dataInicial, dataFinal);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há dados")
        void deveRetornarListaVaziaQuandoNaoHaDados() {
            // Arrange
            when(usuarioAutenticadoPort.obterUsuarioAtual()).thenReturn(usuarioId);
            when(graficoRepository.gerarGraficoPorCategoria(usuarioId.getValue(), dataInicial, dataFinal))
                    .thenReturn(Collections.emptyList());

            // Act
            List<GraficoResponse> resultado = graficoService.gerarGraficoTotalPorCategoria(dataInicio, dataFim);

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());

            verify(usuarioAutenticadoPort).obterUsuarioAtual();
            verify(graficoRepository).gerarGraficoPorCategoria(usuarioId.getValue(), dataInicial, dataFinal);
        }

        @Test
        @DisplayName("Deve converter ZonedDateTime para LocalDateTime corretamente")
        void deveConverterZonedDateTimeParaLocalDateTimeCorretamente() {
            // Arrange
            when(usuarioAutenticadoPort.obterUsuarioAtual()).thenReturn(usuarioId);
            when(graficoRepository.gerarGraficoPorCategoria(eq(usuarioId.getValue()), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());

            // Act
            graficoService.gerarGraficoTotalPorCategoria(dataInicio, dataFim);

            // Assert
            verify(graficoRepository).gerarGraficoPorCategoria(
                    eq(usuarioId.getValue()),
                    eq(dataInicial),
                    eq(dataFinal)
            );
        }
    }

    @Nested
    @DisplayName("Testes do método gerarEvolucaoFinanceira")
    class GerarEvolucaoFinanceiraTests {

        @Test
        @DisplayName("Deve gerar evolução financeira com sucesso")
        void deveGerarEvolucaoFinanceiraComSucesso() {
            // Arrange
            List<TransacaoPorPeriodoResponse> evolucaoEsperada = Arrays.asList(
                    new TransacaoPorPeriodoResponse("2024-01", new BigDecimal("1000.00"), new BigDecimal("800.00")),
                    new TransacaoPorPeriodoResponse("2024-02", new BigDecimal("1200.00"), new BigDecimal("900.00")),
                    new TransacaoPorPeriodoResponse("2024-03", new BigDecimal("1100.00"), new BigDecimal("850.00"))
            );

            when(usuarioAutenticadoPort.obterUsuarioAtual()).thenReturn(usuarioId);
            when(graficoRepository.gerarEvolucaoFinanceira(usuarioId.getValue(), dataInicial, dataFinal))
                    .thenReturn(evolucaoEsperada);

            // Act
            List<TransacaoPorPeriodoResponse> resultado = graficoService.gerarEvolucaoFinanceira(dataInicio, dataFim);

            // Assert
            assertNotNull(resultado);
            assertEquals(3, resultado.size());
            assertEquals("2024-01", resultado.get(0).getDataPeriodo());
            assertEquals(new BigDecimal("1000.00"), resultado.get(0).getReceitaValor());
            assertEquals(new BigDecimal("800.00"), resultado.get(0).getDespesaValor());

            verify(usuarioAutenticadoPort).obterUsuarioAtual();
            verify(graficoRepository).gerarEvolucaoFinanceira(usuarioId.getValue(), dataInicial, dataFinal);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há evolução financeira")
        void deveRetornarListaVaziaQuandoNaoHaEvolucaoFinanceira() {
            // Arrange
            when(usuarioAutenticadoPort.obterUsuarioAtual()).thenReturn(usuarioId);
            when(graficoRepository.gerarEvolucaoFinanceira(usuarioId.getValue(), dataInicial, dataFinal))
                    .thenReturn(Collections.emptyList());

            // Act
            List<TransacaoPorPeriodoResponse> resultado = graficoService.gerarEvolucaoFinanceira(dataInicio, dataFim);

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());

            verify(usuarioAutenticadoPort).obterUsuarioAtual();
            verify(graficoRepository).gerarEvolucaoFinanceira(usuarioId.getValue(), dataInicial, dataFinal);
        }
    }

    @Nested
    @DisplayName("Testes do método gerarResumoFinanceiro")
    class GerarResumoFinanceiroTests {

        @Test
        @DisplayName("Deve gerar resumo financeiro com sucesso")
        void deveGerarResumoFinanceiroComSucesso() {
            // Arrange
            ResumoFinanceiroResponse resumoEsperado = new ResumoFinanceiroResponse(
                    new BigDecimal("5000.00"), // totalReceitas
                    new BigDecimal("3500.00"), // totalDespesas
                    new BigDecimal("1500.00")  // saldo
            );

            when(usuarioAutenticadoPort.obterUsuarioAtual()).thenReturn(usuarioId);
            when(graficoRepository.gerarResumoFinanceiro(usuarioId.getValue(), dataInicial, dataFinal))
                    .thenReturn(resumoEsperado);

            // Act
            ResumoFinanceiroResponse resultado = graficoService.gerarResumoFinanceiro(dataInicio, dataFim);

            // Assert
            assertNotNull(resultado);
            assertEquals(new BigDecimal("5000.00"), resultado.getTotalReceitas());
            assertEquals(new BigDecimal("3500.00"), resultado.getTotalDespesas());
            assertEquals(new BigDecimal("1500.00"), resultado.getSaldo());

            verify(usuarioAutenticadoPort).obterUsuarioAtual();
            verify(graficoRepository).gerarResumoFinanceiro(usuarioId.getValue(), dataInicial, dataFinal);
        }

        @Test
        @DisplayName("Deve gerar resumo financeiro com valores zero")
        void deveGerarResumoFinanceiroComValoresZero() {
            // Arrange
            ResumoFinanceiroResponse resumoEsperado = new ResumoFinanceiroResponse(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );

            when(usuarioAutenticadoPort.obterUsuarioAtual()).thenReturn(usuarioId);
            when(graficoRepository.gerarResumoFinanceiro(usuarioId.getValue(), dataInicial, dataFinal))
                    .thenReturn(resumoEsperado);

            // Act
            ResumoFinanceiroResponse resultado = graficoService.gerarResumoFinanceiro(dataInicio, dataFim);

            // Assert
            assertNotNull(resultado);
            assertEquals(BigDecimal.ZERO, resultado.getTotalReceitas());
            assertEquals(BigDecimal.ZERO, resultado.getTotalDespesas());
            assertEquals(BigDecimal.ZERO, resultado.getSaldo());

            verify(usuarioAutenticadoPort).obterUsuarioAtual();
            verify(graficoRepository).gerarResumoFinanceiro(usuarioId.getValue(), dataInicial, dataFinal);
        }



        @Test
        @DisplayName("Deve gerar resumo financeiro com saldo negativo")
        void deveGerarResumoFinanceiroComSaldoNegativo() {
            // Arrange
            ResumoFinanceiroResponse resumoEsperado = new ResumoFinanceiroResponse(
                    new BigDecimal("2000.00"), // totalReceitas
                    new BigDecimal("3000.00"), // totalDespesas
                    new BigDecimal("-1000.00") // saldo negativo
            );

            when(usuarioAutenticadoPort.obterUsuarioAtual()).thenReturn(usuarioId);
            when(graficoRepository.gerarResumoFinanceiro(usuarioId.getValue(), dataInicial, dataFinal))
                    .thenReturn(resumoEsperado);

            // Act
            ResumoFinanceiroResponse resultado = graficoService.gerarResumoFinanceiro(dataInicio, dataFim);

            // Assert
            assertNotNull(resultado);
            assertEquals(new BigDecimal("2000.00"), resultado.getTotalReceitas());
            assertEquals(new BigDecimal("3000.00"), resultado.getTotalDespesas());
            assertEquals(new BigDecimal("-1000.00"), resultado.getSaldo());

            verify(usuarioAutenticadoPort).obterUsuarioAtual();
            verify(graficoRepository).gerarResumoFinanceiro(usuarioId.getValue(), dataInicial, dataFinal);
        }
    }

    @Nested
    @DisplayName("Testes de validação de parâmetros")
    class ValidacaoParametrosTests {

        @Test
        @DisplayName("Deve aceitar diferentes fusos horários para ZonedDateTime")
        void deveAceitarDiferentesFusosHorarios() {
            // Arrange
            ZonedDateTime dataInicioUTC = ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
            ZonedDateTime dataFimUTC = ZonedDateTime.of(2024, 12, 31, 23, 59, 59, 0, ZoneId.of("UTC"));

            when(usuarioAutenticadoPort.obterUsuarioAtual()).thenReturn(usuarioId);
            when(graficoRepository.gerarGraficoPorCategoria(any(), any(), any()))
                    .thenReturn(Collections.emptyList());

            // Act
            graficoService.gerarGraficoTotalPorCategoria(dataInicioUTC, dataFimUTC);

            // Assert
            verify(graficoRepository).gerarGraficoPorCategoria(
                    eq(usuarioId.getValue()),
                    eq(dataInicioUTC.toLocalDateTime()),
                    eq(dataFimUTC.toLocalDateTime())
            );
        }

        @Test
        @DisplayName("Deve converter corretamente datas com nanossegundos")
        void deveConverterCorretamenteDatasComNanossegundos() {
            // Arrange
            ZonedDateTime dataComNanos = ZonedDateTime.of(2024, 6, 15, 14, 30, 45, 123456789, ZoneId.systemDefault());
            ZonedDateTime dataFimComNanos = ZonedDateTime.of(2024, 6, 15, 18, 45, 30, 987654321, ZoneId.systemDefault());

            when(usuarioAutenticadoPort.obterUsuarioAtual()).thenReturn(usuarioId);
            when(graficoRepository.gerarEvolucaoFinanceira(any(), any(), any()))
                    .thenReturn(Collections.emptyList());

            // Act
            graficoService.gerarEvolucaoFinanceira(dataComNanos, dataFimComNanos);

            // Assert
            verify(graficoRepository).gerarEvolucaoFinanceira(
                    eq(usuarioId.getValue()),
                    eq(dataComNanos.toLocalDateTime()),
                    eq(dataFimComNanos.toLocalDateTime())
            );
        }
    }

    @Nested
    @DisplayName("Testes de integração de comportamento")
    class IntegracaoComportamentoTests {

        @Test
        @DisplayName("Deve chamar repository apenas uma vez por método")
        void deveChamarRepositoryApenasUmaVezPorMetodo() {
            // Arrange
            when(usuarioAutenticadoPort.obterUsuarioAtual()).thenReturn(usuarioId);
            when(graficoRepository.gerarGraficoPorCategoria(any(), any(), any()))
                    .thenReturn(Collections.emptyList());

            // Act
            graficoService.gerarGraficoTotalPorCategoria(dataInicio, dataFim);

            // Assert
            verify(graficoRepository, times(1)).gerarGraficoPorCategoria(any(), any(), any());
            verify(graficoRepository, never()).gerarEvolucaoFinanceira(any(), any(), any());
            verify(graficoRepository, never()).gerarResumoFinanceiro(any(), any(), any());
        }

        @Test
        @DisplayName("Deve validar usuário antes de acessar repository")
        void deveValidarUsuarioAntesDeAcessarRepository() {
            // Arrange
            when(usuarioAutenticadoPort.obterUsuarioAtual())
                    .thenThrow(new UsuarioNaoAutenticadoException("Usuário não autenticado"));

            // Act & Assert
            assertThrows(UsuarioNaoAutenticadoException.class,
                    () -> graficoService.gerarGraficoTotalPorCategoria(dataInicio, dataFim));

            // Verifica que o repository não foi chamado
            verify(graficoRepository, never()).gerarGraficoPorCategoria(any(), any(), any());
            verify(usuarioAutenticadoPort, times(1)).obterUsuarioAtual();
        }

        @Test
        @DisplayName("Deve propagar exceções do repository")
        void devePropagarExcecoesDoRepository() {
            // Arrange
            when(usuarioAutenticadoPort.obterUsuarioAtual()).thenReturn(usuarioId);
            when(graficoRepository.gerarResumoFinanceiro(any(), any(), any()))
                    .thenThrow(new RuntimeException("Erro no banco de dados"));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> graficoService.gerarResumoFinanceiro(dataInicio, dataFim));

            assertEquals("Erro no banco de dados", exception.getMessage());
            verify(usuarioAutenticadoPort).obterUsuarioAtual();
            verify(graficoRepository).gerarResumoFinanceiro(usuarioId.getValue(), dataInicial, dataFinal);
        }

        @Test
        @DisplayName("Deve validar consistência de parâmetros entre usuário e repository")
        void deveValidarConsistenciaParametrosEntreUsuarioERepository() {
            // Arrange
            when(usuarioAutenticadoPort.obterUsuarioAtual()).thenReturn(usuarioId);
            when(graficoRepository.gerarGraficoPorCategoria(usuarioId.getValue(), dataInicial, dataFinal))
                    .thenReturn(Collections.emptyList());

            // Act
            graficoService.gerarGraficoTotalPorCategoria(dataInicio, dataFim);

            // Assert - Verifica se os mesmos dados do usuário são passados para o repository
            verify(usuarioAutenticadoPort).obterUsuarioAtual();
            verify(graficoRepository).gerarGraficoPorCategoria(
                    eq(usuarioId.getValue()), // Mesmo ID obtido do port
                    eq(dataInicial),
                    eq(dataFinal)
            );
        }
    }
}