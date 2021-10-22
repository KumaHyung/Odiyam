package com.soapclient.place.domain.usecase.location

import com.soapclient.place.domain.UseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.Location
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.repository.UserLocationRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UpdateLocationUseCase @Inject constructor(
    private val locationRepository: UserLocationRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : UseCase<UpdateLocationUseCase.Param, Result<Boolean>>(dispatcher) {
    override suspend fun execute(param: Param): Result<Boolean> {
        return locationRepository.updateLocation(param.id, param.location)
    }

    data class Param(
        val id: String,
        val location: Location
    )
}