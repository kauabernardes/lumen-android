package org.lumen.app.data.remote.api

import org.lumen.app.data.model.Community
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface CommunityApi {

    @GET("community/in")
    suspend fun imIn (@Header("Authorization") token: String) : Response<List<Community>>


}