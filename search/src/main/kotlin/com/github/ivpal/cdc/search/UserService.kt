package com.github.ivpal.cdc.search

import com.github.ivpal.cdc.search.persistence.User
import com.github.ivpal.cdc.search.persistence.UserRepository
import com.github.ivpal.cdc.search.stream.ValueUser
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class UserService(
    private val userRepository: UserRepository,
    private val mapper: UserMapper
) {
    fun create(value: ValueUser): Mono<User> = mapper.mapToModel(value).let { userRepository.save(it) }

    fun remove(id: Long?): Mono<Void> = id?.let { userRepository.deleteById(id) } ?: Mono.empty()

    fun update(value: ValueUser): Mono<User> =
        userRepository.findById(value.id)
            .map { user ->
                user.apply {
                    username = requireNotNull(value.username)
                    firstname = requireNotNull(value.firstname)
                    lastname = requireNotNull(value.lastname)
                }
            }
            .flatMap { userRepository.save(it) }

    fun search(query: String): Flux<User> = userRepository.search(query)
}
