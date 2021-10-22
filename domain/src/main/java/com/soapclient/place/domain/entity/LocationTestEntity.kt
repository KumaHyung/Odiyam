package com.soapclient.place.domain.entity

import java.util.*

data class LocationTestEntity(
    val generator: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val date: Date = Date())
