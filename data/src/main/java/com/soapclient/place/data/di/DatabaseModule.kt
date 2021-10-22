package com.soapclient.place.data.di

import android.content.Context
import com.soapclient.place.data.database.LocalDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideLocalDatabase(@ApplicationContext context: Context) = LocalDatabase.getInstance(context)

    @Provides
    fun provideSearchHistoryDao(localDatabase: LocalDatabase) = localDatabase.searchHistoryDao()
}