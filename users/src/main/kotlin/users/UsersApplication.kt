package users

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class UsersApplication

fun main(args: Array<String>) {
    SpringApplication.run(UsersApplication::class.java, *args)
}
