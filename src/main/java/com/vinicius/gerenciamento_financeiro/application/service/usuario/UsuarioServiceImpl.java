package com.vinicius.gerenciamento_financeiro.application.service.usuario;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.UsuarioMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.LoginRequest;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.UsuarioPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.AuthenticationResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.UsuarioResponse;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.auditoria.AuditoriaJpa;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.categoria.entity.CategoriaJpaEntity;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.usuario.entity.UsuarioJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
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

    @Override
    public AuthenticationResponse save(UsuarioPost usuarioPost) {
        try {
            log.debug("Iniciando cadastro de usuário: {}", usuarioPost.email());

            if (usuarioRepository.existsByEmail(usuarioPost.email())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email já cadastrado");
            }

            String hashSenha = passwordEncoder.encode(usuarioPost.senha());
            Usuario usuario = mapper.toEntity(usuarioPost, hashSenha);

            Usuario usuarioSalvo = usuarioRepository.save(usuario);
            log.info("Usuário cadastrado com sucesso: ID {}", usuarioSalvo.getId().getValue());

            criarCategoriasPadrao(usuarioSalvo);

            LoginRequest login = LoginRequest.builder()
                    .email(usuarioPost.email())
                    .senha(usuarioPost.senha())
                    .build();

            return loginUseCase.autenticar(login);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao cadastrar usuário: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro interno ao cadastrar usuário");
        }
    }

    @Override
    public UsuarioResponse findById(Long id) {
        try {
            log.debug("Buscando usuário por ID: {}", id);

            UsuarioId usuarioId = UsuarioId.of(id);
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            return mapper.toResponse(usuario);

        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar usuário: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro interno ao buscar usuário");
        }
    }

    private void criarCategoriasPadrao(Usuario usuario) {
        try {
            log.debug("Criando categorias padrão para usuário: {}", usuario.getId().getValue());

            List<Categoria> categoriasPadrao = List.of(
                    Categoria.criar(
                            "A Pagar",
                            "Despesas pendentes",
                            "icone-apagar",
                            usuario.getId()
                    ),

                    Categoria.criar(
                            "Pretendidas",
                            "Despesas planejadas",
                            "icone-pretendidas",
                            usuario.getId()
                    ),

                    Categoria.criar(
                            "Prazo",
                            "Despesas com prazo",
                            "icone-prazo",
                            usuario.getId()
                    ),

                    Categoria.criar(
                            "Pagas",
                            "Despesas quitadas",
                            "icone-pagas",
                            usuario.getId()
                    )
            );

            categoriaRepository.saveAll(categoriasPadrao);
            log.info("Categorias padrão criadas para usuário: {}", usuario.getEmail().getEndereco());

        } catch (Exception e) {
            log.error("Erro ao criar categorias padrão para usuário {}: {}",
                    usuario.getId().getValue(), e.getMessage(), e);
        }
    }
}