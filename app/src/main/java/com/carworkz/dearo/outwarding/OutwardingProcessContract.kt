package com.carworkz.dearo.outwarding

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.*

interface OutwardingProcessContract {

    interface View : BaseView<Presenter> {

        fun displayJobEstimation(obj:JobCard)

        fun displayProforma(invoice: Invoice, hsnList: List<HSN>)

        fun displaySplitInvoice(invoice: Invoice)

        fun showPartSelection(jobCardId: String, partId: String?, partName: String, brandId: String?, partNumbers: List<PartNumber>)

        fun showError(error: String?)

        fun moveToNextScreen(preview: Boolean)

        fun showPrintEstimate()

        fun onSaveSuccess(savedObj: Any?, showCustomerApproval: Boolean)

        fun onThirdPartyRemoveSuccess()

        fun showAmcDetails(obj: List<AMC>)

        fun displaySearchResults(partNumbers: List<PartNumber>)

        fun onFetchVendors(vendors: List<Vendor>)
    }

    interface Presenter : BasePresenter {
        fun getPartNumber(jobCardId: String, partId: String?, partName: String, brandId: String?, showStock: Boolean, vehicleType: String?,packageId: String?)

        fun getEstimation(jobCardId: String)

        fun saveEstimation(
            jobCardId: String,
            obj: CostEstimate,
            showPdf: Boolean,
            showCustomerApproval: Boolean
        )

        fun getProformaInvoice(invoiceId: String)

        fun removeThirdParty(invoiceId: String, thirdParty: ThirdParty)

        fun getHsn()

        fun updateProformaInvoice(invoiceId: String, invoice: Invoice, isPreview: Boolean)

        fun getVehicleAmcById(id:String?)

        fun getVendorList()
    }
}