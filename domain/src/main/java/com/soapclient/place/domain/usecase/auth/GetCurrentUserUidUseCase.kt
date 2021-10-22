package com.soapclient.place.domain.usecase.auth

import com.soapclient.place.domain.FlowUseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.entity.UserInfo
import com.soapclient.place.domain.repository.AuthenticationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUidUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): FlowUseCase<Unit, UserInfo?>(dispatcher) {
    override fun execute(parameters: Unit): Flow<Result<UserInfo?>> {
        return authenticationRepository.getCurrentUserInfo()
    }
}