package com.soapclient.place.data.repository

import com.soapclient.place.data.datasources.UserLocationDataSource
import com.soapclient.place.domain.entity.UserLocation
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.repository.UserLocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserLocationRepositoryImpl @Inject constructor(
    private val userLocationDataSource: UserLocationDataSource
) : UserLocationRepository {
    override fun getUserLocation(id: String): Flow<Result<Pair<String, UserLocation>>> {
        return userLocationDataSource.getUserLocation(id)
    }

    override fun getUsersLocation(idList: List<String>): Flow<Result<Pair<String, UserLocation>>> {
        return userLocationDataSource.getUsersLocation(idList)
    }

    override suspend fun updateLocation(id: String, value: Any): Result<Boolean> {
        return userLocationDataSource.updateLocation(id, value)
    }
}