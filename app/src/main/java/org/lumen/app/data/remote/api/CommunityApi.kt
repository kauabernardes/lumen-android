package org.lumen.app.data.remote.api

import org.lumen.app.data.model.Community
import org.lumen.app.data.model.post.Post
import org.lumen.app.data.remote.model.CommunityInResponse
import org.lumen.app.data.remote.model.PostsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CommunityApi {

    @GET("community/in")
    suspend fun imIn (@Header("Authorization") token: String,
                      @Query("page") page: Int = 0,
                      @Query("limit") limit: Int = 5,
                      ) : Response<CommunityInResponse>


    @GET("community/{communityId}")
    suspend fun community (@Header("Authorization") token: String, @Path("communityId") communityId: String) : Response<Community>

    @GET("community/{communityId}/posts")
    suspend fun communityPosts (
        @Header("Authorization") token: String,
        @Path("communityId") communityId: String,
        @Query("page") page: Int = 0,
        @Query("limit") limit: Int = 5,
    ) : Response<PostsResponse>


}