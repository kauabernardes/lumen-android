package org.lumen.app.data.model.post

import com.google.gson.annotations.SerializedName
import org.lumen.app.data.model.Community

data class Post(
    @SerializedName("id")
    val id: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("createdAt")
    val createdAt: String?,

    @SerializedName("user")
    val user: PostUser,

    @SerializedName("isLiked")
    var isLiked: Boolean?,

    @SerializedName("likesCount")
    var likesCount: Int?,

    @SerializedName("commentsCount")
    val commentsCount: Int?,

    @SerializedName("parent")
    val parent : Post?,

    @SerializedName("parentId")
    val parentId: String?,

    @SerializedName("community")
    val community: Community?,

    @SerializedName("comments")
    val comments: MutableList<Post>?
)