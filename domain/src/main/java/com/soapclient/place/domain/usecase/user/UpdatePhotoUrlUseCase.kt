package com.soapclient.place.domain.usecase.user

import com.soapclient.place.domain.UseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UpdatePhotoUrlUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : UseCase<UpdatePhotoUrlUseCase.Param, Result<Boolean>>(dispatcher) {
    override suspend fun execute(param: Param): Result<Boolean> {
        return userRepository.updatePhotoUrl(param.id, param.url)
    }

    data class Param(
        val id: String,
        val url: String
    )
}