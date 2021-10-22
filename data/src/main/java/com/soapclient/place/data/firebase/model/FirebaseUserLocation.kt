package com.soapclient.place.data.firebase.model

data class FirebaseUserLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val battery_percent: Int = 999,
    val battery_charging: Boolean = false,
    val updateTime: Long = 0L)