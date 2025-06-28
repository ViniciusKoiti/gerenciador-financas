// ETAPA 5: Atualizar domain/service/usuario/UsuarioServiceImpl.java
package com.vinicius.gerenciamento_financeiro.domain.service.usuario;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.UsuarioMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.LoginRequest;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.UsuarioPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.AuthenticationResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.UsuarioResponse;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.auditoria.AuditoriaJpa;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.categoria.entity.CategoriaJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.port.in.LoginUseCase;
import com.vinicius.gerenciamento_financeiro.port.in.UsuarioService;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginUseCase loginUseCase;
    private final UsuarioMapper mapper;

    public AuthenticationResponse save(UsuarioPost usuarioPost) {
        if (usuarioRepository.existsByEmail(usuarioPost.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado");
        }

        String hashSenha = passwordEncoder.encode(usuarioPost.senha());

        Usuario usuario = mapper.toEntity(usuarioPost, hashSenha);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        criarCategoriasPadrao(usuarioSalvo);

        LoginRequest login = LoginRequest.builder()
                .email(usuarioPost.email())
                .senha(usuarioPost.senha())
                .build();

        return loginUseCase.autenticar(login);
    }

    @Override
    public UsuarioResponse findById(Long id) {
        UsuarioId usuarioId = UsuarioId.of(id);
        Usuario usuario = usuarioRepository.findById(usuarioId.getValue())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return mapper.toResponse(usuario);
    }

    private void criarCategoriasPadrao(Usuario usuario) {
        List<CategoriaJpaEntity> categoriasPadrao = List.of(
                CategoriaJpaEntity.builder()
                        .nome("A Pagar")
                        .descricao("Despesas pendentes")
                        .ativa(true)
                        .icone("icone-apagar")
                        .auditoria(new AuditoriaJpa())
                        .usuario(null)
                        .build(),

                CategoriaJpaEntity.builder()
                        .nome("Pretendidas")
                        .descricao("Despesas planejadas")
                        .ativa(true)
                        .icone("icone-pretendidas")
                        .auditoria(new AuditoriaJpa())
                        .usuario(null) // TODO: Ajustar
                        .build(),

                CategoriaJpaEntity.builder()
                        .nome("Prazo")
                        .descricao("Despesas com prazo")
                        .ativa(true)
                        .icone("icone-prazo")
                        .auditoria(new AuditoriaJpa())
                        .usuario(null) // TODO: Ajustar
                        .build(),

                CategoriaJpaEntity.builder()
                        .nome("Pagas")
                        .descricao("Despesas quitadas")
                        .ativa(true)
                        .icone("icone-pagas")
                        .auditoria(new AuditoriaJpa())
                        .usuario(null) // TODO: Ajustar
                        .build()
        );

        // categoriaRepository.saveAll(categoriasPadrao);

        log.info("Categorias padrão criadas para usuário: {}", usuario.getEmail());

        categoriaRepository.saveAll(categoriasPadrao);
    }
}