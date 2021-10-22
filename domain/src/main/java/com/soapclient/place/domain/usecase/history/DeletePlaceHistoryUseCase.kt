package com.soapclient.place.domain.usecase.history

import com.soapclient.place.domain.UseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.Place
import com.soapclient.place.domain.repository.PlaceHistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DeletePlaceHistoryUseCase @Inject constructor(
    private val placeHistoryRepository: PlaceHistoryRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : UseCase<Place, Unit>(dispatcher) {
    override suspend fun execute(param: Place) {
        return placeHistoryRepository.deletePlaceHistory(param)
    }
}