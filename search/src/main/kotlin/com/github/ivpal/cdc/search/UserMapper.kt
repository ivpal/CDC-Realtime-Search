package com.github.ivpal.cdc.search

import com.github.ivpal.cdc.search.persistence.User
import com.github.ivpal.cdc.search.stream.ValueUser
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface UserMapper {
    fun mapToModel(value: ValueUser): User
}
