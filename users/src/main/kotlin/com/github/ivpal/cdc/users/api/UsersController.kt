package com.github.ivpal.cdc.users.api

import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import com.github.ivpal.cdc.users.UserMapper
import com.github.ivpal.cdc.users.error.UserNotFoundException
import com.github.ivpal.cdc.users.persistence.UserRepository

@RestController
@RequestMapping("/api/users")
class UsersController(
    private val repository: UserRepository,
    private val mapper: UserMapper
) {
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): UserResponse {
        return repository.findByIdOrNull(id)
            ?.let { mapper.mapToDto(it) }
            ?: throw UserNotFoundException(id)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody rq: UserRequest): UserResponse {
        return mapper.mapToModel(rq)
            .let { repository.save(it) }
            .let { mapper.mapToDto(it) }
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody rq: UserRequest): UserResponse {
        return repository.findByIdOrNull(id)
            ?.apply {
                username = rq.username
                firstname = rq.firstname
                lastname = rq.lastname
            }
            ?.let { repository.save(it) }
            ?.let { mapper.mapToDto(it) }
            ?: throw UserNotFoundException(id)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        if (!repository.existsById(id)) {
            throw UserNotFoundException(id)
        }
        repository.deleteById(id)
    }
}
