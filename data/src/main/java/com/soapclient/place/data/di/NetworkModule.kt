package com.soapclient.place.data.di

import com.soapclient.place.data.api.KakaoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideKakaoService(): KakaoService {
        return KakaoService.create()
    }
}