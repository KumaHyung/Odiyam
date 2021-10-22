package com.soapclient.place.domain.usecase.group

import com.soapclient.place.domain.UseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.repository.GroupDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DenyLocationSharingUseCase @Inject constructor(
    private val groupDataRepository: GroupDataRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : UseCase<DenyLocationSharingUseCase.Param, Result<Boolean>>(dispatcher) {
    override suspend fun execute(param: Param): Result<Boolean> {
        return groupDataRepository.denyLocationSharing(param.ownId, param.childId)
    }

    data class Param(
        val ownId: String,
        val childId: String
    )
}