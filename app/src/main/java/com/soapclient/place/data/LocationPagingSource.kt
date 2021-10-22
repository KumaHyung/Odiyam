package com.soapclient.place.data

import androidx.paging.PagingSource
import com.soapclient.place.domain.entity.Place
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.usecase.place.SearchPlaceByKeywordUseCase

private const val STARTING_PAGE_INDEX = 1

class LocationPagingSource(
        private val searchPlaceByKeywordUseCase: SearchPlaceByKeywordUseCase,
        private val query: String,
        private val x: String,
        private val y: String
) : PagingSource<Int, Place>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Place> {
        val page = params.key ?: STARTING_PAGE_INDEX
        return try {
            val result = searchPlaceByKeywordUseCase(SearchPlaceByKeywordUseCase.Param(keyword = query, page = page, x = x, y = y, size = params.loadSize))
            if (result is Result.Success) {
                LoadResult.Page(
                    data = result.data.placeList,
                    prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                    nextKey = if (result.data.isLastData) null else page + 1
                )
            } else {
                throw Exception("Search place fail")
            }
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}
