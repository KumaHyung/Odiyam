package com.soapclient.place.data.firebase.datasources

import com.google.firebase.database.*
import com.soapclient.place.data.datasources.UserDataSource
import com.soapclient.place.data.firebase.model.FirebaseUserProfile
import com.soapclient.place.data.mapper.Mapper
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.entity.UserProfile
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class FirebaseUserDataSourceImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
): UserDataSource {
    private val dataBaseRef: DatabaseReference by lazy {
        firebaseDatabase.getReference(REF_PATH)
    }

    override suspend fun addUser(
        ownId: String,
        value: Any
    ): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        dataBaseRef.child(ownId).setValue(value)
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

    override suspend fun findUserIdByEmail(email: String): Result<String?> = suspendCancellableCoroutine { continuation ->
        dataBaseRef.orderByChild(PATH_EMAIL).equalTo(email).limitToFirst(1).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.hasChildren()) {
                    for (child: DataSnapshot in snapshot.children) {
                        continuation.resume(Result.Success(child.key))
                    }
                } else {
                    continuation.resume(Result.Success(null))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.cancel(error.toException())
            }
        })
    }

//    override suspend fun getUserProfileWait(id: String): Result<UserProfile> = suspendCancellableCoroutine { continuation ->
//        dataBaseRef.child(id).addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                snapshot.getValue(FirebaseUserProfile::class.java)?.let { userProfile ->
//                    continuation.resume(Result.Success(Mapper.mapperToUserProfile(userProfile)))
//                } ?: run {
//                    continuation.resumeWithException(Exception("No data"))
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                continuation.resumeWithException(error.toException())
//            }
//        })
//    }

    override suspend fun getUserProfileWait(id: String): Result<UserProfile> {
        dataBaseRef.child(id).get().await().getValue(FirebaseUserProfile::class.java)?.let { userProfile ->
            return Result.Success(Mapper.mapperToUserProfile(userProfile))
        } ?: return Result.Error(Exception("Fail to get user profile"))
    }

    override fun getUserProfile(id: String): Flow<Result<UserProfile>> = callbackFlow {

        val listener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(FirebaseUserProfile::class.java)?.let { userProfile ->
                    offer(Result.Success(Mapper.mapperToUserProfile(userProfile)))
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        dataBaseRef.child(id).addValueEventListener(listener)

        awaitClose {
            dataBaseRef.child(id).removeEventListener(listener)
        }
    }

    override suspend fun updatePhotoUrl(
        id: String,
        url: String
    ): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val map = HashMap<String, Any>()
        map[PHOTO_URL] = "$id/$url"
        dataBaseRef.child(id).updateChildren(map)
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
        private const val REF_PATH = "user"
        private const val PATH_EMAIL = "email"
        private const val PHOTO_URL = "photo_url"
    }
}