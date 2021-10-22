package com.soapclient.place.data.firebase.datasources

import android.util.Log
import com.google.firebase.database.*
import com.soapclient.place.data.datasources.UserLocationDataSource
import com.soapclient.place.data.firebase.model.FirebaseUserLocation
import com.soapclient.place.data.mapper.Mapper
import com.soapclient.place.domain.entity.UserLocation
import com.soapclient.place.domain.entity.Result
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class FirebaseUserLocationDataSourceImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
): UserLocationDataSource {
    private val dataBaseRef: DatabaseReference by lazy {
        firebaseDatabase.getReference(REF_PATH)
    }

    override fun getUserLocation(id: String): Flow<Result<Pair<String, UserLocation>>> {
        return callbackFlow {
            val ref = dataBaseRef.child(id)
            val listener = object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(FirebaseUserLocation::class.java)?.let { location ->
                        offer(Result.Success(id to Mapper.mapperToUserLocation(location)))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    offer(Result.Error(error.message))
                    cancel()
                }
            }

            ref.addValueEventListener(listener)
            awaitClose {
                ref.removeEventListener(listener)
            }
        }
    }

    override fun getUsersLocation(idList: List<String>): Flow<Result<Pair<String, UserLocation>>> {
        return idList.asFlow().flatMapMerge {
            getUserLocation(it)
        }
    }

    override suspend fun updateLocation(id: String, value: Any): Result<Boolean> = suspendCancellableCoroutine { continuation ->

        dataBaseRef.child(id).setValue(value)
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
        private const val REF_PATH = "location"
    }
}