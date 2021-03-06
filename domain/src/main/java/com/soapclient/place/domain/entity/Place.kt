package com.soapclient.place.domain.entity

import java.util.*

data class Place(
    val place_name: String,
    val x: String,
    val y: String,
    var date: Date?,
    val id: String,
    val category_name: String,
    val category_group_code: String,
    val category_group_name: String,
    val phone: String,
    val address_name: String,
    val road_address_name: String,
    val place_url: String,
    val distance: String
)
