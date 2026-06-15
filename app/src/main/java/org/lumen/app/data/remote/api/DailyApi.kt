package org.lumen.app.data.remote.api

import org.lumen.app.data.remote.model.CreateDailyRequest
import org.lumen.app.data.remote.model.MessageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DailyApi {

    @POST("daily-log")
    suspend fun create (
        @Header("Authorization") token: String,
        @Body request: CreateDailyRequest
    ): Response<MessageResponse>

}