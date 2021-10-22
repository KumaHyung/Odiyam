package com.soapclient.place.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.soapclient.place.data.api.KakaoService
import com.soapclient.place.data.datasources.*
import com.soapclient.place.data.firebase.datasources.*
import com.soapclient.place.data.local.PlaceHistoryLocalDataSourceImpl
import com.soapclient.place.data.local.dao.PlaceHistoryDao
import com.soapclient.place.data.remote.PlaceSearchRemoteDataSourceImpl
import com.soapclient.place.data.repository.*
import com.soapclient.place.domain.di.ApplicationScope
import com.soapclient.place.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DataModule {

    @Singleton
    @Provides
    fun providePlaceHistoryDataSource(
        placeHistoryDao: PlaceHistoryDao
    ): PlaceHistoryDataSource {
        return PlaceHistoryLocalDataSourceImpl(placeHistoryDao)
    }

    @Singleton
    @Provides
    fun providePlaceHistoryRepository(
        placeHistoryDataSource: PlaceHistoryDataSource
    ): PlaceHistoryRepository {
        return PlaceHistoryRepositoryImpl(placeHistoryDataSource)
    }

    @Singleton
    @Provides
    fun provideAuthenticationDataSource(
        @ApplicationScope applicationScope: CoroutineScope,
        firebaseAuth: FirebaseAuth
    ): AuthenticationDataSource {
        return FirebaseAuthenticationDataSourceImpl(applicationScope, firebaseAuth)
    }

    @Singleton
    @Provides
    fun provideAuthenticationRepository(
        authenticationDataSource: AuthenticationDataSource
    ): AuthenticationRepository {
        return AuthenticationRepositoryImpl(authenticationDataSource)
    }

    @Singleton
    @Provides
    fun provideGroupDataSource(
        firebaseDatabase: FirebaseDatabase
    ): GroupDataSource {
        return FirebaseGroupDataSourceImpl(firebaseDatabase)
    }

    @Singleton
    @Provides
    fun provideGroupDataRepository(
        groupDataSource: GroupDataSource
    ): GroupDataRepository {
        return GroupDataRepositoryImpl(groupDataSource)
    }

    @Singleton
    @Provides
    fun providePlaceSearchDataSource(
        kakaoService: KakaoService
    ): PlaceSearchDataSource {
        return PlaceSearchRemoteDataSourceImpl(kakaoService)
    }

    @Singleton
    @Provides
    fun providePlaceSearchRepository(
        placeSearchDataSource: PlaceSearchDataSource
    ): PlaceSearchRepository {
        return PlaceSearchRepositoryImpl(placeSearchDataSource)
    }

    @Singleton
    @Provides
    fun provideUserDataSource(
        firebaseDatabase: FirebaseDatabase
    ): UserDataSource {
        return FirebaseUserDataSourceImpl(firebaseDatabase)
    }

    @Singleton
    @Provides
    fun provideUserRepository(
        userDataSource: UserDataSource
    ): UserRepository {
        return UserRepositoryImpl(userDataSource)
    }

    @Singleton
    @Provides
    fun provideUserLocationDataSource(
        firebaseDatabase: FirebaseDatabase
    ): UserLocationDataSource {
        return FirebaseUserLocationDataSourceImpl(firebaseDatabase)
    }

    @Singleton
    @Provides
    fun provideUserLocationRepository(
        userLocationDataSource: UserLocationDataSource
    ): UserLocationRepository {
        return UserLocationRepositoryImpl(userLocationDataSource)
    }

    @Singleton
    @Provides
    fun providePhotoStorageDataSource(
        firebaseStorage: FirebaseStorage
    ): PhotoStorageDataSource {
        return FirebasePhotoStorageDataSourceImpl(firebaseStorage)
    }

    @Singleton
    @Provides
    fun providePhotoStorageRepository(
        photoStorageDataSource: PhotoStorageDataSource
    ): PhotoStorageRepository {
        return PhotoStorageRepositoryImpl(photoStorageDataSource)
    }
}