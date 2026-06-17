package br.edu.ifsp.guarulhos.api_gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value; // ✅ correto
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered{

        //rotas que não precisam de token
        private static final List<String> ROTAS_PUBLICAS = List.of(
                "/api/auth/registrar",
                "/api/auth/login"
        );

        @Value("${jwt.secret}")
        private String secret;

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain){
            String caminho = exchange.getRequest().getURI().getPath();

            if (ROTAS_PUBLICAS.stream().anyMatch(caminho::startsWith)){
                return chain.filter(exchange); // rota publica passa sem o token
            }

            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")){
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete(); // se rot nn for publica e nn tiver token, retornar 401
            }

            try {
                String token = authHeader.substring(7);//valida o token e extrai os dados
                Claims claims = Jwts.parser()
                        .verifyWith(Keys.hmacShaKeyFor(
                                secret.getBytes(StandardCharsets.UTF_8)
                        ))
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                ServerWebExchange exchangeModificado = exchange.mutate()
                        .request(r -> r
                                .header("X-User-Id", claims.getSubject()) //injeta id do usuario nos headers
                                .header("X-User-Role", claims.get("perfil", String.class)))//injeta perfil do usuario nos headers
                        .build();

                        return chain.filter(exchangeModificado);

            }catch (Exception e){
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }


            }
            @Override
            public int getOrder(){
                return -1;
        }
}
