package org.antechrestos.springboot.tracingissue.api.web

import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationRegistry
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.slf4j.MDCContext
import org.antechrestos.springboot.tracingissue.domain.User
import org.antechrestos.springboot.tracingissue.domain.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.observability.micrometer.Micrometer
import reactor.core.publisher.Mono
import java.time.Duration

data class CreateUserDto(val firstname: String, val name: String)
data class UserDto(val id: String, val firstname: String, val name: String)

@RestController
class UserController(private val userRepository: UserRepository, private val observationRegistry: ObservationRegistry) {

    private val logger = LoggerFactory.getLogger(UserController::class.java)

    @PostMapping("/api/v1/users-without-context")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createUserNoContext(@RequestBody user: CreateUserDto) : UserDto {
        return launchUserCreation(user)
    }

    @PostMapping("/api/v1/users")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createUser(@RequestBody user: CreateUserDto) : UserDto {
        return launchUserCreation(user)
    }

    @PostMapping("/api/v1/users-with-mdc")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createUserMdc(@RequestBody user: CreateUserDto) : UserDto {
        return launchUserCreation(user)
    }

    private suspend fun launchUserCreation(user: CreateUserDto): UserDto {
        logger.debug("Start save of user {}", user)
        return userRepository.create(
            User.newUser(
                firstname = user.firstname,
                name = user.name,
            )
        )
            .let { toWeb(it) }
            .also {
                logger.debug("Wait for it")
                longTreatment(it)
                logger.debug("Created user {}", it)
            }
    }

    private suspend fun longTreatment(userDto: UserDto) {
        Mono.just(userDto)
            .flatMap { mono(MDCContext()) {
                logger.debug("Start long treatment")
                delay(Duration.ofSeconds(2).toMillis())
                logger.debug("End long treatment")
            } }
            .tap(
                Micrometer.observation(observationRegistry) { registry ->
                    val observation = Observation.createNotStarted("long-treatment", registry)
                        .contextualName("long-treatment")
                    observation
                }
            )
            .awaitSingle()
    }

    @GetMapping("/api/v1/users")
    suspend fun listUsers() = userRepository.list()
        .map { toWeb(it) }

    @GetMapping("/api/v1/users/{id}")
    suspend fun getUser(@PathVariable("id") id: String): UserDto {
        logger.debug("Loading user with id {}", id)
        return userRepository.get(id)
            ?.let { toWeb(it) }
            ?.also { logger.debug("Found user {}", it) }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @DeleteMapping("/api/v1/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun deleteUser(@PathVariable("id") id: String) {
        logger.debug("Removing user with id {}", id)
        userRepository.delete(id)
        logger.debug("User deleted {}", id)
    }

    private fun toWeb(user: User) = UserDto(
        id = user.id,
        firstname = user.firstname,
        name = user.name
    )

}