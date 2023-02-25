package com.github.ivpal.cdc.users

import com.github.ivpal.cdc.users.api.UserRequest
import com.github.ivpal.cdc.users.api.UserResponse
import com.github.ivpal.cdc.users.persistence.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface UserMapper {
    fun mapToDto(user: User): UserResponse

    @Mapping(target = "id", ignore = true)
    fun mapToModel(dto: UserRequest): User
}
