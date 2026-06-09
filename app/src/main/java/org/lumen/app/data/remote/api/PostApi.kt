package org.lumen.app.data.remote.api


import org.lumen.app.data.model.post.Post
import org.lumen.app.data.remote.model.CreatePostRequest
import org.lumen.app.data.remote.model.LikeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET

import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PostApi {

    @POST("posts/{postId}/like")
    suspend fun like (
        @Header("Authorization") token: String,
        @Path("postId") postId: String,
    ) : Response<LikeResponse>

    @GET("posts/{postId}")
    suspend fun post (
        @Header("Authorization") token: String,
        @Path("postId") postId: String,
    ) : Response<Post>

    @POST("posts")
    suspend fun createPost (
        @Header("Authorization") token: String,
        @Body createPostRequest: CreatePostRequest,
    ) : Response<Post>
}