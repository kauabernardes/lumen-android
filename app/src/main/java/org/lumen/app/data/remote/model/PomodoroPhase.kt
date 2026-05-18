package org.lumen.app.data.remote.model


import com.google.gson.annotations.SerializedName

enum class PomodoroPhase {
    @SerializedName("study")
    STUDY,

    @SerializedName("break")
    BREAK
}