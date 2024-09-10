package com.carworkz.dearo.domain.entities

class Query {
    lateinit var q: String
    lateinit var filter: FilterCustomer
    var size: Int? = null
    var from: Int? = null
}

class FilterCustomer {
    companion object {
        const val IN_PROGRESS = "inProgressDate"
        const val COMPLETION_DATE = "completionDate"
        const val INVOICE_DATE = "invoiceDate"
        const val SERVICE_DATE = "serviceDate"
    }
    var inProgressDate: DateRange? = null
    var completionDate: DateRange? = null
    var invoiceDate: DateRange? = null
    var serviceDate: DateRange? = null
}

class DateRange {
    var to: String? = null
    var from: String? = null
}
