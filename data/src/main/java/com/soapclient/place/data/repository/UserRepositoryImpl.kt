package com.soapclient.place.data.repository

import com.soapclient.place.data.datasources.UserDataSource
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.entity.UserProfile
import com.soapclient.place.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource
) : UserRepository {

    override suspend fun addUser(
        ownId: String,
        value: Any
    ): Result<Boolean> {
        return userDataSource.addUser(ownId, value)
    }

    override suspend fun findUserIdByEmail(email: String): Result<String?> {
        return userDataSource.findUserIdByEmail(email)
    }

    override suspend fun getUserProfileWait(id: String): Result<UserProfile> {
        return userDataSource.getUserProfileWait(id)
    }

    override fun getUserProfile(id: String): Flow<Result<UserProfile>> {
        return userDataSource.getUserProfile(id)
    }

    override suspend fun updatePhotoUrl(
        id: String,
        url: String
    ): Result<Boolean> {
        return userDataSource.updatePhotoUrl(id, url)
    }
}