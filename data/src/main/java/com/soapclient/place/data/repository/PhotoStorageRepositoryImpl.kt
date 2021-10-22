package com.soapclient.place.data.repository

import android.net.Uri
import com.soapclient.place.data.datasources.PhotoStorageDataSource
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.repository.PhotoStorageRepository
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoStorageRepositoryImpl @Inject constructor(
    private val photoStorageDataSource: PhotoStorageDataSource
) : PhotoStorageRepository {

    override suspend fun getDownloadUrl(path: String): Result<URL> {
        return photoStorageDataSource.getDownloadUrl(path)
    }

    override suspend fun uploadImage(id: String, uri: Uri): Result<Boolean> {
        return photoStorageDataSource.uploadImage(id, uri)
    }
}