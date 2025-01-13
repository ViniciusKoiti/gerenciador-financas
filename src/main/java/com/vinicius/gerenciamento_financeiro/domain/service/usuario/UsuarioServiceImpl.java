package com.vinicius.gerenciamento_financeiro.domain.service.usuario;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.UsuarioMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.LoginRequest;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.UsuarioPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.AuthenticationResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.UsuarioResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.port.in.LoginUseCase;
import com.vinicius.gerenciamento_financeiro.port.in.UsuarioService;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Email já cadastrado");
        }

        Usuario usuario = Usuario.builder()
                .nome(usuarioPost.nome())
                .email(usuarioPost.email())
                .senha(passwordEncoder.encode(usuarioPost.senha()))
                .auditoria(new Auditoria())
                .build();

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        List<Categoria> categoriasPadrao = List.of(
                new Categoria(
                        null,
                        "A Pagar",
                        "Despesas pendentes",
                        true,
                        "icone-apagar",
                        null,
                        null,
                        null,
                        usuarioSalvo,
                        new Auditoria()
                ),
                new Categoria(
                        null,
                        "Pretendidas",
                        "Despesas planejadas",
                        true,
                        "icone-pretendidas",
                        null,
                        null,
                        null,
                        usuarioSalvo,
                        new Auditoria()
                ),
                new Categoria(
                        null,
                        "Prazo",
                        "Despesas com prazo",
                        true,
                        "icone-prazo",
                        null,
                        null,
                        null,
                        usuarioSalvo,
                        new Auditoria()
                ),
                new Categoria(
                        null,
                        "Pagas",
                        "Despesas quitadas",
                        true,
                        "icone-pagas",
                        null,
                        null,
                        null,
                        usuarioSalvo,
                        new Auditoria()
                )
        );

        categoriaRepository.saveAll(categoriasPadrao);

        LoginRequest login = LoginRequest.builder()
                .email(usuarioPost.email())
                .senha(usuarioPost.senha())
                .build();
        return loginUseCase.autenticar(login);
    }

    @Override
    public UsuarioResponse findById(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return mapper.toResponse(usuario);
    }


}
