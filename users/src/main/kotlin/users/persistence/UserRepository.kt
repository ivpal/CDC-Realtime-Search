package users.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
//    override fun getReferenceById(id: Long): User?
}
