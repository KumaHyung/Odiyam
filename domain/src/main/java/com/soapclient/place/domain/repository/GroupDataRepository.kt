package com.soapclient.place.domain.repository

import com.soapclient.place.domain.entity.Result
import kotlinx.coroutines.flow.Flow

interface GroupDataRepository {
    suspend fun addUserToGrantedGroup(senderId: String, receiverId: String, value: Any): Result<Boolean>

    suspend fun addUserToGrantWaitGroup(senderId: String, receiverId: String, value: Any): Result<Boolean>

    suspend fun grantLocationSharing(ownId: String, childId: String): Result<Boolean>

    suspend fun denyLocationSharing(ownId: String, childId: String): Result<Boolean>

    fun getGrantedUserIds(ownId: String): Flow<Result<List<String>>>

    fun getGrantWaitUserIds(ownId: String): Flow<Result<List<String>>>
}