package org.lumen.app.data.model

import com.google.gson.annotations.SerializedName

data class CommandResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("status") val status: String,
    @SerializedName("timeLeft") val timeLeft: Int? = null
)