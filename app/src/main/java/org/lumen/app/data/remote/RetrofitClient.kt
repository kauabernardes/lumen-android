package org.lumen.app.data.remote

import org.lumen.app.data.remote.api.AuthApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL : String = "http://10.0.2.2:3000/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }
}