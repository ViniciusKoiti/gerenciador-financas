package com.vinicius.gerenciamento_financeiro.domain.service.cliente;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.cliente.ClienteResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.Cliente;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.port.in.BuscarClienteUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.cliente.ClienteRepository;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioAutenticadoPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BuscarClienteUseCaseImpl implements BuscarClienteUseCase {
    private final ClienteRepository clienteRepository;
    private final UsuarioAutenticadoPort usuarioAutenticadoPort;
    public BuscarClienteUseCaseImpl(ClienteRepository clienteRepository, UsuarioAutenticadoPort usuarioAutenticadoPort) {
        this.clienteRepository = clienteRepository;
        this.usuarioAutenticadoPort = usuarioAutenticadoPort;
    }

    @Override
    public Optional<ClienteResponse> findById(ClienteId id) {
        return Optional.empty();
    }

    @Override
    public boolean existsByIdAndUsuarioId(ClienteId clienteId, UsuarioId usuarioId) {
        return false;
    }

    @Override
    public Optional<ClienteResponse> findByIdAndUsuarioId(ClienteId clienteId, UsuarioId usuarioId) {
        return Optional.empty();
    }

    @Override
    public List<ClienteResponse> findByUsuarioId(UsuarioId usuarioId) {
        return null;
    }
}
