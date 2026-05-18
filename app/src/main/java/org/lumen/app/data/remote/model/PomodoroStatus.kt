package org.lumen.app.data.remote.model

import com.google.gson.annotations.SerializedName

enum class PomodoroStatus {

    @SerializedName("paused")
    PAUSED,

    @SerializedName("running")
    RUNNING,

}