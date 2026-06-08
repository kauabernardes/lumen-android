package org.lumen.app.data.model.post

import com.google.gson.annotations.SerializedName

data class PostUser(
    @SerializedName("id")
    val id: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("profileImage")
    val profileImage: String
)
