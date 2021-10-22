package com.soapclient.place.data.local.dao

import androidx.room.*
import com.soapclient.place.data.common.model.CommonPlace
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceHistoryDao {
    @Query("SELECT * FROM place ORDER BY date DESC")
    fun getAllPlaceHistory(): Flow<List<CommonPlace>>

    @Query("SELECT * FROM place WHERE place_name = :place_name")
    suspend fun getPlaceHistory(place_name: String): CommonPlace

    @Query("SELECT EXISTS(SELECT 1 FROM place WHERE place_name = :place_name LIMIT 1)")
    suspend fun isExistPlaceHistory(place_name: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaceHistory(place: CommonPlace): Long

    @Delete
    suspend fun deletePlaceHistory(place: CommonPlace)
}