package com.carworkz.dearo.data

import com.carworkz.dearo.base.ErrorWrapper

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out T : Any> {

    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val errorWrapper: ErrorWrapper) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$errorWrapper]"
        }
    }
}
