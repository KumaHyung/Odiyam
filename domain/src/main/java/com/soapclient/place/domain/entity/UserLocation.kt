package com.soapclient.place.domain.entity

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val battery_percent: Int,
    val battery_charging: Boolean,
    val updateTime: Long)
