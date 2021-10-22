package com.soapclient.place.domain.usecase.user

import com.soapclient.place.domain.UseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class FindUserIdByEmailUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : UseCase<String, Result<String?>>(dispatcher) {
    override suspend fun execute(param: String): Result<String?> {
        return userRepository.findUserIdByEmail(param)
    }
}