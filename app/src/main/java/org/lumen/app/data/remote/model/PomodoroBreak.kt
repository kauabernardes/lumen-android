package org.lumen.app.data.remote.model

import com.google.gson.annotations.SerializedName

enum class PomodoroBreak {

    @SerializedName("short")
    SHORT,
    @SerializedName("long")
    LONG
}