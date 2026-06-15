package org.lumen.app.data.remote

import org.lumen.app.data.remote.api.AgendaApi
import org.lumen.app.data.remote.api.AuthApi
import org.lumen.app.data.remote.api.CommunityApi
import org.lumen.app.data.remote.api.DailyApi
import org.lumen.app.data.remote.api.PostApi
import org.lumen.app.data.remote.api.RecommendationApi
import org.lumen.app.data.remote.api.RewardApi
import org.lumen.app.data.remote.api.SessionApi
import org.lumen.app.data.remote.api.UserApi
import org.lumen.app.data.remote.model.RecommendationResponse
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

    val postApi: PostApi by lazy {
        retrofit.create(PostApi::class.java)
    }

    val userApi : UserApi by lazy {
        retrofit.create(UserApi::class.java)

    }

    val dailyApi : DailyApi by lazy {
        retrofit.create(DailyApi::class.java)
    }

    val agendaApi: AgendaApi by lazy {
        retrofit.create(AgendaApi::class.java)
    }

    val rewardApi: RewardApi by lazy {
        retrofit.create(RewardApi::class.java)
    }

    val recommendationApi: RecommendationApi by lazy {
        retrofit.create(RecommendationApi::class.java)
    }

}