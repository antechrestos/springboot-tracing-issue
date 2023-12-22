package org.antechrestos.springboot.tracingissue.infrastructure

import org.springframework.data.annotation.Id
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

data class DBUser(
    @Id val id: String,
    val firstname: String,
    val name: String
)

interface UserRepositoryMongoStore : CoroutineCrudRepository<DBUser, String>
