package com.soapclient.place.domain.usecase.auth

import com.soapclient.place.domain.UseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.repository.AuthenticationRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SignInWithEmailAndPasswordUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<SignInWithEmailAndPasswordUseCase.Param, Result<Boolean>>(dispatcher) {
    override suspend fun execute(param: Param): Result<Boolean> {
        return authenticationRepository.signInWithEmailAndPassword(
            param.email,
            param.password
        )
    }

    data class Param(
        val email: String,
        val password: String,
    )
}