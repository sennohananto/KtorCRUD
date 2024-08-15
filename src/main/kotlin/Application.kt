package org.example

import io.github.cdimascio.dotenv.dotenv
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop

data class User(val id: Int, val name: String, val email: String)

object Users : IntIdTable() {
    val name = varchar("name", 50)
    val email = varchar("email", 50).uniqueIndex()
}

fun main() {

    // Load .env file
    val dotenv = try {
        dotenv()
    } catch (e: Exception) {
        null
    }
    // Retrieve values
    val dbUrl = dotenv?.get("DB_URL") ?: System.getenv("DB_URL") ?: "defaultURL"
    val dbUser = dotenv?.get("DB_USERNAME") ?: System.getenv("DB_USERNAME") ?: "defaultUser"
    val dbPassword = dotenv?.get("DB_PASSWORD") ?: System.getenv("DB_PASSWORD") ?: "defaultPassword"

    // Initialize the database connection
    Database.connect(
        url = dbUrl,
        driver = "com.mysql.cj.jdbc.Driver",
        user = dbUser,
        password = dbPassword
    )

    // Create the table if it doesn't exist
    transaction {
        create(Users)
    }

    embeddedServer(Netty, port = 8080) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        jackson { }
    }

    install(StatusPages) {
        exception<Throwable> { cause ->
            call.respond(mapOf("error" to cause.localizedMessage))
        }
    }

    routing {
        route("/users") {

            // GET /users - Retrieve all users
            get {
                val users = transaction {
                    Users.selectAll().map {
                        User(it[Users.id].value, it[Users.name], it[Users.email])
                    }
                }
                call.respond(ApiResponse(status = "Success","List of all users", data = users))
            }

            // GET /users/{id} - Retrieve a user by ID
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(mapOf("error" to "Invalid user ID"))
                    return@get
                }

                val user = transaction {
                    Users.select { Users.id eq id }.singleOrNull()?.let {
                        User(it[Users.id].value, it[Users.name], it[Users.email])
                    }
                }

                if (user == null) {
                    call.respond(ApiResponse(status = "Failed","User ID not found", data = null))
                } else {
                    call.respond(ApiResponse(status = "Success","User data retrieved successfully", data = user))
                }
            }

            // POST /users - Create a new user
            post {
                val newUser = call.receive<User>()
                val userId = transaction {
                    Users.insertAndGetId {
                        it[name] = newUser.name
                        it[email] = newUser.email
                    }.value
                }
                call.respond(ApiResponse(status = "Success","User added successfully", data = newUser))
            }

            // PUT /users/{id} - Update an existing user
            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(ApiResponse(status = "Failed","Invalid User ID", data = null))
                    return@put
                }

                val updatedUser = call.receive<User>()
                transaction {
                    Users.update({ Users.id eq id }) {
                        it[name] = updatedUser.name
                        it[email] = updatedUser.email
                    }
                }

                call.respond(ApiResponse(status = "Success","User edited successfully", data = updatedUser))
            }

            // DELETE /users/{id} - Delete a user
            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {

                    call.respond(ApiResponse(status = "Failed","Invalid User ID", data = null))
                    return@delete
                }

                transaction {
                    Users.deleteWhere { Users.id eq id }
                }
                call.respond(ApiResponse(status = "Success","User with ID $id deleted successfully", data = null))
            }
        }
    }
}