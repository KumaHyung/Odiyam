package com.soapclient.place.domain.entity

interface UserInfo {

    fun isSignedIn(): Boolean

    fun getUid(): String?

    fun getEmail(): String?

    fun getName(): String?
}