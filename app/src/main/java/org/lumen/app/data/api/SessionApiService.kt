package org.lumen.app.data.api

import org.lumen.app.data.model.CommandResponse
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface SessionApiService {

    @POST("session/{sessionId}/toggle")
    suspend fun toggleTimer(
        @Path("sessionId") sessionId: String,
        @Header("Authorization") token: String
    ) : Response<CommandResponse>
}