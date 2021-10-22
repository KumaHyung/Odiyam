package com.soapclient.place.data.firebase.datasources

import android.util.Log
import com.google.firebase.database.*
import com.soapclient.place.data.datasources.GroupDataSource
import com.soapclient.place.data.firebase.model.FirebaseRequestInfo
import com.soapclient.place.domain.entity.Result
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class FirebaseGroupDataSourceImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
): GroupDataSource {
    private val dataBaseRef: DatabaseReference by lazy {
        firebaseDatabase.getReference(REF_PATH)
    }

    override suspend fun addUserToGrantedGroup(
        senderId: String,
        receiverId: String,
        value: Any
    ): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        dataBaseRef.child(senderId).child(CHILD_PATH_GRANTED).child(receiverId).setValue(value)
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

    override suspend fun addUserToGrantWaitGroup(
        senderId: String,
        receiverId: String,
        value: Any
    ): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        dataBaseRef.child(receiverId).child(CHILD_PATH_WAIT_GRANT).child(senderId).setValue(value)
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

    override suspend fun grantLocationSharing(
        ownId: String,
        childId: String
    ): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val data = FirebaseRequestInfo(System.currentTimeMillis())

        dataBaseRef.runTransaction(object: Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                currentData.child(ownId).child(CHILD_PATH_GRANTED).child(childId).value = data
                currentData.child(childId).child(CHILD_PATH_GRANTED).child(ownId).value = data
                currentData.child(ownId).child(CHILD_PATH_WAIT_GRANT).child(childId).value = null
                currentData.child(childId).child(CHILD_PATH_WAIT_GRANT).child(ownId).value = null
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                continuation.resume(Result.Success(true))
            }
        })
    }

    override suspend fun denyLocationSharing(
        ownId: String,
        childId: String
    ): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        dataBaseRef.child(ownId).child(CHILD_PATH_WAIT_GRANT).child(childId).removeValue()
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

    override fun getGrantedUserIds(ownId: String): Flow<Result<List<String>>> = callbackFlow {
        val ref = dataBaseRef.child(ownId).child(CHILD_PATH_GRANTED)
        val listener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<String>()
                snapshot.children.forEach {
                    it.key?.let { id ->
                        list.add(id)
                    }
                }
                offer(Result.Success(list))
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


    override fun getGrantWaitUserIds(ownId: String): Flow<Result<List<String>>> {
        return callbackFlow {
            val ref = dataBaseRef.child(ownId).child(CHILD_PATH_WAIT_GRANT)
            val listener = object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<String>()
                    snapshot.children.forEach {
                        it.key?.let { id ->
                            list.add(id)
                        }
                    }
                    offer(Result.Success(list))
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

    companion object {
        private const val REF_PATH = "group"
        private const val CHILD_PATH_GRANTED = "granted"
        private const val CHILD_PATH_WAIT_GRANT = "wait_grant"
    }
}