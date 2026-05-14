package org.lumen.app.data.remote.api

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path

interface SessionApi {

    @POST("session/{sessionId}/toggle")
    suspend fun toggleTimer(@Path("sessionId") sessioId: String) : Response<Unit>


}