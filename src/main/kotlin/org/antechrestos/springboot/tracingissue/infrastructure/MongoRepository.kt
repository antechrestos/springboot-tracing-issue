package org.antechrestos.springboot.tracingissue.infrastructure

import kotlinx.coroutines.flow.map
import org.antechrestos.springboot.tracingissue.domain.User
import org.antechrestos.springboot.tracingissue.domain.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
class MongoRepository(private val mongoStore: UserRepositoryMongoStore) : UserRepository {


    private val logger = LoggerFactory.getLogger(this.javaClass)

    override suspend fun create(user: User) =
        mongoStore.save(
            DBUser(
                id = user.id,
                firstname = user.firstname,
                name = user.name
            )
        )
            .let { toDomain(it) }
            .also { logger.debug("user created {}", it) }

    override fun list() = mongoStore.findAll().map { toDomain(it) }


    override suspend fun get(id: String) = mongoStore.findById(id)
        ?.let { toDomain(it) }
        ?.also {
            logger.debug("user created {}", it)
        }

    override suspend fun delete(id: String) {
        mongoStore.deleteById(id)
        logger.debug("user deleted {}", id)
    }

    private fun toDomain(it: DBUser) = User(
        id = it.id,
        firstname = it.firstname,
        name = it.name
    )
}