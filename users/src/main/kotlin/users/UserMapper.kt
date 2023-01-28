package users

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import users.api.UserRequest
import users.api.UserResponse
import users.persistence.User

@Mapper(componentModel = "spring")
interface UserMapper {
    fun mapToDto(user: User): UserResponse

    @Mapping(target = "id", ignore = true)
    fun mapToModel(dto: UserRequest): User
}
