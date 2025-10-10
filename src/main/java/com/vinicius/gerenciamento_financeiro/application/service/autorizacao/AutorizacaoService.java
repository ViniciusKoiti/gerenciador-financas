package com.vinicius.gerenciamento_financeiro.application.service.autorizacao;

import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.port.in.UsuarioAutenticadoPort;
import com.vinicius.gerenciamento_financeiro.port.in.VerificarPropriedadeRecursoUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutorizacaoService  implements VerificarPropriedadeRecursoUseCase {

    private final CategoriaRepository categoriaRepository;
    private final UsuarioAutenticadoPort usuarioAutenticadoPort;

    @Override
    public boolean verificarPropriedadeCategoria(CategoriaId categoriaId) {
        try {
            UsuarioId usuarioAtual = usuarioAutenticadoPort.obterUsuarioAtual();
            boolean pertence = categoriaRepository.existsByIdAndUsuarioId(categoriaId, usuarioAtual);

            if (!pertence) {
                log.warn("Acesso negado à categoria {}: não pertence ao usuário {}",
                        categoriaId.getValue(), usuarioAtual.getValue());
            }

            return pertence;

        } catch (Exception e) {
            log.error("Erro ao verificar propriedade da categoria", e);
            return false;
        }
    }
}
