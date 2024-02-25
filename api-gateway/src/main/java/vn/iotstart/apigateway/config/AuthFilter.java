package vn.iotstart.apigateway.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import vn.iotstart.apigateway.jwt.service.JwtService;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class AuthFilter implements GatewayFilter {

    private final JwtService jwtService;

    private Mono<Void> onError(final ServerWebExchange exchange, final String err, final String body) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();

        if (request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
            String jwt = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0).substring(7);
            try {
                if (jwtService.validateToken(jwt)) {
                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                            .header("Authorization", "Bearer " + jwt)
                            .build();

                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                }
                return onError(exchange, "You are not authorized to access this resource!", "You are not authorized to access this resource!");
            } catch (MalformedJwtException e) {
                String body = "{\"success\": false, \"message\": \"Unauthorized\", \"result\": \"Invalid JWT token. Please login again!\", \"statusCode\": \"401\"}";
                return onError(exchange, "Invalid JWT token. Please login again!", body);
            } catch (ExpiredJwtException e) {
                String body = "{\"success\": false, \"message\": \"Unauthorized\", \"result\": \"JWT token expired. Please login again!\", \"statusCode\": \"401\"}";
                return onError(exchange, "JWT token expired. Please login again!", body);
            } catch (SignatureException e) {
                String body = "{\"success\": false, \"message\": \"Unauthorized\", \"result\": \"JWT signature does not match locally computed signature. Please login again!\", \"statusCode\": \"401\"}";
                return onError(exchange, "JWT signature does not match locally computed signature. Please login again!", body);
            } catch (Exception e) {
                String body = "{\"success\": false, \"message\": \"Unauthorized\", \"result\": \"Please login again!\", \"statusCode\": \"401\"}";
                return onError(exchange, "Please login again!", body);
            }
        }
        return chain.filter(exchange);
    }
}
