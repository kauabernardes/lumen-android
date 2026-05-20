package org.lumen.app.data.remote.model


import com.google.gson.annotations.SerializedName

data class PomodoroState(
    @SerializedName("timeLeft")
    val timeLeft: Int,

    @SerializedName("status")
    val status: PomodoroStatus,

    @SerializedName("phase")
    val phase: PomodoroPhase,

    @SerializedName("cycle")
    val cycle: Int
)