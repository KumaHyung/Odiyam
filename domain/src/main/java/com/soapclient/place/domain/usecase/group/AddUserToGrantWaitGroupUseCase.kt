package com.soapclient.place.domain.usecase.group

import com.soapclient.place.domain.UseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.repository.GroupDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddUserToGrantWaitGroupUseCase @Inject constructor(
    private val groupDataRepository: GroupDataRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : UseCase<AddUserToGrantWaitGroupUseCase.Param, Result<Boolean>>(dispatcher) {
    override suspend fun execute(param: Param): Result<Boolean> {
        return groupDataRepository.addUserToGrantWaitGroup(param.senderId, param.receiverId, param.value)
    }

    data class Param(
        val senderId: String,
        val receiverId: String,
        val value: Any
    )
}