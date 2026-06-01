package org.lumen.app.data.remote

import org.lumen.app.data.remote.api.AuthApi
import org.lumen.app.data.remote.api.CommunityApi
import org.lumen.app.data.remote.api.SessionApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val sessionApi: SessionApi by lazy {
        retrofit.create(SessionApi::class.java)
    }

    val communityApi: CommunityApi by lazy {
        retrofit.create(CommunityApi::class.java)
    }
}