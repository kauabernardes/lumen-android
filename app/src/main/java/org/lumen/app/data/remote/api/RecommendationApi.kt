package org.lumen.app.data.remote.api

import org.lumen.app.data.remote.model.CreateDailyRequest
import org.lumen.app.data.remote.model.MessageResponse
import org.lumen.app.data.remote.model.RecommendationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface RecommendationApi {

    @GET("recommendation")
    suspend fun recommendation (
        @Header("Authorization") token: String,

    ): Response<RecommendationResponse>

}