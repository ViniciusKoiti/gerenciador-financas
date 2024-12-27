package com.vinicius.gerenciamento_financeiro;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtAuthenticationFilter;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtService;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.UsuarioMapper;
import com.vinicius.gerenciamento_financeiro.domain.service.usuario.UsuarioServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GerenciamentoFinanceiroApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private UsuarioServiceImpl usuarioService;

	@MockBean
	private UsuarioMapper usuarioMapper;

	@MockBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@MockBean
	private AuthenticationManager authenticationManager;

	@Test
	void contextLoads() {
		assertNotNull(mockMvc);
	}
}