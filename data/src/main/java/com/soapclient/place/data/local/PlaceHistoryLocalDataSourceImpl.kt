package com.soapclient.place.data.local

import com.soapclient.place.data.datasources.PlaceHistoryDataSource
import com.soapclient.place.data.local.dao.PlaceHistoryDao
import com.soapclient.place.data.common.model.CommonPlace
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceHistoryLocalDataSourceImpl @Inject constructor(
    private val placeHistoryDao: PlaceHistoryDao
): PlaceHistoryDataSource {
    override fun getAllPlaceHistory(): Flow<List<CommonPlace>> = placeHistoryDao.getAllPlaceHistory().distinctUntilChanged()

    override suspend fun getPlaceHistory(place_name: String): CommonPlace = placeHistoryDao.getPlaceHistory(place_name)

    override suspend fun isExistPlaceHistory(place_name: String): Boolean = placeHistoryDao.isExistPlaceHistory(place_name)

    override suspend fun insertPlaceHistory(place: CommonPlace): Long = placeHistoryDao.insertPlaceHistory(place)

    override suspend fun deletePlaceHistory(place: CommonPlace) = placeHistoryDao.deletePlaceHistory(place)
}