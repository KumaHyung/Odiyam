package com.soapclient.place.data.datasources

import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.entity.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserDataSource {
    suspend fun addUser(ownId: String, value: Any): Result<Boolean>

    suspend fun findUserIdByEmail(email: String): Result<String?>

    suspend fun getUserProfileWait(id: String): Result<UserProfile>

    fun getUserProfile(id: String): Flow<Result<UserProfile>>

    suspend fun updatePhotoUrl(id: String, url: String): Result<Boolean>
}