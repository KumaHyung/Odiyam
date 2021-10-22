package com.soapclient.place.data.datasources

import com.soapclient.place.domain.entity.UserLocation
import com.soapclient.place.domain.entity.Result
import kotlinx.coroutines.flow.Flow


interface UserLocationDataSource {

    fun getUserLocation(id: String): Flow<Result<Pair<String, UserLocation>>>

    fun getUsersLocation(idList: List<String>): Flow<Result<Pair<String, UserLocation>>>

    suspend fun updateLocation(id: String, value: Any): Result<Boolean>
}