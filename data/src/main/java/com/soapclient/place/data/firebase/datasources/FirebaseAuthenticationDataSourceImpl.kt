package com.soapclient.place.data.firebase.datasources

import com.google.firebase.auth.FirebaseAuth
import com.soapclient.place.data.datasources.AuthenticationDataSource
import com.soapclient.place.data.firebase.model.FirebaseUserInfo
import com.soapclient.place.domain.di.ApplicationScope
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.entity.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class FirebaseAuthenticationDataSourceImpl @Inject constructor(
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val firebaseAuth: FirebaseAuth
): AuthenticationDataSource {
    private val userInfo: SharedFlow<Result<UserInfo?>> =
        callbackFlow<FirebaseAuth> {
            val listener: ((FirebaseAuth) -> Unit) = { auth ->
                offer(auth)
            }
            firebaseAuth.addAuthStateListener(listener)
            awaitClose {
                firebaseAuth.removeAuthStateListener(listener)
            }
        }.map { authState ->
            Result.Success(FirebaseUserInfo(authState.currentUser))
        }.shareIn(
            scope = applicationScope,
            replay = 1,
            started = SharingStarted.WhileSubscribed()
        )

    override fun getCurrentUserInfo(): Flow<Result<UserInfo?>> = userInfo

    override suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Result<UserInfo> = suspendCancellableCoroutine { continuation ->
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                continuation.resume(Result.Success(FirebaseUserInfo(it.user)))
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
            .addOnCanceledListener {
                continuation.resumeWithException(Exception("Firebase Task was cancelled"))
            }
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        firebaseAuth.signInWithEmailAndPassword(email, password)
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
}