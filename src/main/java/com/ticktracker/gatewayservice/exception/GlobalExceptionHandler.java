package com.ticktracker.gatewayservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Order(-2) // Give priority to this exception handler instead of default handler
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {


        ServerHttpResponse response = exchange.getResponse();

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        HttpStatus status;
        String message;

        if (ex instanceof UnAuthorizeException) {
            status = HttpStatus.UNAUTHORIZED;
            message = ex.getMessage();
        } else {


            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message =   ex.getClass().getSimpleName() + ex.getMessage();
        }

        response.setStatusCode(status);
        try {
            // Create DTO object for structured error response
            ExceptionResponse errorResponse =
                    new ExceptionResponse(message, status);

            // Convert Java object -> JSON string
            String body = objectMapper.writeValueAsString(errorResponse);

            // Convert JSON string -> bytes -> DataBuffer
            DataBuffer buffer = response.bufferFactory()
                    .wrap(body.getBytes(StandardCharsets.UTF_8));

            // Write response asynchronously
            return response.writeWith(Mono.just(buffer));

        } catch (Exception e) {
            return response.setComplete();
        }
    }
}
