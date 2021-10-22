package com.soapclient.place.data.datasources

import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.entity.UserInfo
import kotlinx.coroutines.flow.Flow

interface AuthenticationDataSource {
    fun getCurrentUserInfo(): Flow<Result<UserInfo?>>

    suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<UserInfo>

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Boolean>
}