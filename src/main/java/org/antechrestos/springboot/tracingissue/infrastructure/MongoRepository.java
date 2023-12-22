package org.antechrestos.springboot.tracingissue.infrastructure;

import org.antechrestos.springboot.tracingissue.domain.User;
import org.antechrestos.springboot.tracingissue.domain.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
class MongoRepository implements UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(MongoRepository.class);
    private final UserRepositoryMongoStore mongoStore;

    MongoRepository(UserRepositoryMongoStore mongoStore) {
        this.mongoStore = mongoStore;
    }


    @Override
    public Mono<User> create(User user) {
        return mongoStore.save(new DBUser(user.id(), user.firstname(), user.name()))
                .map(this::toDomain)
                .doOnEach(userSignal -> logger.debug("Saved {}", userSignal.get()));
    }

    @Override
    public Flux<User> list() {
        return mongoStore.findAll().map(this::toDomain);
    }

    @Override
    public Mono<User> get(String id) {
        return mongoStore.findById(id)
                .map(this::toDomain)
                .doOnEach(userSignal -> logger.debug("Saved {}", userSignal.get()));
    }

    @Override
    public Mono<Void> delete(String id) {
        return mongoStore.deleteById(id);
    }

    private User toDomain(DBUser dbUser) {
        return new User(dbUser.id(), dbUser.firstname(), dbUser.name());
    }
}