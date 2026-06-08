package org.lumen.app.data.remote.model

import com.google.gson.annotations.SerializedName

data class Meta(
    @SerializedName("total")
    var total: Int,
    @SerializedName("page")
    val page: Int,
    @SerializedName("lastPage")
    val lastPage: Int,

)
