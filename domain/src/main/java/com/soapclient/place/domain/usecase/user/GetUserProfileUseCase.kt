package com.soapclient.place.domain.usecase.user

import com.soapclient.place.domain.FlowUseCase
import com.soapclient.place.domain.UseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.entity.UserProfile
import com.soapclient.place.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : FlowUseCase<String, UserProfile>(dispatcher) {
    override fun execute(param: String): Flow<Result<UserProfile>> {
        return userRepository.getUserProfile(param)
    }
}