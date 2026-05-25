package org.lumen.app.data.model.post

import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("id")
    val id: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("user")
    val user: PostUser,

    @SerializedName("isLiked")
    val isLiked: Boolean,

    @SerializedName("likesCount")
    val likesCount: Int,

    @SerializedName("commentsCount")
    val commentsCount: Int

)