package org.lumen.app.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(

    @SerializedName("userId")
    val userId: String,

    @SerializedName("username")
    val username: String,
) : Parcelable
