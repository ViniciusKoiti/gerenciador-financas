package com.vinicius.gerenciamento_financeiro.port.in;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.cliente.ClienteFiltroRequest;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.cliente.ClienteResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.Cliente;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.List;

public interface BuscarClienteUseCase {

    Optional<ClienteResponse> findById(ClienteId id);

    boolean existsByIdAndUsuarioId(ClienteId clienteId, UsuarioId usuarioId);

    Page<ClienteResponse> findPageClienteComFiltros(ClienteFiltroRequest filtroRequest, Pageable pageable);

    Page<ClienteResponse> findPageCliente(Pageable pageable);

    List<ClienteResponse> findAllClienteComFiltros(ClienteFiltroRequest filtroRequest);


}
