package com.soapclient.place.domain.usecase.auth

import com.soapclient.place.domain.UseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.*
import com.soapclient.place.domain.repository.AuthenticationRepository
import com.soapclient.place.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class CreateUserWithEmailAndPasswordUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val userRepository: UserRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
): UseCase<CreateUserWithEmailAndPasswordUseCase.Param, Result<Boolean>>(dispatcher) {
    override suspend fun execute(param: Param): Result<Boolean> {
        val createResult = authenticationRepository.createUserWithEmailAndPassword(
            param.email,
            param.password
        )

        return if (createResult is Result.Success) {
            createResult.data.getUid()?.let { uid ->
                val addResult = userRepository.addUser(
                    uid,
                    UserProfile(
                        createResult.data.getName() ?: "",
                        createResult.data.getEmail() ?: "",
                        ""
                    )
                )
                if (addResult is Result.Success) {
                    Result.Success(true)
                } else {
                    Result.Error(addResult)
                }
            } ?: Result.Error("?_?")
        } else {
            Result.Error(createResult)
        }
    }

    data class Param(
        val email: String,
        val password: String,
    )
}