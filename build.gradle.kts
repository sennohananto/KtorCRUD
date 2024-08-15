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
    implementation("io.ktor:ktor-server-core:2.3.0")
    implementation("io.ktor:ktor-server-netty:2.3.0")
    implementation("io.ktor:ktor-server-status-pages:2.3.0")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.0")
    implementation("io.ktor:ktor-serialization-jackson:2.3.0")
    implementation("io.ktor:ktor-server-swagger:2.3.0")
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.0")
    testImplementation("io.ktor:ktor-server-tests:2.3.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.0")

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "org.example.ApplicationKt"  // Replace with your fully qualified main class name
    }
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

application {
    mainClass.set("org.example.ApplicationKt")  // Replace with your fully qualified main class name
}