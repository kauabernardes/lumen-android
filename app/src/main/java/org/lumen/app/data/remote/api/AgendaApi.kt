package org.lumen.app.data.remote.api

import org.lumen.app.data.model.Event
import org.lumen.app.data.remote.model.CreateDailyRequest
import org.lumen.app.data.remote.model.CreateEventRequest
import org.lumen.app.data.remote.model.MessageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AgendaApi {

    @GET("agenda/my")
    suspend fun my (
        @Header("Authorization") token: String,
    ): Response<List<Event>>

    @POST("agenda")
    suspend fun create( @Header("Authorization") token: String, @Body body: CreateEventRequest ) : Response<Event>
}