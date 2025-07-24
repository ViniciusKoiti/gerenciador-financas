package com.vinicius.gerenciamento_financeiro.adapter.in.web.controller.cliente;

import com.vinicius.gerenciamento_financeiro.port.in.BuscarClienteUseCase;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cliente")
@RequiredArgsConstructor
public class ClienteController {

    private final BuscarClienteUseCase buscarClienteUseCase;

}
