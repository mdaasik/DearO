package com.carworkz.dearo

import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.helpers.MetaData

/**
 * Created by Farhan on 1/8/17.
 */
class ErrorImpl(private val statusCode: Int, private val message: String?, private val statusName: String?, private val code: String?, private val errorMsgs: Map<String, List<String>>?, private val metaData: MetaData?) : ErrorWrapper {

    override fun getErrorMessages(): Map<String, List<String>>? {
        return errorMsgs
    }

    override fun getErrorCode(): String? {
        return code
    }

    override fun getErrorMessage(): String? {
        return message
    }

    override fun getErrorStatus(): String? {
        return statusName
    }

    override fun getErrorStatusCode(): Int {
        return statusCode
    }

    override fun getHeader(): MetaData? {
        return metaData
    }
}