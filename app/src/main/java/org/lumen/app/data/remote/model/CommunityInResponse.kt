package org.lumen.app.data.remote.model

import com.google.gson.annotations.SerializedName
import org.lumen.app.data.model.Community

data class CommunityInResponse(
    @SerializedName("data")
    val data: List<Community>,
    val meta: Meta,
)
