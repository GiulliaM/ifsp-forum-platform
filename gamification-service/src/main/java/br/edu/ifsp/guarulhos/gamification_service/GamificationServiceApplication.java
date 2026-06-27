package br.edu.ifsp.guarulhos.gamification_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada do gamification-service, responsável pela classificação e pontuação
 * (acúmulo de pontos por contribuição e ranking de usuários — US-11 e US-12).
 */
@SpringBootApplication
public class GamificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GamificationServiceApplication.class, args);
	}

}