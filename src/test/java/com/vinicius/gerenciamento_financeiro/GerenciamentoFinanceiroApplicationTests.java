package com.vinicius.gerenciamento_financeiro;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtAuthenticationFilter;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtService;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.UsuarioMapper;
import com.vinicius.gerenciamento_financeiro.adapter.out.messaging.RabbitMQConsumer;
import com.vinicius.gerenciamento_financeiro.adapter.out.messaging.RabbitMQNotificador;
import com.vinicius.gerenciamento_financeiro.application.service.usuario.UsuarioServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GerenciamentoFinanceiroApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private JwtService jwtService;

	@MockitoBean
	private UsuarioServiceImpl usuarioService;

	@MockitoBean
	private UsuarioMapper usuarioMapper;

	@MockitoBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockitoBean
	private AuthenticationManager authenticationManager;

	@MockitoBean
	private RabbitTemplate rabbitTemplate;


	@MockitoBean
	private RabbitMQConsumer rabbitMQConsumer;

	@MockitoBean
	private RabbitMQNotificador rabbitMQNotificador;

	@Test
	void contextLoads() {
		assertNotNull(mockMvc);
	}
}