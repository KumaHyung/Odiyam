package com.soapclient.place.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class InternalFilePath

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ExternalFilePath