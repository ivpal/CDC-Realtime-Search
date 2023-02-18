package com.github.ivpal.cdc.search

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories

@SpringBootApplication
@EnableReactiveElasticsearchRepositories
class SearchApplication

fun main(args: Array<String>) {
    SpringApplication.run(SearchApplication::class.java, *args)
}
