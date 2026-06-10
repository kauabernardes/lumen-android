package org.lumen.app.data.remote.model


data class CreatePostRequest(
    val content: String,
    val communityId: String,
)
