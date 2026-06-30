package br.edu.ifsp.guarulhos.gamification_service.config;

import br.edu.ifsp.guarulhos.gamification_service.model.Conquista;
import br.edu.ifsp.guarulhos.gamification_service.repository.ConquistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final ConquistaRepository conquistaRepository;

    @Override
    public void run(ApplicationArguments args){
        List<Conquista> badges = List.of(
                badge("PRIMEIRO_TOPICO",    "Primeiro Tópico",    "Você criou seu primeiro tópico de discussão!"),
                badge("PRIMEIRO_COMENTARIO","Primeiro Comentário","Você fez seu primeiro comentário em um tópico!"),
                badge("PRIMEIRO_LIKE",      "Popular",            "Seu conteúdo recebeu o primeiro like!"),
                badge("PRIMEIRO_EXERCICIO", "Primeiro Exercício", "Você resolveu seu primeiro exercício!"),
                badge("10_EXERCICIOS",      "Maratonista",        "Você resolveu 10 exercícios!")
        );

        for (Conquista b : badges){
            if (conquistaRepository.findByCriterio(b.getCriterio()).isEmpty()){
                conquistaRepository.save(b);
            }
        }
    }

    private Conquista badge(String criterio, String nome, String descricao){
        return Conquista.builder()
                .criterio(criterio)
                .nome(nome)
                .descricao(descricao)
                .build();
    }
}
