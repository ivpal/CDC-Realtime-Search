package com.github.ivpal.cdc.search

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchConfiguration

@Configuration
class ElasticsearchClientConfig : ReactiveElasticsearchConfiguration() {
    @Value("\${spring.data.elasticsearch.client.reactive.endpoints}")
    private lateinit var hostAndPort: String

    override fun clientConfiguration(): ClientConfiguration =
        ClientConfiguration.builder()
            .connectedTo(hostAndPort)
            .build()
}
