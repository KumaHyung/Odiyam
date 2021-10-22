package com.soapclient.place.domain.repository

import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.entity.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun addUser(ownId: String, value: Any): Result<Boolean>

    suspend fun findUserIdByEmail(email: String): Result<String?>

    suspend fun getUserProfileWait(id: String): Result<UserProfile>

    fun getUserProfile(id: String): Flow<Result<UserProfile>>

    suspend fun updatePhotoUrl(ownId: String, url: String): Result<Boolean>
}