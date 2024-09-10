package com.carworkz.dearo.domain.entities

import com.google.gson.annotations.SerializedName

data class CarpmScanReport(
    @SerializedName("license_plate") val licensePlate: String,
    @SerializedName("report_url") val reportUrl: String,
    @SerializedName("car_company") val carCompany: String,
    @SerializedName("status") val status: Int,
    @SerializedName("time") val time: String,
    @SerializedName("mechanic_name") val mechanicName: String,
    @SerializedName("mechanic_email") val mechanicEmail: String,
    @SerializedName("vin") val vin: String,
    @SerializedName("fuel_trim") val fuelTrim: String?,
    @SerializedName("fuel_system") val fuelSystem: String?,
    @SerializedName("battery_voltage") val batteryVoltage: String?,
    @SerializedName("engine_misfire") val engineMisfire: String?,
    @SerializedName("engine_knocking") val engineKnocking: String?,
    @SerializedName("idling_rpm") val idlingRpm: String?,
    @SerializedName("idling_rpm_status") val idlingRpmStatus: String?,
    @SerializedName("coolant_temp_status") val coolantTempStatus: String?,
    @SerializedName("max_coolant_temp") val maxCoolantTemp: String?,
    @SerializedName("scan_ended") val scanEnded: String,
    @SerializedName("code_details") val codeDetails: List<CodeDetail>
)

data class CodeDetail(
    @SerializedName("dtc") val dtc: String,
    @SerializedName("meaning") val meaning: String,
    @SerializedName("module") val module: String,
    @SerializedName("status") val status: String,
    @SerializedName("system") val system: String,
    @SerializedName("severity") val severity: Int,
    @SerializedName("descriptions") val descriptions: List<String>,
    @SerializedName("causes") val causes: List<String>,
    @SerializedName("solutions") val solutions: List<String>,
    @SerializedName("symptoms") val symptoms: List<String>
)
