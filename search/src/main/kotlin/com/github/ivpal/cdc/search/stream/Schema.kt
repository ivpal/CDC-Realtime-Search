package com.github.ivpal.cdc.search.stream

data class Value<T>(val payload: Payload<T>)

data class Payload<T>(
    val before: T?,
    val after: T,
    val op: String,
)

data class ValueUser(
    val id: Long,
    val username: String,
    val firstname: String,
    val lastname: String
)
