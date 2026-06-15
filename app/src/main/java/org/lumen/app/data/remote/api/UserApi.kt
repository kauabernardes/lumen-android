package org.lumen.app.data.remote.api


import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.lumen.app.data.model.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.Path

interface UserApi {
    @GET("user/{userId}")
    suspend fun user (
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        ) : Response<User>



    @Multipart
    @PATCH("user/profile")
    suspend fun editProfile(
        @Header("Authorization") token: String,
        @Part("username") username: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part file: MultipartBody.Part?
    ): Response<User>
}