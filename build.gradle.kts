plugins {
    kotlin("jvm") version "1.9.23"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:1.6.0")
    implementation("io.ktor:ktor-server-netty:1.6.0")
    implementation("io.ktor:ktor-jackson:1.6.0") // For JSON serialization
//    implementation("io.ktor:ktor-server-freemarker:1.6.0")
    implementation("org.jetbrains.exposed:exposed-core:0.34.1") // Exposed ORM
    implementation("org.jetbrains.exposed:exposed-dao:0.34.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.34.1")
    implementation("mysql:mysql-connector-java:8.0.33") // MySQL JDBC driver
    implementation("ch.qos.logback:logback-classic:1.2.3") // Logging
    implementation("io.github.cdimascio:dotenv-kotlin:6.3.1")
    testImplementation("io.ktor:ktor-server-tests:1.6.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.30")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.example.ApplicationKt"  // Replace with your actual main class
    }
}