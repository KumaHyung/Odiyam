package com.soapclient.place.domain.usecase.place

import com.soapclient.place.domain.UseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.PlaceSearchResult
import com.soapclient.place.domain.repository.PlaceSearchRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SearchPlaceUseCase @Inject constructor(
    private val placeSearchRepository: PlaceSearchRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : UseCase<SearchPlaceUseCase.Param, PlaceSearchResult>(dispatcher) {
    override suspend fun execute(param: Param): PlaceSearchResult {
        return placeSearchRepository.searchPlace(
            category_group_code = param.category_group_code,
            x = param.x,
            y = param.y,
            radius = param.radius,
            page = param.page
        )
    }

    data class Param(
        val category_group_code: String,
        val x: String,
        val y: String,
        val radius: Int,
        val page: Int
    )
}