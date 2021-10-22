package com.soapclient.place.data.repository

import com.soapclient.place.data.mapper.Mapper
import com.soapclient.place.data.datasources.PlaceSearchDataSource
import com.soapclient.place.domain.entity.PlaceSearchResult
import com.soapclient.place.domain.repository.PlaceSearchRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceSearchRepositoryImpl @Inject constructor(
    private val placeSearchRemoteDataSource: PlaceSearchDataSource
) : PlaceSearchRepository {

    override suspend fun searchPlaceByKeyword(
        keyword: String,
        x: String,
        y: String,
        page: Int,
        size: Int
    ): PlaceSearchResult {
        return runCatching {
            Mapper.mapperToPlaceSearchResult(placeSearchRemoteDataSource.searchPlaceByKeyword(
                query = keyword,
                x = x,
                y = y,
                page = page,
                size = size
            ))
        }.getOrDefault(PlaceSearchResult(true, listOf()))
    }

    override suspend fun searchPlace(
        category_group_code: String,
        x: String,
        y: String,
        radius: Int,
        page: Int
    ): PlaceSearchResult {
        return runCatching {
            Mapper.mapperToPlaceSearchResult(placeSearchRemoteDataSource.searchPlace(
                category_group_code = category_group_code,
                x = x,
                y = y,
                radius = radius,
                page = page
            ))
        }.getOrDefault(PlaceSearchResult(true, listOf()))
    }
}