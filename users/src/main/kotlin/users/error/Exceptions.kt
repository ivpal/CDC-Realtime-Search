package users.error

open class NotFoundException(override val message: String) : RuntimeException(message)
class UserNotFoundException(id: Long) : NotFoundException("User with id=$id not found")
