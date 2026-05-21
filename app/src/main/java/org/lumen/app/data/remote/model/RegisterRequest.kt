package org.lumen.app.data.remote.model

data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String
)
