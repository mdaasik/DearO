package com.carworkz.dearo.helpers

import com.carworkz.dearo.ErrorImpl
import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.domain.entities.Error
import com.carworkz.dearo.events.AccessTokenExpiredEvent
import com.carworkz.dearo.utils.Constants.ErrorConstants
import com.carworkz.dearo.utils.Utility
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.squareup.moshi.Moshi
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Farhan on 3/8/17.
 */

class ErrorWrapperHelper @Inject
internal constructor(private val moshi: Moshi) {
    var status = ""
    private val codes = arrayListOf(400, 401, 422)

    fun transformToErrorWrapper(error: String?): ErrorWrapper {
        val errorObj = Error()
        try {
            val errorJson = JSONObject(error)
            val errorJsonObj = errorJson.getJSONObject("error")
            errorObj.statusCode = errorJsonObj.getInt("statusCode")
            if (codes.find { it == errorObj.statusCode } != null) {
                errorObj.code = errorJsonObj.optString("code")
                errorObj.message = errorJsonObj.getString("message")
                errorObj.name = errorJsonObj.getString("name")
                if (errorJsonObj.optJSONObject("details") != null) {
                    val detailsMsgsJson = errorJsonObj.optJSONObject("details").optJSONObject("messages")
                    if (detailsMsgsJson != null) {
                        val detailsMap = HashMap<String, List<String>>()
                        val it = detailsMsgsJson.keys()
                        var currentIndex = 0
                        it.forEach {
                            detailsMap[it] = Utility.getListFromJsonArray(detailsMsgsJson.getJSONArray(it))
                            currentIndex++
                        }
                        errorObj.details.messages.errorMessages = detailsMap
                    }
                }
            } else {
                errorObj.code = errorJsonObj.optString("statusCode",ErrorConstants.DEFAULT_ERROR_CODE).toString()
                errorObj.message = errorJsonObj.optString("message",ErrorConstants.DEFAULT_ERROR_MESSAGE)
            }
        } catch (e: JSONException) {
            Timber.d(e)
            return transformToErrorWrapper(e)
        }
        val detailsMsgs = errorObj.details.messages.errorMessages
        if (errorObj.code == ErrorConstants.INVALID_ACCESS_TOKEN) {
            EventsManager.post(AccessTokenExpiredEvent())
        }
        FirebaseCrashlytics.getInstance().log("WorkshopId: "+SharedPrefHelper.getWorkshopId()+"\nUserId: "+SharedPrefHelper.getUserId()+"\nStatusCode: "+errorObj.statusCode+"\nErrorCode: "+errorObj.code+"\nErrorMessage: "+errorObj.message+"\nDetailMessage: "+detailsMsgs);
        return ErrorImpl(errorObj.statusCode, errorObj.message, errorObj.name, errorObj.code, detailsMsgs, null)
    }

    fun transformToErrorWrapper(error: String?, metaData: MetaData): ErrorWrapper {
        val errorObj = Error()
        try {
            val errorJson = JSONObject(error)
            val errorJsonObj = errorJson.getJSONObject("error")
            errorObj.statusCode = errorJsonObj.getInt("statusCode")
            if (codes.find { it == errorObj.statusCode } != null) {
                errorObj.code = errorJsonObj.optString("code")
                errorObj.message = errorJsonObj.getString("message")
                errorObj.name = errorJsonObj.getString("name")
                if (errorJsonObj.optJSONObject("details") != null) {
                    val detailsMsgsJson = errorJsonObj.optJSONObject("details").optJSONObject("messages")
                    if (detailsMsgsJson != null) {
                        val detailsMap = HashMap<String, List<String>>()
                        val it = detailsMsgsJson.keys()
                        var currentIndex = 0
                        it.forEach {
                            detailsMap[it] = Utility.getListFromJsonArray(detailsMsgsJson.getJSONArray(it))
                            currentIndex++
                        }
                        errorObj.details.messages.errorMessages = detailsMap
                    }
                }
                errorObj.metaData = metaData
            } else {
                errorObj.code = ErrorConstants.DEFAULT_ERROR_CODE
                errorObj.message = ErrorConstants.DEFAULT_ERROR_MESSAGE
            }
        } catch (e: JSONException) {
            Timber.d(e)
            return transformToErrorWrapper(e)
        }
        val detailsMsgs = errorObj.details.messages.errorMessages
        if (errorObj.code == ErrorConstants.INVALID_ACCESS_TOKEN) {
            EventsManager.post(AccessTokenExpiredEvent())
        }
        FirebaseCrashlytics.getInstance().log("WorkshopId: "+SharedPrefHelper.getWorkshopId()+"\nUserId: "+SharedPrefHelper.getUserId()+"\nStatusCode: "+errorObj.statusCode+"\nErrorCode: "+errorObj.code+"\nErrorMessage: "+errorObj.message+"\nDetailMessage: "+detailsMsgs);
        return ErrorImpl(errorObj.statusCode, errorObj.message, errorObj.name, errorObj.code, detailsMsgs, errorObj.metaData)
    }

    @Suppress("UNUSED_PARAMETER")
    fun transformToErrorWrapper(e: Exception): ErrorWrapper {
        val statusCode = ErrorConstants.NETWORK_ERROR_STATUS_CODE
        val status = ErrorConstants.NETWORK_ERROR_STATUS
        val message = ErrorConstants.DEFAULT_ERROR_MESSAGE
        val code = ErrorConstants.DEFAULT_ERROR_CODE
        FirebaseCrashlytics.getInstance().log("WorkshopId: "+SharedPrefHelper.getWorkshopId()+"\nUserId: "+SharedPrefHelper.getUserId()+"\nStatusCode: "+statusCode+"\nErrorCode: "+code+"\nErrorMessage: "+message+"\nDetailMessage: "+status);
        return ErrorImpl(statusCode, message, status, code, null, null)
    }

    fun transformToErrorWrapper(t: Throwable): ErrorWrapper {
        val statusCode: Int = if (t is InterruptedException)
            ErrorConstants.NETWORK_CANCEL_REQ_CODE
        else
            ErrorConstants.NETWORK_ERROR_STATUS_CODE

        val status = ErrorConstants.NETWORK_ERROR_STATUS
        val message = ErrorConstants.DEFAULT_ERROR_MESSAGE
        val code = ErrorConstants.DEFAULT_ERROR_CODE
        FirebaseCrashlytics.getInstance().log("WorkshopId: "+SharedPrefHelper.getWorkshopId()+"\nUserId: "+SharedPrefHelper.getUserId()+"\nStatusCode: "+statusCode+"\nErrorCode: "+code+"\nErrorMessage: "+message+"\nDetailMessage: "+status);
        return ErrorImpl(statusCode, message, status, code, null, null)
    }
}
