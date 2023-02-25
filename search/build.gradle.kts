group = "com.github.ivpal.cdc.search"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("org.springframework.cloud:spring-cloud-starter-stream-kafka")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("io.github.oshai:kotlin-logging-jvm:${Constants.kotlinLoggingJvmVersion}")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Constants.coroutinesVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${Constants.coroutinesVersion}")

    testImplementation("org.springframework.cloud:spring-cloud-stream-test-binder")

    testImplementation("org.testcontainers:elasticsearch:${Constants.testcontainersVersion}")
    testImplementation("io.rest-assured:rest-assured:${Constants.restAssuredVersion}")
    implementation("org.awaitility:awaitility:${Constants.awaitilityVersion}")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${Constants.springCloudVersion}")
    }
}
