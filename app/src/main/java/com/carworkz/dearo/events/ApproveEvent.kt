package com.carworkz.dearo.events


/**
 *
 *
 *
 *
 *
 *
 * @param type represents [ESTIMATE_CUSTOMER_APPROVAL]/[ESTIMATE_REFRESH]
 *
 *  @author Mehul Kadam
 * */
class ApproveEvent(val type: String, val id: String) {
    companion object {
        const val ESTIMATE_CUSTOMER_APPROVAL = "estimate_customer_approve"
        const val ESTIMATE_REFRESH = "estimate_refresh"
    }
}