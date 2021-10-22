package com.soapclient.place.data.common.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "place", indices = [Index(value = ["place_name"], unique = true)])
data class CommonPlace(
    @PrimaryKey @ColumnInfo @Expose @SerializedName("place_name") val place_name: String,
    @ColumnInfo(name = "x") @Expose @SerializedName("x") val x: String,
    @ColumnInfo(name = "y") @Expose @SerializedName("y") val y: String,
    @ColumnInfo(name = "date") var date: Date?,
    @Expose @SerializedName("id") val id: String,
    @Expose @SerializedName("category_name") val category_name: String,
    @Expose @SerializedName("category_group_code") val category_group_code: String,
    @Expose @SerializedName("category_group_name") val category_group_name: String,
    @Expose @SerializedName("phone") val phone: String,
    @Expose @SerializedName("address_name") val address_name: String,
    @Expose @SerializedName("road_address_name") val road_address_name: String,
    @Expose @SerializedName("place_url") val place_url: String,
    @Expose @SerializedName("distance") val distance: String)
