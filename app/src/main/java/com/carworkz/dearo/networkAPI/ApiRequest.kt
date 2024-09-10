package com.carworkz.dearo.networkAPI

import com.carworkz.dearo.domain.entities.CarpmScanReport
import com.carworkz.dearo.domain.entities.ClearCodeCarpmRequest
import com.carworkz.dearo.domain.entities.ScannedCarpmRequest
import com.carworkz.dearo.utils.Constants.ApiConstants
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiRequest {
    @POST(ApiConstants.REPORT_CAR_PM)
    fun getScannedData(
        @Body request: ScannedCarpmRequest
    ): Call<CarpmScanReport>


    @POST(ApiConstants.CLEAR_CAR_PM)
    fun clearScannedData(
        @Body request: ClearCodeCarpmRequest
    ): Call<JsonElement>
}