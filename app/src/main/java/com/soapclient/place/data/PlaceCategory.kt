package com.soapclient.place.data

import com.soapclient.place.R

enum class PlaceCategory(val code: String, val iconResId: Int, val stringResId: Int) {
    HOSPITAL("HP8", R.drawable.btn_hospital, R.string.category_hospital),
    PHARMACY("PM9", R.drawable.btn_pharmacy, R.string.category_pharmacy),
    GAS_STATION("OL7", R.drawable.btn_gas_station, R.string.category_gas_station),
    SUBWAY_STATION("SW8", R.drawable.btn_subway, R.string.category_subway_station),
    CAFE("CE7", R.drawable.btn_cafe, R.string.category_cafe),
    BANK("BK9", R.drawable.btn_bank, R.string.category_bank),
    PARKING_LOT("PK6", R.drawable.btn_parking, R.string.category_parking_lot),
    FOOD("FD6", R.drawable.btn_food, R.string.category_food),
    CONVENIENCE_STORE("CS2", R.drawable.btn_convenience_store, R.string.category_convenience_store);
}
