package com.vinicius.gerenciamento_financeiro;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableRabbit
public class GerenciamentoFinanceiroApplication {

	public static void main(String[] args) {
		SpringApplication.run(GerenciamentoFinanceiroApplication.class, args);
	}

}
