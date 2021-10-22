package com.soapclient.place.data.datasources

import android.net.Uri
import com.soapclient.place.domain.entity.Result
import java.net.URL

interface PhotoStorageDataSource {

    suspend fun getDownloadUrl(path: String): Result<URL>

    suspend fun uploadImage(id: String, uri: Uri): Result<Boolean>
}