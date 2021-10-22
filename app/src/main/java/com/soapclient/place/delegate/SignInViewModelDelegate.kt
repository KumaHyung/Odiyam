package com.soapclient.place.delegate

import com.soapclient.place.domain.di.ApplicationScope
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.entity.UserInfo
import com.soapclient.place.domain.usecase.auth.GetCurrentUserUidUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface SignInViewModelDelegate {
    val userInfo: StateFlow<UserInfo?>

    val isUserSignedIn: StateFlow<Boolean>
}

internal class SignInViewModelDelegateImpl @Inject constructor(
    getCurrentUserUidUseCase: GetCurrentUserUidUseCase,
    @ApplicationScope val applicationScope: CoroutineScope
) : SignInViewModelDelegate {
    private val currentUser: Flow<Result<UserInfo?>> = getCurrentUserUidUseCase(Unit)

    override val userInfo: StateFlow<UserInfo?> = currentUser.map {
        (it as? Result.Success)?.data
    }.stateIn(applicationScope, SharingStarted.WhileSubscribed(), null)

    override val isUserSignedIn: StateFlow<Boolean> = userInfo.map {
        it?.isSignedIn() ?: false
    }.stateIn(applicationScope, SharingStarted.WhileSubscribed(), false)
}