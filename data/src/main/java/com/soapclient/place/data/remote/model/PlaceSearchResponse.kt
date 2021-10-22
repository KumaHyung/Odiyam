package com.soapclient.place.data.remote.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.soapclient.place.data.common.model.CommonPlace

data class PlaceSearchResponse(
    @Expose @SerializedName("meta") val meta: Meta,
    @Expose @SerializedName("documents") val placeList: List<CommonPlace>)