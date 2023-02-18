package com.github.ivpal.cdc.search.stream

import com.github.ivpal.cdc.search.UserService
import io.github.oshai.KLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Configuration
class UserChangesConsumerConfig(private val userService: UserService) {
    @Bean
    fun userChangesConsumer(): (Flux<Value<ValueUser>>) -> Unit = { flow ->
        flow
            .doOnNext { logger.info { it } }
            .flatMap { e ->
                val payload = e.payload
                when (payload.op) {
                    "c" -> userService.create(payload.after)
                    "u" -> userService.update(payload.after)
                    "d" -> userService.remove(payload.before?.id)
                    else -> Mono.error(IllegalStateException("Unexpected value for 'op'=${payload.op}"))
                }
            }
            .subscribe()
    }

    companion object : KLogging()
}
