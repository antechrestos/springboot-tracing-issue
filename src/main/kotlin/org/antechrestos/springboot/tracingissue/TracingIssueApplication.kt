package org.antechrestos.springboot.tracingissue

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.antechrestos.springboot.tracingissue.infrastructure.UserRepositoryMongoStore
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.info.GitProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

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
}

fun main(args: Array<String>) {
	runApplication<TracingIssueApplication>(*args)
}
