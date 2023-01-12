package com.example.ldap.handler

import com.example.ldap.service.LdapQueryService
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Component
class EndpointHandler(
    private val ldapQueryService: LdapQueryService
) {
    fun getUserInfo(request: ServerRequest): Mono<ServerResponse> {
        val userId = request.pathVariable("userId")
        return Mono.justOrEmpty(ldapQueryService.queryLdap(userId))
            .flatMap { adUser -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(adUser) }
            .switchIfEmpty { ServerResponse.notFound().build() }
    }
}
