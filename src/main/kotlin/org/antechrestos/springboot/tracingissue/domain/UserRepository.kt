package org.antechrestos.springboot.tracingissue.domain

import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun create(user: User): User

    fun list(): Flow<User>

    suspend fun get(id: String): User?

    suspend fun delete(id: String)

}