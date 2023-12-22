package org.antechrestos.springboot.tracingissue.domain;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> create(User user);

    Flux<User> list();


    Mono<User> get(String id);

    Mono<Void> delete(String id);

}