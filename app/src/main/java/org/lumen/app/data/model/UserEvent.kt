package org.lumen.app.data.model

import com.google.gson.annotations.SerializedName

data class UserEvent(
    @SerializedName("userId") val userId: String,
    @SerializedName("username") val username: String? = null // username só vem no user_joined
)