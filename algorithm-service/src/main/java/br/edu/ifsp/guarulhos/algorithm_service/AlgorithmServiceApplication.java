package br.edu.ifsp.guarulhos.algorithm_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada do algorithm-service, responsável pela área de estudos de algoritmos
 * (catálogo, submissões e feedback).
 */
@SpringBootApplication
public class AlgorithmServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlgorithmServiceApplication.class, args);
	}

}
