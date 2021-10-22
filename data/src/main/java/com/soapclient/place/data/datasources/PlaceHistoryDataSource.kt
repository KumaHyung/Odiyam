package com.soapclient.place.data.datasources

import com.soapclient.place.data.common.model.CommonPlace
import kotlinx.coroutines.flow.Flow

interface PlaceHistoryDataSource {
    fun getAllPlaceHistory(): Flow<List<CommonPlace>>

    suspend fun getPlaceHistory(place_name: String): CommonPlace

    suspend fun isExistPlaceHistory(place_name: String): Boolean

    suspend fun insertPlaceHistory(place: CommonPlace): Long

    suspend fun deletePlaceHistory(place: CommonPlace)
}