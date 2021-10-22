package com.soapclient.place.domain.entity

data class PlaceSearchResult(
    val isLastData: Boolean,
    val placeList: List<Place>
)
