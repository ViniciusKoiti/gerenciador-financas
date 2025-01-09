package com.vinicius.gerenciamento_financeiro.domain.service.usuario;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.UsuarioMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.UsuarioPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.UsuarioResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.port.in.UsuarioService;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {


    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper mapper;

    public UsuarioResponse save(UsuarioPost usuarioPost) {
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
        return mapper.toResponse(usuarioSalvo);
    }

    @Override
    public UsuarioResponse findById(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return mapper.toResponse(usuario);
    }


}
