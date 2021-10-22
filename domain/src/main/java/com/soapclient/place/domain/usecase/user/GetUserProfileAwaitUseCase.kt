package com.soapclient.place.domain.usecase.user

import com.soapclient.place.domain.UseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.entity.UserProfile
import com.soapclient.place.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetUserProfileAwaitUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : UseCase<String, Result<UserProfile>>(dispatcher) {
    override suspend fun execute(param: String): Result<UserProfile> {
        return userRepository.getUserProfileWait(param)
    }
}