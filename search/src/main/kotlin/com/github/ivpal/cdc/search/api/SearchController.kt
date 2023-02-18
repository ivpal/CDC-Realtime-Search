package com.github.ivpal.cdc.search.api

import com.github.ivpal.cdc.search.UserService
import com.github.ivpal.cdc.search.persistence.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/api/search")
class SearchController(private val userService: UserService) {
    @GetMapping
    fun search(@RequestParam q: String): Flux<User> = userService.search(q)
}
