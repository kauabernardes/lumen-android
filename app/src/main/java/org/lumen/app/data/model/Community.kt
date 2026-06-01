package org.lumen.app.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Community (
    @SerializedName("id")
    val id : String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("authorId")
    val authorId: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("membersCount")
    val membersCount: String,
    @SerializedName("isMember")
    val isMember: Boolean
) : Parcelable
