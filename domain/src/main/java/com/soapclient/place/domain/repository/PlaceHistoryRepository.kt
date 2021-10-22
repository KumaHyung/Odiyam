package com.soapclient.place.domain.repository

import com.soapclient.place.domain.entity.Place
import com.soapclient.place.domain.entity.Result
import kotlinx.coroutines.flow.Flow

interface PlaceHistoryRepository {
    fun getAllPlaceHistory(): Flow<Result<List<Place>>>

    suspend fun getPlaceHistory(place_name: String): Place

    suspend fun isExistPlaceHistory(place_name: String): Boolean

    suspend fun insertPlaceHistory(place: Place): Long

    suspend fun deletePlaceHistory(place: Place)
}