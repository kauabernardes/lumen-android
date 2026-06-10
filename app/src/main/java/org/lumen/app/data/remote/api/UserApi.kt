package org.lumen.app.data.remote.api


import org.lumen.app.data.model.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface UserApi {
    @GET("user/{userId}")
    suspend fun user (
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        ) : Response<User>
}