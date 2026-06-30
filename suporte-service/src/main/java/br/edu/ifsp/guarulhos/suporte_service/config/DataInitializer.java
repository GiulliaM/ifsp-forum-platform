package br.edu.ifsp.guarulhos.suporte_service.config;

import br.edu.ifsp.guarulhos.suporte_service.model.FaqEntrada;
import br.edu.ifsp.guarulhos.suporte_service.repository.FaqEntradaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final FaqEntradaRepository faqEntradaRepository;

    @Override
    public void run(ApplicationArguments args){
        List<FaqEntrada> faq = List.of(
                pergunta("Esqueci minha senha, como recupero o acesso?",
                        "Na tela de login, use a opção 'Esqueci minha senha' para receber as instruções de redefinição."),
                pergunta("Por que meu tópico não aparece na listagem do fórum?",
                        "Verifique se o título tem ao menos 10 caracteres e a descrição 30, e se uma categoria foi selecionada."),
                pergunta("Minha submissão de exercício não retorna resultado, o que fazer?",
                        "O processamento leva até 10 segundos; se persistir, abra um chamado informando o exercício e a linguagem usada."),
                pergunta("Como funciona a pontuação da gamificação?",
                        "Você ganha pontos ao criar tópicos, comentar, receber likes e resolver exercícios. Veja o histórico no seu perfil."),
                pergunta("Por que não recebo notificações de tópicos que sigo?",
                        "Confira se o seguimento ainda está ativo na seção 'Meus Tópicos' do seu perfil."),
                pergunta("Posso editar um comentário depois de publicado?",
                        "Sim, em até 30 minutos após a publicação."),
                pergunta("Como envio imagens em uma publicação do fórum?",
                        "É possível enviar PNG, JPG ou GIF de até 5 MB, ou incorporar por URL."),
                pergunta("Minhas preferências de aprendizado não estão impactando as sugestões",
                        "As mudanças nas preferências passam a valer a partir do próximo acesso à plataforma."),
                pergunta("Como sei se já resolvi um exercício antes?",
                        "O catálogo de exercícios exibe o status individual de cada exercício para o seu usuário."),
                pergunta("Quanto tempo leva para meu chamado de suporte ser respondido?",
                        "Chamados sem resposta há mais de 48 horas são automaticamente sinalizados como urgentes para os moderadores."),
                pergunta("Como acompanho o andamento do meu chamado?",
                        "Use o protocolo recebido na abertura para consultar o status em 'Meus Chamados'.")
        );

        for (FaqEntrada f : faq){
            if (faqEntradaRepository.findByPergunta(f.getPergunta()).isEmpty()){
                faqEntradaRepository.save(f);
            }
        }
    }

    private FaqEntrada pergunta(String pergunta, String resposta){
        return FaqEntrada.builder()
                .pergunta(pergunta)
                .resposta(resposta)
                .build();
    }
}
