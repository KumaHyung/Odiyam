package com.soapclient.place.data.datasources

import com.soapclient.place.data.remote.model.PlaceSearchResponse

interface PlaceSearchDataSource {
    suspend fun searchPlace(
        category_group_code: String,
        x: String,
        y: String,
        radius: Int,
        page: Int
    ): PlaceSearchResponse

    suspend fun searchPlaceByKeyword(
        query: String,
        x: String,
        y: String,
        page: Int,
        size: Int
    ): PlaceSearchResponse
}