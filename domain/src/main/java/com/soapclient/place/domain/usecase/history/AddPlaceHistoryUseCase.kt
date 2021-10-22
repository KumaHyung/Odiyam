package com.soapclient.place.domain.usecase.history

import com.soapclient.place.domain.UseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.Place
import com.soapclient.place.domain.repository.PlaceHistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AddPlaceHistoryUseCase @Inject constructor(
    private val placeHistoryRepository: PlaceHistoryRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : UseCase<Place, Long>(dispatcher) {
    override suspend fun execute(param: Place): Long {
        return placeHistoryRepository.insertPlaceHistory(param)
    }
}