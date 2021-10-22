package com.soapclient.place.data.mapper

import com.soapclient.place.data.firebase.model.FirebaseUserLocation
import com.soapclient.place.data.firebase.model.FirebaseUserProfile
import com.soapclient.place.data.common.model.CommonPlace
import com.soapclient.place.data.remote.model.PlaceSearchResponse
import com.soapclient.place.domain.entity.*

object Mapper {
    fun mapperToPlaceEntity(place: CommonPlace): Place {
        return Place(
            place_name = place.place_name,
            x = place.x,
            y = place.y,
            date = place.date,
            id = place.id,
            category_name = place.category_name,
            category_group_code = place.category_group_code,
            category_group_name = place.category_group_name,
            phone = place.phone,
            address_name = place.address_name,
            road_address_name = place.road_address_name,
            place_url = place.place_url,
            distance = place.distance
        )
    }

    fun mapperToPlace(place: Place): CommonPlace {
        return CommonPlace(
            place_name = place.place_name,
            x = place.x,
            y = place.y,
            date = place.date,
            id = place.id,
            category_name = place.category_name,
            category_group_code = place.category_group_code,
            category_group_name = place.category_group_name,
            phone = place.phone,
            address_name = place.address_name,
            road_address_name = place.road_address_name,
            place_url = place.place_url,
            distance = place.distance
        )
    }

    fun mapperToPlaceSearchResult(placeSearchResponse: PlaceSearchResponse): PlaceSearchResult {
        return PlaceSearchResult(
            isLastData = placeSearchResponse.meta.is_end,
            placeList = placeSearchResponse.placeList.map { mapperToPlaceEntity(it)}
        )
    }

    fun mapperToUserLocation(firebaseUserLocation: FirebaseUserLocation): UserLocation {
        return UserLocation(
            latitude = firebaseUserLocation.latitude,
            longitude = firebaseUserLocation.longitude,
            battery_charging = firebaseUserLocation.battery_charging,
            battery_percent = firebaseUserLocation.battery_percent,
            updateTime = firebaseUserLocation.updateTime
        )
    }

    fun mapperToUserProfile(firebaseUserProfile: FirebaseUserProfile): UserProfile {
        return UserProfile(
            name = firebaseUserProfile.name!!,
            email = firebaseUserProfile.email!!,
            photo_url = firebaseUserProfile.photo_url!!
        )

    }
}