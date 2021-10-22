package com.soapclient.place.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Meta(
    @Expose @SerializedName("total_count") val total_count: Int,
    @Expose @SerializedName("pageable_count") val pageable_count: Int,
    @Expose @SerializedName("is_end") val is_end: Boolean)