package org.antechrestos.springboot.tracingissue.api.web;

import org.antechrestos.springboot.tracingissue.domain.User;
import org.antechrestos.springboot.tracingissue.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;

    UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/api/v1/users")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserDto> createUser(@RequestBody CreateUserDto user) {
        logger.debug("Start save of user {}", user);
        return userRepository.create(
                        User.newUser(
                                user.firstname(),
                                user.name())
                ).map(this::toWeb)
                .doOnEach(userSignal -> logger.debug("Created {}", userSignal.get()));
    }

    @GetMapping("/api/v1/users")
    public Flux<UserDto> listUsers() {
        return userRepository.list()
                .map(this::toWeb);
    }

    @GetMapping("/api/v1/users/{id}")
    public Mono<UserDto> getUser(@PathVariable("id") String id) {
        return userRepository.get(id)
                .map(this::toWeb)
                .switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @DeleteMapping("/api/v1/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(@PathVariable("id") String id) {
        logger.debug("Removing user with id {}", id);
        return userRepository.delete(id)
                .doOnEach(userSignal -> logger.debug("Deleted {}", id));
    }


    private UserDto toWeb(User user) {
        return new UserDto(user.id(), user.firstname(), user.name());
    }

}