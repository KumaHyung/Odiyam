package com.soapclient.place.di

import com.soapclient.place.delegate.SignInViewModelDelegate
import com.soapclient.place.delegate.SignInViewModelDelegateImpl
import com.soapclient.place.domain.di.ApplicationScope
import com.soapclient.place.domain.usecase.auth.GetCurrentUserUidUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DelegateModule {
    @Singleton
    @Provides
    fun provideSignInViewModelDelegate(
        getCurrentUserUidUseCase: GetCurrentUserUidUseCase,
        @ApplicationScope applicationScope: CoroutineScope
    ): SignInViewModelDelegate {
        return SignInViewModelDelegateImpl(
            getCurrentUserUidUseCase,
            applicationScope
        )
    }
}