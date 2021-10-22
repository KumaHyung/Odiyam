package com.soapclient.place.data.repository

import com.soapclient.place.data.datasources.GroupDataSource
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.repository.GroupDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupDataRepositoryImpl @Inject constructor(
    private val groupDataSource: GroupDataSource
) : GroupDataRepository {

    override suspend fun addUserToGrantedGroup(
        senderId: String,
        receiverId: String,
        value: Any
    ): Result<Boolean> {
        return groupDataSource.addUserToGrantedGroup(senderId, receiverId, value)
    }

    override suspend fun addUserToGrantWaitGroup(
        senderId: String,
        receiverId: String,
        value: Any
    ): Result<Boolean> {
        return groupDataSource.addUserToGrantWaitGroup(senderId, receiverId, value)
    }

    override suspend fun grantLocationSharing(
        ownId: String,
        childId: String
    ): Result<Boolean> {
        return groupDataSource.grantLocationSharing(ownId, childId)
    }

    override suspend fun denyLocationSharing(
        ownId: String,
        childId: String
    ): Result<Boolean> {
        return groupDataSource.denyLocationSharing(ownId, childId)
    }

    override fun getGrantedUserIds(ownId: String): Flow<Result<List<String>>> {
        return groupDataSource.getGrantedUserIds(ownId)
    }

    override fun getGrantWaitUserIds(ownId: String): Flow<Result<List<String>>> {
        return groupDataSource.getGrantWaitUserIds(ownId)
    }
}