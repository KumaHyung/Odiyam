package com.soapclient.place.data.repository

import android.util.Log
import com.soapclient.place.data.datasources.PlaceHistoryDataSource
import com.soapclient.place.data.mapper.Mapper
import com.soapclient.place.domain.entity.Place
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.repository.PlaceHistoryRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceHistoryRepositoryImpl @Inject constructor(
    private val placeHistoryLocalDataSource: PlaceHistoryDataSource,
    ) : PlaceHistoryRepository {
    override fun getAllPlaceHistory(): Flow<Result<List<Place>>> {
        return placeHistoryLocalDataSource.getAllPlaceHistory().map { list ->
            Result.Success(
                list.map { place ->
                    Mapper.mapperToPlaceEntity(place)
                }
            )
        }
    }

    override suspend fun getPlaceHistory(place_name: String): Place {
        return Mapper.mapperToPlaceEntity(placeHistoryLocalDataSource.getPlaceHistory(place_name))
    }

    override suspend fun isExistPlaceHistory(place_name: String): Boolean {
        return placeHistoryLocalDataSource.isExistPlaceHistory(place_name)
    }

    override suspend fun insertPlaceHistory(place: Place): Long {
        return placeHistoryLocalDataSource.insertPlaceHistory(Mapper.mapperToPlace(place))
    }

    override suspend fun deletePlaceHistory(place: Place) {
        placeHistoryLocalDataSource.deletePlaceHistory(Mapper.mapperToPlace(place))
    }
}