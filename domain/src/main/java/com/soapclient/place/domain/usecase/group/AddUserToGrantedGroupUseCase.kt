package com.soapclient.place.domain.usecase.group

import com.soapclient.place.domain.UseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.repository.GroupDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddUserToGrantedGroupUseCase @Inject constructor(
    private val groupDataRepository: GroupDataRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : UseCase<AddUserToGrantedGroupUseCase.Param, Result<Boolean>>(dispatcher) {
    override suspend fun execute(param: Param): Result<Boolean> {
        return groupDataRepository.addUserToGrantedGroup(param.senderId, param.receiverId, param.value)
    }

    data class Param(
        val senderId: String,
        val receiverId: String,
        val value: Any
    )
}