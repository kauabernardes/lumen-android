package org.lumen.app.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(

    @SerializedName("userId")
    val userId: String?,

    @SerializedName("id")
    val id: String?,


    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String?,

    @SerializedName("imgProfile")
    val imgProfile: String?,
) : Parcelable
