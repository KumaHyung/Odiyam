package com.soapclient.place.domain.usecase.history

import com.soapclient.place.domain.UseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.repository.PlaceHistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class IsExistPlaceHistoryUseCase @Inject constructor(
    private val placeHistoryRepository: PlaceHistoryRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : UseCase<String, Boolean>(dispatcher) {
    override suspend fun execute(param: String): Boolean {
        return placeHistoryRepository.isExistPlaceHistory(param)
    }
}