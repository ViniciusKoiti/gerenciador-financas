package com.vinicius.gerenciamento_financeiro.services.grafico;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtService;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.ResumoFinanceiroResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.TransacaoPorPeriodoResponse;
import com.vinicius.gerenciamento_financeiro.domain.service.grafico.GraficoService;
import com.vinicius.gerenciamento_financeiro.port.out.grafico.GraficoRepository;
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
@DisplayName("Testes do GraficoService")
class GraficoServiceTest {

    @Mock
    private GraficoRepository graficoRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private GraficoService graficoService;

    private Long usuarioId;
    private ZonedDateTime dataInicio;
    private ZonedDateTime dataFim;
    private LocalDateTime dataInicial;
    private LocalDateTime dataFinal;

    @BeforeEach
    void setUp() {
        usuarioId = 1L;
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
            List<GraficoResponse> graficoResponseEsperado = Arrays.asList(
                    new GraficoResponse("Alimentação", new BigDecimal("500.00")),
                    new GraficoResponse("Transporte", new BigDecimal("300.00")),
                    new GraficoResponse("Lazer", new BigDecimal("200.00"))
            );

            Mockito.when(jwtService.getByAutenticaoUsuarioId()).thenReturn(usuarioId);
            Mockito.when(graficoRepository.gerarGraficoPorCategoria(usuarioId, dataInicial, dataFinal))
                    .thenReturn(graficoResponseEsperado);

            // Act
            List<GraficoResponse> resultado = graficoService.gerarGraficoPorCategoria(dataInicio, dataFim);

            // Assert
            Assertions.assertNotNull(resultado);
            Assertions.assertEquals(3, resultado.size());
            Assertions.assertEquals("Alimentação", resultado.get(0).name());
            Assertions.assertEquals(new BigDecimal("500.00"), resultado.get(0).value());

            Mockito.verify(jwtService).getByAutenticaoUsuarioId();
            Mockito.verify(graficoRepository).gerarGraficoPorCategoria(usuarioId, dataInicial, dataFinal);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há dados")
        void deveRetornarListaVaziaQuandoNaoHaDados() {
            // Arrange
            Mockito.when(jwtService.getByAutenticaoUsuarioId()).thenReturn(usuarioId);
            Mockito.when(graficoRepository.gerarGraficoPorCategoria(usuarioId, dataInicial, dataFinal))
                    .thenReturn(Collections.emptyList());

            // Act
            List<GraficoResponse> resultado = graficoService.gerarGraficoPorCategoria(dataInicio, dataFim);

            // Assert
            Assertions.assertNotNull(resultado);
            Assertions.assertTrue(resultado.isEmpty());

            Mockito.verify(jwtService).getByAutenticaoUsuarioId();
            Mockito.verify(graficoRepository).gerarGraficoPorCategoria(usuarioId, dataInicial, dataFinal);
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário é desconhecido")
        void deveLancarExcecaoQuandoUsuarioEhDesconhecido() {
            // Arrange
            Mockito.when(jwtService.getByAutenticaoUsuarioId()).thenReturn(0L);

            // Act & Assert
            ResponseStatusException exception = Assertions.assertThrows(
                    ResponseStatusException.class,
                    () -> graficoService.gerarGraficoPorCategoria(dataInicio, dataFim)
            );

            Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            Assertions.assertEquals("Usuário desconhecido", exception.getReason());

            Mockito.verify(jwtService).getByAutenticaoUsuarioId();
            Mockito.verify(graficoRepository, Mockito.never()).gerarGraficoPorCategoria(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        @DisplayName("Deve converter ZonedDateTime para LocalDateTime corretamente")
        void deveConverterZonedDateTimeParaLocalDateTimeCorretamente() {
            // Arrange
            Mockito.when(jwtService.getByAutenticaoUsuarioId()).thenReturn(usuarioId);
            Mockito.when(graficoRepository.gerarGraficoPorCategoria(ArgumentMatchers.eq(usuarioId), ArgumentMatchers.any(LocalDateTime.class), ArgumentMatchers.any(LocalDateTime.class)))
                    .thenReturn(Collections.emptyList());

            // Act
            graficoService.gerarGraficoPorCategoria(dataInicio, dataFim);

            // Assert
            Mockito.verify(graficoRepository).gerarGraficoPorCategoria(
                    ArgumentMatchers.eq(usuarioId),
                    ArgumentMatchers.eq(dataInicial),
                    ArgumentMatchers.eq(dataFinal)
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

            Mockito.when(jwtService.getByAutenticaoUsuarioId()).thenReturn(usuarioId);
            Mockito.when(graficoRepository.gerarEvolucaoFinanceira(usuarioId, dataInicial, dataFinal))
                    .thenReturn(evolucaoEsperada);

            // Act
            List<TransacaoPorPeriodoResponse> resultado = graficoService.gerarEvolucaoFinanceira(dataInicio, dataFim);

            // Assert
            Assertions.assertNotNull(resultado);
            Assertions.assertEquals(3, resultado.size());
            Assertions.assertEquals("2024-01", resultado.get(0).getDataPeriodo());
            Assertions.assertEquals(new BigDecimal("1000.00"), resultado.get(0).getReceitaValor());
            Assertions.assertEquals(new BigDecimal("800.00"), resultado.get(0).getDespesaValor());

            Mockito.verify(jwtService).getByAutenticaoUsuarioId();
            Mockito.verify(graficoRepository).gerarEvolucaoFinanceira(usuarioId, dataInicial, dataFinal);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há evolução financeira")
        void deveRetornarListaVaziaQuandoNaoHaEvolucaoFinanceira() {
            // Arrange
            Mockito.when(jwtService.getByAutenticaoUsuarioId()).thenReturn(usuarioId);
            Mockito.when(graficoRepository.gerarEvolucaoFinanceira(usuarioId, dataInicial, dataFinal))
                    .thenReturn(Collections.emptyList());

            // Act
            List<TransacaoPorPeriodoResponse> resultado = graficoService.gerarEvolucaoFinanceira(dataInicio, dataFim);

            // Assert
            Assertions.assertNotNull(resultado);
            Assertions.assertTrue(resultado.isEmpty());

            Mockito.verify(jwtService).getByAutenticaoUsuarioId();
            Mockito.verify(graficoRepository).gerarEvolucaoFinanceira(usuarioId, dataInicial, dataFinal);
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário é desconhecido")
        void deveLancarExcecaoQuandoUsuarioEhDesconhecido() {
            // Arrange
            Mockito.when(jwtService.getByAutenticaoUsuarioId()).thenReturn(0L);

            // Act & Assert
            ResponseStatusException exception = Assertions.assertThrows(
                    ResponseStatusException.class,
                    () -> graficoService.gerarEvolucaoFinanceira(dataInicio, dataFim)
            );

            Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            Assertions.assertEquals("Usuário desconhecido", exception.getReason());

            Mockito.verify(jwtService).getByAutenticaoUsuarioId();
            Mockito.verify(graficoRepository, Mockito.never()).gerarEvolucaoFinanceira(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
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

            Mockito.when(jwtService.getByAutenticaoUsuarioId()).thenReturn(usuarioId);
            Mockito.when(graficoRepository.gerarResumoFinanceiro(usuarioId, dataInicial, dataFinal))
                    .thenReturn(resumoEsperado);

            // Act
            ResumoFinanceiroResponse resultado = graficoService.gerarResumoFinanceiro(dataInicio, dataFim);

            // Assert
            Assertions.assertNotNull(resultado);
            Assertions.assertEquals(new BigDecimal("5000.00"), resultado.getTotalReceitas());
            Assertions.assertEquals(new BigDecimal("3500.00"), resultado.getTotalDespesas());
            Assertions.assertEquals(new BigDecimal("1500.00"), resultado.getSaldo());

            Mockito.verify(jwtService).getByAutenticaoUsuarioId();
            Mockito.verify(graficoRepository).gerarResumoFinanceiro(usuarioId, dataInicial, dataFinal);
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

            Mockito.when(jwtService.getByAutenticaoUsuarioId()).thenReturn(usuarioId);
            Mockito.when(graficoRepository.gerarResumoFinanceiro(usuarioId, dataInicial, dataFinal))
                    .thenReturn(resumoEsperado);

            // Act
            ResumoFinanceiroResponse resultado = graficoService.gerarResumoFinanceiro(dataInicio, dataFim);

            // Assert
            Assertions.assertNotNull(resultado);
            Assertions.assertEquals(BigDecimal.ZERO, resultado.getTotalReceitas());
            Assertions.assertEquals(BigDecimal.ZERO, resultado.getTotalDespesas());
            Assertions.assertEquals(BigDecimal.ZERO, resultado.getSaldo());

            Mockito.verify(jwtService).getByAutenticaoUsuarioId();
            Mockito.verify(graficoRepository).gerarResumoFinanceiro(usuarioId, dataInicial, dataFinal);
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário é desconhecido")
        void deveLancarExcecaoQuandoUsuarioEhDesconhecido() {
            // Arrange
            Mockito.when(jwtService.getByAutenticaoUsuarioId()).thenReturn(0L);

            // Act & Assert
            ResponseStatusException exception = Assertions.assertThrows(
                    ResponseStatusException.class,
                    () -> graficoService.gerarResumoFinanceiro(dataInicio, dataFim)
            );

            Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            Assertions.assertEquals("Usuário desconhecido", exception.getReason());

            Mockito.verify(jwtService).getByAutenticaoUsuarioId();
            Mockito.verify(graficoRepository, Mockito.never()).gerarResumoFinanceiro(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
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

            Mockito.when(jwtService.getByAutenticaoUsuarioId()).thenReturn(usuarioId);
            Mockito.when(graficoRepository.gerarResumoFinanceiro(usuarioId, dataInicial, dataFinal))
                    .thenReturn(resumoEsperado);

            // Act
            ResumoFinanceiroResponse resultado = graficoService.gerarResumoFinanceiro(dataInicio, dataFim);

            // Assert
            Assertions.assertNotNull(resultado);
            Assertions.assertEquals(new BigDecimal("2000.00"), resultado.getTotalReceitas());
            Assertions.assertEquals(new BigDecimal("3000.00"), resultado.getTotalDespesas());
            Assertions.assertEquals(new BigDecimal("-1000.00"), resultado.getSaldo());

            Mockito.verify(jwtService).getByAutenticaoUsuarioId();
            Mockito.verify(graficoRepository).gerarResumoFinanceiro(usuarioId, dataInicial, dataFinal);
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

            Mockito.when(jwtService.getByAutenticaoUsuarioId()).thenReturn(usuarioId);
            Mockito.when(graficoRepository.gerarGraficoPorCategoria(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                    .thenReturn(Collections.emptyList());

            // Act
            graficoService.gerarGraficoPorCategoria(dataInicioUTC, dataFimUTC);

            // Assert
            Mockito.verify(graficoRepository).gerarGraficoPorCategoria(
                    ArgumentMatchers.eq(usuarioId),
                    ArgumentMatchers.eq(dataInicioUTC.toLocalDateTime()),
                    ArgumentMatchers.eq(dataFimUTC.toLocalDateTime())
            );
        }

        @Test
        @DisplayName("Deve converter corretamente datas com nanossegundos")
        void deveConverterCorretamenteDatasComNanossegundos() {
            // Arrange
            ZonedDateTime dataComNanos = ZonedDateTime.of(2024, 6, 15, 14, 30, 45, 123456789, ZoneId.systemDefault());
            ZonedDateTime dataFimComNanos = ZonedDateTime.of(2024, 6, 15, 18, 45, 30, 987654321, ZoneId.systemDefault());

            Mockito.when(jwtService.getByAutenticaoUsuarioId()).thenReturn(usuarioId);
            Mockito.when(graficoRepository.gerarEvolucaoFinanceira(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                    .thenReturn(Collections.emptyList());

            // Act
            graficoService.gerarEvolucaoFinanceira(dataComNanos, dataFimComNanos);

            // Assert
            Mockito.verify(graficoRepository).gerarEvolucaoFinanceira(
                    ArgumentMatchers.eq(usuarioId),
                    ArgumentMatchers.eq(dataComNanos.toLocalDateTime()),
                    ArgumentMatchers.eq(dataFimComNanos.toLocalDateTime())
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
            Mockito.when(jwtService.getByAutenticaoUsuarioId()).thenReturn(usuarioId);
            Mockito.when(graficoRepository.gerarGraficoPorCategoria(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                    .thenReturn(Collections.emptyList());

            // Act
            graficoService.gerarGraficoPorCategoria(dataInicio, dataFim);

            // Assert
            Mockito.verify(graficoRepository, Mockito.times(1)).gerarGraficoPorCategoria(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
            Mockito.verify(graficoRepository, Mockito.never()).gerarEvolucaoFinanceira(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
            Mockito.verify(graficoRepository, Mockito.never()).gerarResumoFinanceiro(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        }

        @Test
        @DisplayName("Deve validar usuário antes de acessar repository")
        void deveValidarUsuarioAntesDeAcessarRepository() {
            // Arrange
            Mockito.when(jwtService.getByAutenticaoUsuarioId()).thenReturn(0L);

            // Act & Assert
            Assertions.assertThrows(ResponseStatusException.class,
                    () -> graficoService.gerarGraficoPorCategoria(dataInicio, dataFim));

            // Verifica que o repository não foi chamado
            Mockito.verify(graficoRepository, Mockito.never()).gerarGraficoPorCategoria(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
            Mockito.verify(jwtService, Mockito.times(1)).getByAutenticaoUsuarioId();
        }

        @Test
        @DisplayName("Deve propagar exceções do repository")
        void devePropagarExcecoesDoRepository() {
            // Arrange
            Mockito.when(jwtService.getByAutenticaoUsuarioId()).thenReturn(usuarioId);
            Mockito.when(graficoRepository.gerarResumoFinanceiro(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                    .thenThrow(new RuntimeException("Erro no banco de dados"));

            // Act & Assert
            RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                    () -> graficoService.gerarResumoFinanceiro(dataInicio, dataFim));

            Assertions.assertEquals("Erro no banco de dados", exception.getMessage());
            Mockito.verify(jwtService).getByAutenticaoUsuarioId();
            Mockito.verify(graficoRepository).gerarResumoFinanceiro(usuarioId, dataInicial, dataFinal);
        }
    }
}