package com.ticktracker.gatewayservice.filters;

import com.ticktracker.gatewayservice.exception.UnAuthorizeException;
import com.ticktracker.gatewayservice.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;

@Configuration
public class LoggingFilter implements GlobalFilter {


//    List<String> allowedUrls = Arrays.asList(
//            "/auth"
//    );

    @Autowired
    private JwtUtil jwtUtil;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if(exchange.getRequest().getPath() .equals("/auth/login"))
        {
            return chain.filter(exchange);
        }


        ServerHttpRequest request = exchange.getRequest();
        //If Bearer Token present in Authorization header , extract it and validate
        if(request.getHeaders().containsKey("Authorization") &&
                request.getHeaders().getFirst("Authorization").startsWith("Bearer "))
        {

            String token = request.getHeaders().getFirst("Authorization").substring(7);
            System.out.println("X-USER-ROLE"+jwtUtil.parseRole(token));
           if(jwtUtil.validateToken(token))
           {
               System.out.println("X-USER-ROLE"+jwtUtil.parseRole(token));
               ServerHttpRequest newRequest = request.mutate().header("X-USER-ID",jwtUtil.parseUserId(token).toString())
                       .header("X-USER-ROLE",jwtUtil.parseRole(token))
                       .header("X-USER-EMAIL",jwtUtil.parseSubject(token)).build();
               return chain.filter(exchange.mutate().request(newRequest).build());

           }

           //ValidateToken() returns false means invalid token
            return Mono.error(new UnAuthorizeException("Unauthorize"));
        }

        return Mono.error(new UnAuthorizeException("No Token "));
    }
}
