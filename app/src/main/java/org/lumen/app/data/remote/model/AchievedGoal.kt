package org.lumen.app.data.remote.model

import com.google.gson.annotations.SerializedName

enum class AchievedGoal {
    @SerializedName("sim")
    SIM,
    @SerializedName("nao")
    NAO,
    @SerializedName("quase")
    QUASE
}