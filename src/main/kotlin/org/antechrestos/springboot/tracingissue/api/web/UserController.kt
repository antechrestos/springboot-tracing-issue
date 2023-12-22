package org.antechrestos.springboot.tracingissue.api.web

import kotlinx.coroutines.flow.map
import org.antechrestos.springboot.tracingissue.domain.User
import org.antechrestos.springboot.tracingissue.domain.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

data class CreateUserDto(val firstname: String, val name: String)
data class UserDto(val id: String, val firstname: String, val name: String)

@RestController
class UserController(private val userRepository: UserRepository) {

    private val logger = LoggerFactory.getLogger(UserController::class.java)

    @PostMapping("/api/v1/users")
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createUser(@RequestBody user: CreateUserDto) : UserDto {
        logger.debug("Start save of user {}", user)
        return userRepository.create(
            User.newUser(
                firstname = user.firstname,
                name = user.name,
            )
        )
            .let { toWeb(it) }.
            also { logger.debug("Created user {}", it) }
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