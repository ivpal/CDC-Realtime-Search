package com.github.ivpal.cdc.users.api

data class UserRequest(val username: String, val firstname: String, val lastname: String)
data class UserResponse(val id: Long?, val username: String, val firstname: String, val lastname: String)
