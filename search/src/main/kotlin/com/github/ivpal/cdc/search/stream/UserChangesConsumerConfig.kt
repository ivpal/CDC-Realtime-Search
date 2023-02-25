package com.github.ivpal.cdc.search.stream

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ivpal.cdc.search.UserService
import io.github.oshai.KLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.KafkaNull
import org.springframework.messaging.support.GenericMessage
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Configuration
class UserChangesConsumerConfig(private val userService: UserService, private val objectMapper: ObjectMapper) {
    @Bean
    fun userChangesConsumer(): (Flux<GenericMessage<Any>>) -> Unit = { flow ->
        flow
            .filter { it.payload !is KafkaNull } // delete operations produce additional null values
            .map { m ->
                val bytes = m.payload as ByteArray
                objectMapper.readValue(bytes, typeRef)
            }
            .doOnNext { logger.info { it } }
            .flatMap { e ->
                with(e.payload) {
                    when (e.payload.op) {
                        "c" -> userService.create(requireNotNull(after))
                        "u" -> userService.update(requireNotNull(after))
                        "d" -> userService.remove(before?.id)
                        else -> Mono.error(IllegalStateException("Unexpected value for 'op'=$op"))
                    }
                }
            }.subscribe()
    }

    companion object : KLogging() {
        private val typeRef: TypeReference<Value<ValueUser>> = object : TypeReference<Value<ValueUser>>() {}
    }
}
