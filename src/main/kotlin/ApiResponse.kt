package org.example

data class ApiResponse<T>(
    val status: String,
    val message: String,
    val data: T? = null
)