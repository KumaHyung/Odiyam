package com.soapclient.place.domain.usecase.group

import com.soapclient.place.domain.FlowUseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.repository.GroupDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGrantedUserIdsUseCase @Inject constructor(
    private val groupDataRepository: GroupDataRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : FlowUseCase<String, List<String>>(dispatcher) {
    override fun execute(parameters: String): Flow<Result<List<String>>> {
        return groupDataRepository.getGrantedUserIds(parameters)
    }
}