package com.soapclient.place.data.remote

import com.soapclient.place.data.api.KakaoService
import com.soapclient.place.data.datasources.PlaceSearchDataSource
import com.soapclient.place.data.remote.model.PlaceSearchResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceSearchRemoteDataSourceImpl @Inject constructor(
    private val kakaoService: KakaoService
): PlaceSearchDataSource {
    override suspend fun searchPlace(
        category_group_code: String,
        x: String,
        y: String,
        radius: Int,
        page: Int
    ): PlaceSearchResponse {
        return kakaoService.searchPlace(
            category_group_code = category_group_code,
            x = x,
            y = y,
            radius = radius,
            page = page,
            size = 15
        )
    }

    override suspend fun searchPlaceByKeyword(
        query: String,
        x: String,
        y: String,
        page: Int,
        size: Int
    ): PlaceSearchResponse {
        return kakaoService.searchLocationByKeyword(
            query = query,
            x = x,
            y = y,
            page = page,
            size = size
        )
    }
}