package com.carworkz.dearo.domain.entities

import com.squareup.moshi.Json

/**
 * Created by kush on 23/11/17.
 */
const val JC_FLOW_FLEXIBLE = "flexible"
const val JC_COMPREHENSIVE = "comprehensive"

class JCConfig {
    @Json(name = "allowNewLabour")
    val allowNewLabour: Boolean? = null
    @Json(name = "allowNewPart")
    val allowNewPart: Boolean? = null
    @Json(name = "allowPartRateChange")
    val allowPartRateChange: Boolean? = null
    @Json(name = "allowLabourRateChange")
    val allowLabourRateChange: Boolean? = null
    @Json(name = "allowJobCardClosure")
    val allowJobCardClosure: Boolean? = null
    @Json(name = "hsnEditable")
    val hsnEditable: HsnEditable? = null
    @Json(name = "allowMrnEstimate")
    val allowMrnEstimate: Boolean? = null
    @Json(name = "flow")
    val flow: String? = null
    @Json(name = "allowLabourSurcharges")
    val allowLabourSurcharges: LabourSurchargeConfig? = null
    @Json(name = "allowLabourReduction")
    val allowLabourReduction: LabourReductionConfig? = null
    @Json(name = "allowReasonForDelay")
    val allowReasonForDelay: ReasonForDelayConfig? = null
    @Json(name = "workOrder")
    val workOrder: WorkOrderConfig? = null
    @Json(name = "preDelivery")
    val preDelivery: PreDeliveryConfig? = null
    @Json(name = "approvals")
    val approvals: Approvals? = null
}
