package org.antechrestos.springboot.tracingissue

import io.micrometer.core.instrument.kotlin.asContextElement
import io.micrometer.observation.ObservationRegistry
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.antechrestos.springboot.tracingissue.infrastructure.UserRepositoryMongoStore
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.info.GitProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange

@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = [UserRepositoryMongoStore::class])
class TracingIssueApplication {
    @Bean
    fun publicApi(
        buildProperties: BuildProperties?,
        gitProperties: GitProperties?,
    ): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Demo Tracing Issue")
                .version("latest-SNAPSHOT")
        )

    @Bean
    fun coroutineContextFilter(observationRegistry: ObservationRegistry): CoWebFilter = object : CoWebFilter() {
        override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
            when(exchange.request.uri.path) {
                "/api/v1/users-without-context" -> chain.filter(exchange)
                "/api/v1/users-with-mdc" -> withContext(MDCContext()){
                    chain.filter(exchange)
                }
                else -> withContext(observationRegistry.asContextElement()){
                    chain.filter(exchange)
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<TracingIssueApplication>(*args)
}
