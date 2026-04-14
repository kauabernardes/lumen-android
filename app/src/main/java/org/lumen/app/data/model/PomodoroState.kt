package org.lumen.app.data.model

import com.google.gson.annotations.SerializedName

data class PomodoroState(
    @SerializedName("timeLeft") val timeLeft: Int,
    @SerializedName("status") val status: String, // "running" ou "paused"
    @SerializedName("phase") val phase: String,   // "study" ou "break"
    @SerializedName("cycle") val cycle: Int
)