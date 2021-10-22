package com.soapclient.place.data.firebase.model

import com.google.firebase.auth.FirebaseUser
import com.soapclient.place.domain.entity.UserInfo

class FirebaseUserInfo(
    private val firebaseUser: FirebaseUser?
) : UserInfo {

    override fun isSignedIn(): Boolean = firebaseUser != null

    override fun getUid(): String?  = firebaseUser?.uid

    override fun getEmail(): String? = firebaseUser?.email

    override fun getName(): String? = firebaseUser?.displayName
}