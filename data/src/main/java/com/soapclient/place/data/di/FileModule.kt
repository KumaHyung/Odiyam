package com.soapclient.place.data.di

import android.content.Context
import android.content.SharedPreferences
import com.soapclient.place.data.local.EXTERNAL_DIRECTORY_RESULT
import com.soapclient.place.data.local.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class FileModule {

    @Singleton
    @Provides
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @InternalFilePath
    @Provides
    fun provideInternalFilePath(@ApplicationContext context: Context): String {
        return context.filesDir.absolutePath
    }

    @ExternalFilePath
    @Provides
    fun provideExternalFilePath(@ApplicationContext context: Context): String {
        return context.getExternalFilesDir(EXTERNAL_DIRECTORY_RESULT)!!.absolutePath
    }
}
