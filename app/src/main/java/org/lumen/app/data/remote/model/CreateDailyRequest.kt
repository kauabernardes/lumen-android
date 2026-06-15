package org.lumen.app.data.remote.model

data class CreateDailyRequest (
    val mood: String,
    val studiedYesterday: String,
    val achievedGoal: AchievedGoal,
    val studyToday: String,

)