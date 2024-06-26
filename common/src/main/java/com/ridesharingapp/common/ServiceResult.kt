package com.ridesharingapp.common

sealed interface ServiceResult<out T> {
    data class Value<T> (val value: T): ServiceResult<T>
    data class Failure<T>(val exception: Exception): ServiceResult<T>
}