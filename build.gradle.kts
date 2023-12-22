plugins {
	id("org.springframework.boot") version "3.2.1"
	id("io.spring.dependency-management") version "1.1.4"
	java
}

group = "org.antechrestos.springboot"
version = "latest-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

extra["springdocVersion"] = "2.1.0"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	implementation("io.projectreactor:reactor-core-micrometer")


	implementation("io.micrometer:micrometer-tracing-bridge-otel")
	runtimeOnly("io.micrometer:micrometer-registry-otlp")
	runtimeOnly("io.opentelemetry:opentelemetry-exporter-otlp")

	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:${property("springdocVersion")}")
	implementation("org.springdoc:springdoc-openapi-starter-common:${property("springdocVersion")}")
	implementation("io.swagger:swagger-annotations:1.6.12") // required for open-api generated code

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
