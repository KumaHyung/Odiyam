package com.soapclient.place.domain.repository

import android.net.Uri
import com.soapclient.place.domain.entity.Result
import java.net.URL

interface PhotoStorageRepository {
    suspend fun getDownloadUrl(path: String): Result<URL>

    suspend fun uploadImage(id: String, uri: Uri): Result<Boolean>
}