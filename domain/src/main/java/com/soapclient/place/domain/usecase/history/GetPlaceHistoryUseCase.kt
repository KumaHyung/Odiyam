package com.soapclient.place.domain.usecase.history

import com.soapclient.place.domain.UseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.Place
import com.soapclient.place.domain.repository.PlaceHistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetPlaceHistoryUseCase @Inject constructor(
    private val placeHistoryRepository: PlaceHistoryRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : UseCase<String, Place>(dispatcher) {
    override suspend fun execute(param: String): Place {
        return placeHistoryRepository.getPlaceHistory(param)
    }
}