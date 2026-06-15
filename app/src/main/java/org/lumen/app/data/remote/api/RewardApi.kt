package org.lumen.app.data.remote.api

import org.lumen.app.data.model.Reward
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface RewardApi {

    @GET("rewards")
    suspend fun my (
        @Header("Authorization") token: String,

    ): Response<List<Reward>>

}