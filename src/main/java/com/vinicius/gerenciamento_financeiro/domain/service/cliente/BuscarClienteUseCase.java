package com.vinicius.gerenciamento_financeiro.domain.service.cliente;

import com.vinicius.gerenciamento_financeiro.port.out.cliente.ClienteRepository;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioAutenticadoPort;
import org.springframework.stereotype.Service;

@Service
public class BuscarClienteUseCase implements com.vinicius.gerenciamento_financeiro.port.in.BuscarClienteUseCase {
    private final ClienteRepository clienteRepository;
    private final UsuarioAutenticadoPort usuarioAutenticadoPort;
    public BuscarClienteUseCase(ClienteRepository clienteRepository, UsuarioAutenticadoPort usuarioAutenticadoPort) {
        this.clienteRepository = clienteRepository;
        this.usuarioAutenticadoPort = usuarioAutenticadoPort;
    }

}
