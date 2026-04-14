package org.lumen.app.data.model

import com.google.gson.annotations.SerializedName

data class JoinSessionRequest (

    @SerializedName("token") val token: String,
    @SerializedName("sessionId") val sessionId: String? = null
)