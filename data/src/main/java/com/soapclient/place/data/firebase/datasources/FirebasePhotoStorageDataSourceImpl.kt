package com.soapclient.place.data.firebase.datasources

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.soapclient.place.data.datasources.PhotoStorageDataSource
import com.soapclient.place.domain.entity.Result
import kotlinx.coroutines.suspendCancellableCoroutine
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class FirebasePhotoStorageDataSourceImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage
): PhotoStorageDataSource {
    private val dataBaseRef: StorageReference by lazy {
        firebaseStorage.getReference(REF_PATH)
    }

    override suspend fun getDownloadUrl(path: String): Result<URL> = suspendCancellableCoroutine { continuation ->
        dataBaseRef.child(path).downloadUrl
            .addOnSuccessListener { uri ->
                continuation.resume(Result.Success(URL(uri.toString())))
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
            .addOnCanceledListener {
                continuation.resumeWithException(Exception("Firebase Task was cancelled"))
            }
    }

    override suspend fun uploadImage(
        id: String,
        uri: Uri
    ): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        dataBaseRef.child(id).child("${uri.lastPathSegment}").putFile(uri)
            .addOnSuccessListener {
                continuation.resume(Result.Success(true))
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
            .addOnCanceledListener {
                continuation.resumeWithException(Exception("Firebase Task was cancelled"))
            }
    }

    companion object {
        private const val REF_PATH = "photo"
    }
}