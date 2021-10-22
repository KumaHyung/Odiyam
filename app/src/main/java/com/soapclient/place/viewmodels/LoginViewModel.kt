package com.soapclient.place.viewmodels

import androidx.lifecycle.*
import com.soapclient.place.R
import com.soapclient.place.data.Event
import com.soapclient.place.delegate.SignInViewModelDelegate
import com.soapclient.place.domain.entity.data
import com.soapclient.place.domain.usecase.auth.CreateUserWithEmailAndPasswordUseCase
import com.soapclient.place.domain.usecase.auth.SignInWithEmailAndPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
        private val signInViewModelDelegate: SignInViewModelDelegate,
        private val createUserWithEmailAndPasswordUseCase: CreateUserWithEmailAndPasswordUseCase,
        private val signInWithEmailAndPasswordUseCase: SignInWithEmailAndPasswordUseCase
) : ViewModel(),
    SignInViewModelDelegate by signInViewModelDelegate {
    private val _loginRequestEvent = MutableSharedFlow<Event>()
    val loginRequestEvent = _loginRequestEvent.asSharedFlow()

    private val _createUserRequestEvent = MutableSharedFlow<Event>()
    val createUserRequestEvent = _createUserRequestEvent.asSharedFlow()

    fun createUserWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            _createUserRequestEvent.emit(Event.Loading)
            val result = createUserWithEmailAndPasswordUseCase(
                CreateUserWithEmailAndPasswordUseCase.Param(
                    email,
                    password
                )
            )
            if (result.data?.data == true) {
                _createUserRequestEvent.emit(Event.Success(null))
            } else {
                _createUserRequestEvent.emit(Event.Fail(R.string.create_account_fail))
            }
        }
    }

    fun loginWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            _loginRequestEvent.emit(Event.Loading)
            val result = signInWithEmailAndPasswordUseCase(SignInWithEmailAndPasswordUseCase.Param(email, password))
            if (result.data?.data == true) {
                _loginRequestEvent.emit(Event.Success(null))
            } else {
                _loginRequestEvent.emit(Event.Fail(R.string.login_fail))
            }
        }
    }

    fun isNeedLogin() = isUserSignedIn.value
}
