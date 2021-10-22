package com.soapclient.place.domain.entity

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow

sealed class Result<out R> {
    class Error<T>(val error: T) : Result<Nothing>()
    class Success<out T>(val data: T) : Result<T>()
}

val Result<*>.succeeded
    get() = this is Result.Success && data != null

fun <T> Result<T>.successOr(fallback: T): T {
    return (this as? Result.Success<T>)?.data ?: fallback
}

val <T> Result<T>.data: T?
    get() = (this as? Result.Success)?.data

inline fun <reified T> Result<T>.updateOnSuccess(liveData: MutableLiveData<T>) {
    if (this is Result.Success) {
        liveData.value = data
    }
}

inline fun <reified T> Result<T>.updateOnSuccess(stateFlow: MutableStateFlow<T>) {
    if (this is Result.Success) {
        stateFlow.value = data
    }
}