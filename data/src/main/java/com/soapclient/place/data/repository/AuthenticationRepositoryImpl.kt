package com.soapclient.place.data.repository

import com.soapclient.place.data.datasources.AuthenticationDataSource
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.entity.UserInfo
import com.soapclient.place.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticationRepositoryImpl @Inject constructor(
    private val authenticationDataSource: AuthenticationDataSource
) : AuthenticationRepository {

    override fun getCurrentUserInfo(): Flow<Result<UserInfo?>> {
        return authenticationDataSource.getCurrentUserInfo()
    }

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<UserInfo> {
        return authenticationDataSource.createUserWithEmailAndPassword(email, password)
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<Boolean> {
        return authenticationDataSource.signInWithEmailAndPassword(email, password)
    }
}