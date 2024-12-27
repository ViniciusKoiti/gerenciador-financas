package com.vinicius.gerenciamento_financeiro.domain.service.usuario;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.UsuarioMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.UsuarioPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.response.UsuarioResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.port.in.UsuarioService;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {


    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper mapper;

    public UsuarioResponse save(UsuarioPost usuarioPost) {
        if (usuarioRepository.existsByEmail(usuarioPost.email())) {
            throw new RuntimeException("Email já cadastrado");
        }

        Usuario usuario = Usuario.builder()
                .nome(usuarioPost.nome())
                .email(usuarioPost.email())
                .senha(passwordEncoder.encode(usuarioPost.senha()))
                .build();

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return mapper.toResponse(usuarioSalvo);
    }

    @Override
    public UsuarioResponse buscarPorId(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    return new UsernameNotFoundException("Usuário não encontrado");
                });

        return mapper.toResponse(usuario);
    }


}
