package org.lumen.app.data.model

data class SessionMessage(
    val id: String,
    val userId: String,
    val username: String,
    val text: String,
    val title: String? = null,
    val subtitle: String? = null,
    val timestamp: String? = null,
    val isAi: Boolean
)