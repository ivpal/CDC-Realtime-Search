package com.github.ivpal.cdc.users.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>
