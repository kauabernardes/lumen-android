package org.lumen.app.data.remote.model

import com.google.gson.annotations.SerializedName

data class LikeResponse(
    @SerializedName("liked")
    val liked: Boolean,
    @SerializedName("totalLikes")
    val totalLikes: Int,
)
