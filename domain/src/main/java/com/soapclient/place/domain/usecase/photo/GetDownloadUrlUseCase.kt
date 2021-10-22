package com.soapclient.place.domain.usecase.photo

import com.soapclient.place.domain.UseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.repository.PhotoStorageRepository
import kotlinx.coroutines.CoroutineDispatcher
import java.net.URL
import javax.inject.Inject

class GetDownloadUrlUseCase @Inject constructor(
    private val photoStorageRepository: PhotoStorageRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : UseCase<GetDownloadUrlUseCase.Param, Result<URL>>(dispatcher) {
    override suspend fun execute(param: Param): Result<URL> {
        return photoStorageRepository.getDownloadUrl(param.path)
    }

    data class Param(
        val path: String
    )
}