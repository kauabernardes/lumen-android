package org.lumen.app.data.remote.api

import org.lumen.app.data.remote.model.LoginRequest
import org.lumen.app.data.remote.model.LoginResponse
import org.lumen.app.data.remote.model.MessageResponse
import org.lumen.app.data.remote.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/login")
    suspend fun login (@Body request: LoginRequest) : Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest) : Response<MessageResponse>
}