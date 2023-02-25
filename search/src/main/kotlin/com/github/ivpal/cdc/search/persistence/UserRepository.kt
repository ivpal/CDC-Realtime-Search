package com.github.ivpal.cdc.search.persistence

import org.springframework.data.elasticsearch.annotations.Query
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository
import reactor.core.publisher.Flux

interface UserRepository : ReactiveElasticsearchRepository<User, Long> {
    @Query("""{"query_string": { "query": "*?0*" } }""")
    fun search(query: String): Flux<User>
}
