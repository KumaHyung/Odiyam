package com.soapclient.place.domain.usecase.place

import com.soapclient.place.domain.UseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.PlaceSearchResult
import com.soapclient.place.domain.repository.PlaceSearchRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SearchPlaceByKeywordUseCase @Inject constructor(
    private val placeSearchRepository: PlaceSearchRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : UseCase<SearchPlaceByKeywordUseCase.Param, PlaceSearchResult>(dispatcher) {
    override suspend fun execute(param: Param): PlaceSearchResult {
        return placeSearchRepository.searchPlaceByKeyword(
            keyword = param.keyword,
            x = param.x,
            y = param.y,
            page = param.page,
            size = param.size
        )
    }

    data class Param(
        val keyword: String,
        val x: String,
        val y: String,
        val page: Int,
        val size: Int
    )
}