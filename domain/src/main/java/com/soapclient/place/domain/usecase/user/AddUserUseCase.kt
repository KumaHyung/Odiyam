package com.soapclient.place.domain.usecase.user

import com.soapclient.place.domain.UseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : UseCase<AddUserUseCase.Param, Result<Boolean>>(dispatcher) {
    override suspend fun execute(param: Param): Result<Boolean> {
        return userRepository.addUser(param.ownId, param.value)
    }

    data class Param(
        val ownId: String,
        val value: Any
    )
}