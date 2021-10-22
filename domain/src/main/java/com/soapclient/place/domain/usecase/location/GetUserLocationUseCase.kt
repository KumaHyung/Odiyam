package com.soapclient.place.domain.usecase.location

import com.soapclient.place.domain.FlowUseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.UserLocation
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.repository.UserLocationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserLocationUseCase @Inject constructor(
    private val locationRepository: UserLocationRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : FlowUseCase<String, Pair<String, UserLocation>>(dispatcher) {
    override fun execute(parameters: String): Flow<Result<Pair<String, UserLocation>>> {
        return locationRepository.getUserLocation(parameters)
    }
}