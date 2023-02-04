package com.github.ivpal.cdc.users.api

import com.fasterxml.jackson.databind.ObjectMapper
import net.datafaker.Faker
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.isA
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@WebAppConfiguration
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Testcontainers
class UsersControllerTest {
    @Autowired private lateinit var webApplicationContext: WebApplicationContext
    @Autowired private lateinit var mapper: ObjectMapper

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    }

    @Test
    fun testControllerMethods() {
        val faker = Faker()
        mockMvc.get("/api/users/1")
            .andExpect {
                status { isNotFound() }
            }

        val rq = UserRequest(
            username = faker.name().username(),
            firstname = faker.name().firstName(),
            lastname = faker.name().lastName()
        )

        val strRs = mockMvc.post("/api/users") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(rq)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id", isA<Int>(Int::class.java))
            jsonPath("$.username", `is`(rq.username))
            jsonPath("$.firstname", `is`(rq.firstname))
            jsonPath("$.lastname", `is`(rq.lastname))
        }.andReturn().response.contentAsString

        val rs = mapper.readValue(strRs, UserResponse::class.java)
        assertThat(rs.id).isNotNull
        assertThat(rs.username).isEqualTo(rq.username)
        assertThat(rs.firstname).isEqualTo(rq.firstname)
        assertThat(rs.lastname).isEqualTo(rq.lastname)

        val updateRq = UserRequest(
            username = faker.name().username(),
            firstname = faker.name().firstName(),
            lastname = faker.name().lastName()
        )

        mockMvc.put("/api/users/${rs.id}") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(updateRq)
        }.andExpect {
            status { isOk() }
            jsonPath("$.id", isA<Int>(Int::class.java))
            jsonPath("$.username", `is`(updateRq.username))
            jsonPath("$.firstname", `is`(updateRq.firstname))
            jsonPath("$.lastname", `is`(updateRq.lastname))
        }

        mockMvc.get("/api/users/${rs.id}")
            .andExpect {
                status { isOk() }
                jsonPath("$.id", `is`(rs.id?.toInt()))
                jsonPath("$.username", `is`(updateRq.username))
                jsonPath("$.firstname", `is`(updateRq.firstname))
                jsonPath("$.lastname", `is`(updateRq.lastname))
            }

        mockMvc.delete("/api/users/${rs.id}")
            .andExpect {
                status { isNoContent() }
            }

        mockMvc.get("/api/users/${rs.id}")
            .andExpect {
                status { isNotFound() }
            }
    }

    companion object {
        @Container
        private val postgres = PostgreSQLContainer("postgres:15-alpine")
            .withDatabaseName("users")
            .withUsername("users")
            .withPassword("users")

        private fun url(): String =
            "jdbc:postgresql://${postgres.host}:${postgres.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)}/${postgres.databaseName}"

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", Companion::url)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }
}
