package org.lumen.app.data.model

import com.google.gson.annotations.SerializedName

data class JoinSessionResponse(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("sessionId") val sessionId: String = "",
    @SerializedName("pomodoro") val pomodoro: PomodoroState? = null,
    @SerializedName("error") val error: String? = null
)