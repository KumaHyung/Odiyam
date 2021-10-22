package com.soapclient.place.domain.usecase.photo

import android.net.Uri
import com.soapclient.place.domain.UseCase
import com.soapclient.place.domain.di.IoDispatcher
import com.soapclient.place.domain.entity.Result
import com.soapclient.place.domain.repository.PhotoStorageRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val photoStorageRepository: PhotoStorageRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) : UseCase<UploadImageUseCase.Param, Result<Boolean>>(dispatcher) {
    override suspend fun execute(param: Param): Result<Boolean> {
        return photoStorageRepository.uploadImage(param.id, param.uri)
    }

    data class Param(
        val id: String,
        val uri: Uri
    )
}