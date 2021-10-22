package com.soapclient.place.domain.usecase.group

import com.soapclient.place.domain.UseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.repository.GroupDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GrantLocationSharingUseCase @Inject constructor(
    private val groupDataRepository: GroupDataRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : UseCase<GrantLocationSharingUseCase.Param, Result<Boolean>>(dispatcher) {
    override suspend fun execute(param: Param): Result<Boolean> {
        return groupDataRepository.grantLocationSharing(param.ownId, param.childId)
    }

    data class Param(
        val ownId: String,
        val childId: String
    )
}