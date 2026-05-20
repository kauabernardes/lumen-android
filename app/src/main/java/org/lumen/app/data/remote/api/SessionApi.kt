package org.lumen.app.data.remote.api

import org.lumen.app.data.model.User
import org.lumen.app.data.remote.model.ForceBreakRequest
import org.lumen.app.data.remote.model.ForceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface SessionApi {

    @POST("session/{sessionId}/toggle")
    suspend fun toggleTimer(
        @Path("sessionId") sessionId: String,
        @Header("Authorization") token: String) : Response<Unit>


    @POST("session/{sessionId}/break")
    suspend fun forceBreak(
        @Path("sessionId") sessionId: String,
        @Body request: ForceBreakRequest,
        @Header("Authorization") token: String) : Response<ForceResponse>

    @POST("session/{sessionId}/study")
    suspend fun forceStudy(
        @Path("sessionId") sessionId: String,
        @Header("Authorization") token: String) : Response<ForceResponse>

    @GET("session/{sessionId}/participants")
    suspend fun getParticipants(
        @Path("sessionId") sessionId: String,
        @Header("Authorization") token: String
    ): Response<List<User>>

}