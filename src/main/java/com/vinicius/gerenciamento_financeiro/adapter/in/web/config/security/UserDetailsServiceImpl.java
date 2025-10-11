package com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.SpringUserDetails;
import com.vinicius.gerenciamento_financeiro.domain.model.pessoa.Email;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.port.in.UsuarioService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioService usuarioService;

    public UserDetailsServiceImpl(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Usuario usuario = usuarioService.buscarPorEmail(new Email(username));
        if (usuario == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new SpringUserDetails(usuario);
    }
}