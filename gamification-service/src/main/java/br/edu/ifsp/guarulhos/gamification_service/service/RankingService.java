package br.edu.ifsp.guarulhos.gamification_service.service;

import br.edu.ifsp.guarulhos.gamification_service.dto.response.RankingItemResponse;
import br.edu.ifsp.guarulhos.gamification_service.model.enums.Escopo;
import br.edu.ifsp.guarulhos.gamification_service.model.enums.Periodo;
import br.edu.ifsp.guarulhos.gamification_service.model.enums.TipoEvento;
import br.edu.ifsp.guarulhos.gamification_service.repository.PontosEventoRepository;
import br.edu.ifsp.guarulhos.gamification_service.repository.RankingProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Monta o ranking de usuários (US-11) a partir da soma de pontos, aplicando os filtros de
 * escopo (geral/fórum/algoritmos) e período (semana/mês/total).
 */
@Service
@RequiredArgsConstructor
public class RankingService {

    private static final int TOP = 50;

    // Quais tipos de evento contam em cada escopo do ranking.
    private static final Set<TipoEvento> TIPOS_FORUM =
            Set.of(TipoEvento.TOPICO_CRIADO, TipoEvento.COMENTARIO, TipoEvento.LIKE_RECEBIDO);
    private static final Set<TipoEvento> TIPOS_ALGORITMOS =
            Set.of(TipoEvento.EXERCICIO_RESOLVIDO);

    private final PontosEventoRepository pontosEventoRepository;

    public List<RankingItemResponse> consultar(Escopo escopo, Periodo periodo){
        Set<TipoEvento> tipos = tiposDoEscopo(escopo);
        LocalDateTime desde = inicioDoPeriodo(periodo);
        Pageable top = PageRequest.of(0, TOP);

        List<RankingProjection> linhas = pontosEventoRepository.ranking(tipos, desde, top);

        AtomicLong posicao = new AtomicLong(1);
        return linhas.stream()
                .map(linha -> RankingItemResponse.builder()
                        .posicao(posicao.getAndIncrement())
                        .usuarioId(linha.getUsuarioId())
                        .pontuacao(linha.getTotal())
                        .build())
                .toList();
        // TODO US-11 CA3: anexar a posição do usuário autenticado quando ele estiver fora do top 50.
    }

    private Set<TipoEvento> tiposDoEscopo(Escopo escopo){
        return switch (escopo){
            case GERAL -> Set.of(TipoEvento.values());
            case FORUM -> TIPOS_FORUM;
            case ALGORITMOS -> TIPOS_ALGORITMOS;
        };
    }

    private LocalDateTime inicioDoPeriodo(Periodo periodo){
        return switch (periodo){
            case SEMANA -> LocalDateTime.now().minusWeeks(1);
            case MES -> LocalDateTime.now().minusMonths(1);
            case TOTAL -> LocalDateTime.of(1970, 1, 1, 0, 0);
        };
    }
}
