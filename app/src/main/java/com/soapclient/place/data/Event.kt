package com.soapclient.place.data

sealed class Event {
    object Loading: Event()
    class Fail<T>(val error: T): Event()
    class Success<T>(val data: T): Event()
}