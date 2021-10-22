package com.soapclient.place.domain.repository

import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.entity.UserInfo
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    fun getCurrentUserInfo(): Flow<Result<UserInfo?>>

    suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<UserInfo>

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Boolean>
}