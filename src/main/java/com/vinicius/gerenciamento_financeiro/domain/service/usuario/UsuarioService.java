package com.vinicius.gerenciamento_financeiro.domain.service.usuario;

import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    // TODO: Adicione as dependências necessárias

    public Usuario save(Usuario entity) {
        // TODO: Implemente a lógica de salvamento
        return entity;
    }

    public Usuario findById(Long id) {
        // TODO: Implemente a lógica de busca
        return null;
    }

    public List<Usuario> findAll() {
        // TODO: Implemente a lógica de listagem
        return List.of();
    }

    public void delete(Long id) {
        // TODO: Implemente a lógica de deleção
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
