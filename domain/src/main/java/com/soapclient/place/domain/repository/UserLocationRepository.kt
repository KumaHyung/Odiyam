package com.soapclient.place.domain.repository

import com.soapclient.place.domain.entity.UserLocation
import com.soapclient.place.domain.entity.Result
import kotlinx.coroutines.flow.Flow

interface UserLocationRepository {

    fun getUserLocation(id: String): Flow<Result<Pair<String, UserLocation>>>

    fun getUsersLocation(idList: List<String>): Flow<Result<Pair<String, UserLocation>>>

    suspend fun updateLocation(id: String, value: Any): Result<Boolean>
}