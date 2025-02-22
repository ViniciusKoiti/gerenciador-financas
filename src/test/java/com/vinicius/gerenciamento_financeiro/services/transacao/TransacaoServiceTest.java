package com.vinicius.gerenciamento_financeiro.services.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtService;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.TransacaoMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoMovimentacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.service.transacao.NotificarTransacaoService;
import com.vinicius.gerenciamento_financeiro.domain.service.transacao.TransacaoService;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import com.vinicius.gerenciamento_financeiro.port.out.transacao.TransacaoRepository;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransacaoServiceTest {
    @InjectMocks
    private TransacaoService transacaoService;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private NotificarTransacaoService notificarTransacaoService;

    @Mock
    private TransacaoMapper transacaoMapper;

    @Test
    void deveAdicionarTransacaoComSucesso() {
        Long usuarioId = 1L;
        Long categoriaId = 2L;
        Usuario usuario = new Usuario(usuarioId);
        Categoria categoria = new Categoria(categoriaId, "Salário", "Descrição","Icone", usuario);
        TransacaoPost transacaoPost = new TransacaoPost("descricao", BigDecimal.valueOf(100), TipoMovimentacao.RECEITA, LocalDateTime.now(), categoriaId);
        Auditoria auditoria = new Auditoria();
        Transacao transacao = new Transacao(10L, "Descricao", BigDecimal.valueOf(100), TipoMovimentacao.RECEITA, LocalDateTime.now(),usuario);
        when(jwtService.getByAutenticaoUsuarioId()).thenReturn(usuarioId);
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(transacaoMapper.toEntity(any(TransacaoPost.class), any(Categoria.class), any(Usuario.class), any(Auditoria.class)))
                .thenReturn(transacao);
        transacaoService.adicionarTransacao(transacaoPost);
        verify(transacaoRepository, times(1)).salvarTransacao(transacao);
        verify(notificarTransacaoService, times(1)).notificarTransacaoAtrasada(transacao);
    }
}
