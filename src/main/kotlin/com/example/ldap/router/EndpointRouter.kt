package com.example.ldap.router

import com.example.ldap.handler.EndpointHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class EndpointRouter {
    @Bean
    fun certRoutes(endpointHandler: EndpointHandler): RouterFunction<ServerResponse> {
        return RouterFunctions
            .route(RequestPredicates.GET("/api/query/{userId}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), endpointHandler::getUserInfo)
    }
}
