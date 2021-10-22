package com.soapclient.place.domain.repository

import com.soapclient.place.domain.entity.PlaceSearchResult

interface PlaceSearchRepository {
    suspend fun searchPlaceByKeyword(keyword: String, x: String, y: String, page: Int, size: Int): PlaceSearchResult

    suspend fun searchPlace(category_group_code: String, x: String, y: String, radius: Int, page: Int): PlaceSearchResult
}