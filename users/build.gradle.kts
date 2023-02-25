group = "com.github.ivpal.cdc.users"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.testcontainers:postgresql:${Constants.testcontainersVersion}")
    testImplementation("net.datafaker:datafaker:${Constants.datafakerVersion}")
}
