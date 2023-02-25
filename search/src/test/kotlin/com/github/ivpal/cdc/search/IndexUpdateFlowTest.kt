package com.github.ivpal.cdc.search

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ivpal.cdc.search.persistence.User
import com.github.ivpal.cdc.search.stream.Payload
import com.github.ivpal.cdc.search.stream.Value
import com.github.ivpal.cdc.search.stream.ValueUser
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import org.awaitility.Awaitility.await
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.context.annotation.Import
import org.springframework.messaging.support.GenericMessage
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.concurrent.TimeUnit
import org.springframework.beans.factory.annotation.Value as ValueProperty

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import(TestChannelBinderConfiguration::class)
class IndexUpdateFlowTest {
    @Autowired private lateinit var input: InputDestination

    @Autowired private lateinit var objectMapper: ObjectMapper

    @LocalServerPort private lateinit var port: String

    @ValueProperty("\${spring.cloud.stream.bindings.userChangesConsumer-in-0.destination}")
    private lateinit var topic: String

    @Test
    fun testOperations() {
        RestAssured.port = port.toInt()

        given()
            .`when`()
            .get("/api/search?q=Jo")
            .then()
            .assertThat()
            .statusCode(200)
            .body("", hasSize<User>(0))

        var user = ValueUser(
            id = 1,
            username = "username",
            firstname = "John",
            lastname = "Doe"
        )
        sendCreate(user)

        await().atMost(2, TimeUnit.SECONDS).until {
            given()
                .`when`()
                .get("/api/search?q=Jo")
                .jsonPath()
                .getList<User>("")
                .size == 1
        }

        given()
            .`when`()
            .get("/api/search?q=Jo")
            .then()
            .body("get(0).id", `is`(user.id.toInt()))
            .body("get(0).username", `is`(user.username))
            .body("get(0).firstname", `is`(user.firstname))
            .body("get(0).lastname", `is`(user.lastname))

        user = ValueUser(
            id = 1,
            username = "new_username",
            firstname = "Sam",
            lastname = "Winston"
        )
        sendUpdate(user)

        await().atMost(2, TimeUnit.SECONDS).until {
            given()
                .`when`()
                .get("/api/search?q=sa")
                .jsonPath()
                .getList<User>("")
                .size == 1
        }

        given()
            .`when`()
            .get("/api/search?q=sa")
            .then()
            .body("get(0).id", `is`(user.id.toInt()))
            .body("get(0).username", `is`(user.username))
            .body("get(0).firstname", `is`(user.firstname))
            .body("get(0).lastname", `is`(user.lastname))

        sendDelete(user.id)

        await().atMost(2, TimeUnit.SECONDS).until {
            given()
                .`when`()
                .get("/api/search?q=sa")
                .jsonPath()
                .getList<User>("")
                .size == 0
        }
    }

    private fun sendCreate(user: ValueUser) {
        Value(payload = Payload(before = null, after = user, op = "c"))
            .also { send(it) }
    }

    private fun sendUpdate(user: ValueUser) {
        Value(payload = Payload(before = null, after = user, op = "u"))
            .also { send(it) }
    }

    private fun sendDelete(id: Long) {
        Value(payload = Payload(before = ValueUser(id = id), after = null, op = "d"))
            .also { send(it) }
    }

    private fun send(data: Value<ValueUser>) {
        objectMapper.writeValueAsBytes(data)
            .also { input.send(GenericMessage(it), topic) }
    }

    companion object {
        @Container
        private val elastic = ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.6.1")
            .withEnv("discovery.type", "single-node")
            .withEnv("http.host", "0.0.0.0")
            .withEnv("transport.host", "0.0.0.0")
            .withEnv("xpack.security.enabled", "false")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.elasticsearch.client.reactive.endpoints", elastic::getHttpHostAddress)
        }
    }
}
