package org.lumen.app.data.remote.model

data class LoginResponse(
    var access_token : String,
    var user : LoginUserResponse
)
