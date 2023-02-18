package com.github.ivpal.cdc.search.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

@Document(indexName = "users")
class User(
    @Id val id: Long,
    var username: String,
    var firstname: String,
    var lastname: String
)
