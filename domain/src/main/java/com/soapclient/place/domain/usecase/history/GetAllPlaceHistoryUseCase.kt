package com.soapclient.place.domain.usecase.history

import com.soapclient.place.domain.FlowUseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.Place
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.repository.PlaceHistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllPlaceHistoryUseCase @Inject constructor(
    private val placeHistoryRepository: PlaceHistoryRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : FlowUseCase<Unit, List<Place>>(dispatcher) {
    override fun execute(parameters: Unit): Flow<Result<List<Place>>> {
        return placeHistoryRepository.getAllPlaceHistory()
    }
}