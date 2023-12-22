package org.antechrestos.springboot.tracingissue.infrastructure;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

interface UserRepositoryMongoStore extends ReactiveMongoRepository<DBUser, String> {
}
