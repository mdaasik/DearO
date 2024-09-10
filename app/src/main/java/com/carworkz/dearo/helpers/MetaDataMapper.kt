package com.carworkz.dearo.helpers

import retrofit2.Response
import timber.log.Timber

class MetaDataMapper {

    companion object {
        fun toObject(response: Response<*>): MetaData {
            val metaData = MetaData()
            try {
                metaData.totalItemsCount = response.headers().get("x-total-count")!!.toInt()
            } catch (e: NullPointerException) {
                Timber.d("empty count $e")
            }
            try {
                metaData.leadId = response.headers().get("x-lead-id")!!.toString()
            } catch (e: NullPointerException) {
                Timber.d("empty$e")
            }
            return metaData
        }
    }
}
