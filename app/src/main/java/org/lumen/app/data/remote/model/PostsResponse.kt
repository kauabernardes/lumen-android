package org.lumen.app.data.remote.model

import com.google.gson.annotations.SerializedName
import org.lumen.app.data.model.post.Post

data class PostsResponse(
    @SerializedName("data")
    val data: List<Post>,

    @SerializedName("meta")
    val meta: Meta
)
