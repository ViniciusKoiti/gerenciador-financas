package com.vinicius.gerenciamento_financeiro.application.service.usuario;

import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.pessoa.Email;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.port.in.UsuarioService;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    public Usuario registrar(Usuario usuario) {
        try {
            log.debug("Registrando usuario: {}", usuario.getEmail().getEndereco());

            if (usuarioRepository.existsByEmail(usuario.getEmail().getEndereco())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email ja cadastrado");
            }

            Usuario usuarioSalvo = usuarioRepository.save(usuario);
            log.info("Usuario registrado com sucesso: ID {}", usuarioSalvo.getId().getValue());

            criarCategoriasPadrao(usuarioSalvo);

            return usuarioSalvo;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao registrar usuario: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro interno ao registrar usuario");
        }
    }

    @Override
    public Usuario buscarPorEmail(Email email) {
        try {
            log.debug("Buscando usuario por email: {}", email.getEndereco());

            return usuarioRepository.findByEmail(email.getEndereco())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));

        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar usuario por email {}: {}", email.getEndereco(), e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro interno ao buscar usuario");
        }
    }

    @Override
    public Usuario buscarPorId(UsuarioId id) {
        try {
            log.debug("Buscando usuario por ID: {}", id.getValue());

            return usuarioRepository.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));

        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar usuario: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro interno ao buscar usuario");
        }
    }

    @Override
    public Usuario atualizar(Usuario usuario) {
        try {
            log.debug("Atualizando usuario: {}", usuario.getId().getValue());

            usuarioRepository.findById(usuario.getId())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));

            Usuario usuarioAtualizado = usuarioRepository.save(usuario);
            log.info("Usuario atualizado com sucesso: ID {}", usuarioAtualizado.getId().getValue());

            return usuarioAtualizado;

        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao atualizar usuario: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro interno ao atualizar usuario");
        }
    }

    private void criarCategoriasPadrao(Usuario usuario) {
        try {
            log.debug("Criando categorias padrao para usuario: {}", usuario.getId().getValue());

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
            log.info("Categorias padrao criadas para usuario: {}", usuario.getEmail().getEndereco());

        } catch (Exception e) {
            log.error("Erro ao criar categorias padrao para usuario {}: {}",
                    usuario.getId().getValue(), e.getMessage(), e);
        }
    }
}
