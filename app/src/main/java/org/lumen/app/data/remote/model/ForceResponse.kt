package org.lumen.app.data.remote.model

data class ForceResponse(
    var success: Boolean,
    var status: PomodoroStatus,
    var timeLeft: Int,
)
