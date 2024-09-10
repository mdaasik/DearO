package com.carworkz.dearo.data

import com.carworkz.dearo.domain.entities.PdcBase
import com.carworkz.dearo.domain.entities.CustomerAndAddress
import com.carworkz.dearo.amc.AMCPurchase
import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.helpers.PaginationList
import com.carworkz.dearo.domain.entities.PdcEntity
import okhttp3.ResponseBody
import org.jetbrains.annotations.NotNull
import java.io.File

/**
 * Created by Kush on 26/7/17.
 */
interface DataSource {

    interface OnResponseCallback<T> {
        fun onSuccess(obj: T)
        fun onError(error: ErrorWrapper)
    }

    //Auth
    fun sendOtp(mobileNo: String, callback: OnResponseCallback<NetworkPostResponse>)

    fun loginUser(mobileNo: String, otpcode: Int, callback: OnResponseCallback<User>)

    fun logoutUser(callback: OnResponseCallback<NetworkPostResponse?>)

    //CustomerAndCar
    fun searchCustomerAndCarDetails(
        mobileNo: String,
        registrationNumber: String,
        callback: OnResponseCallback<CustomerVehicleSearch>
    )

    //new search api call
    fun newSearchApiCall(
        mobileNo: String,
        registrationNumber: String,
        callback: OnResponseCallback<ResponseBody>
    )

    //Invoice and Job card
    fun saveVoice(jobCardId: String, verbatim: Verbatim, callback: OnResponseCallback<Verbatim>)

    fun getVoiceFromJobCard(jobCardId: String, callback: OnResponseCallback<Verbatim?>)

    fun getInventory(vehicleType: String?, callback: OnResponseCallback<List<Inventory>>)

    fun initiateJobCard(
        customerId: String,
        vehicleId: String,
        appointmentId: String?,
        vehicleAmcId: String?,
        type: String,
        callback: OnResponseCallback<JobCard>
    )

    fun saveJobCardInventory(
        jobCardId: String,
        serviceType: String,
        fuelReading: Float,
        kmsReading: Int,
        inventory: List<Inventory>,
        remarks: String?,
        callback: OnResponseCallback<NetworkPostResponse>
    )

    fun saveJobCardEstimate(
        jobCardId: String,
        dateTime: String,
        minEstimate: Int,
        maxEstimate: Int,
        status: String?,
        notify: Boolean,
        reasonForDelay: String?,
        callback: OnResponseCallback<NetworkPostResponse>
    )
//-------------------------------------------------------------------------

    //Customer and address
    fun addCustomer(
        customer: CustomerAndAddress,
        callback: OnResponseCallback<CustomerAndAddressResponse>
    )

    fun updateCustomer(
        id: String,
        customer: Customer,
        address: Address,
        callback: OnResponseCallback<Customer>
    )

    fun getCustomerById(Id: String, callback: OnResponseCallback<Customer>)

    fun pinCodeCity(pinCode: Int, callback: OnResponseCallback<Pincode>)

    fun addCustomerAddress(address: Address, callback: OnResponseCallback<Address>)

    fun updateCustomerAddress(
        addressId: String,
        address: Address,
        callback: OnResponseCallback<Address>
    )

    //-------------------------------------------------------------------------
    fun getCustomerConcernSuggestions(
        query: String,
        callback: OnResponseCallback<List<CustomerConcern>>
    )

    fun getMake(vehicleType: String?, callback: OnResponseCallback<List<Make>>)

    fun getModel(makeSlug: String, callback: OnResponseCallback<List<Model>>)

    fun getVariant(
        modelSlug: String,
        fuelType: String?,
        callback: OnResponseCallback<List<Variant>>
    )

    fun addVehicle(vehicle: Vehicle, callback: OnResponseCallback<Vehicle>)

    suspend fun updateVehicleVariantInfo(
        vehicleId: String,
        VehicleVariantBody: VehicleVariantBody
    ): Result<NetworkPostResponse>

    fun updateVehicle(vehicle: Vehicle, callback: OnResponseCallback<Vehicle>)

    fun getCompanyNames(callback: OnResponseCallback<List<InsuranceCompany>>)

    fun getInsuranceCompanyAddresses(
        insuranceCompany: InsuranceCompany,
        callback: OnResponseCallback<List<InsuranceCompanyDetails>>
    )

    fun getJobCards(
        types: List<String>,
        limit: Int,
        skip: Int,
        callback: OnResponseCallback<PaginationList<JobCard>>
    )

    fun searchJobCards(search: String, query: String, callback: OnResponseCallback<List<JobCard>>)

    fun cancelJobCard(jobCardId: String, callback: OnResponseCallback<NetworkPostResponse>)

    fun addAlternate(phone: String, customerId: String, callback: OnResponseCallback<Customer>)

    fun updateNumber(phone: String, customerId: String, callback: OnResponseCallback<Customer>)

    fun saveDamageImage(image: FileObject, callback: OnResponseCallback<FileObject>)

    fun savePDCImage(image: FileObject, callback: OnResponseCallback<FileObject>)

    fun updateDamageImage(
        originalFileName: String,
        imageId: String,
        url: String,
        isUploaded: Boolean,
        callback: OnResponseCallback<FileObject>?
    )

    fun updateCaption(
        caption: String,
        jobCardId: String,
        imageId: String,
        callback: OnResponseCallback<FileObject>
    )

    fun getDamages(jobcardId: String, sort: String?, callback: OnResponseCallback<List<FileObject>>)

    suspend fun getDamages(jobcardId: String, sort: String?): Result<List<FileObject>>

    suspend fun getPdcImages(jobcardId: String, sort: String?): Result<List<FileObject>>

    fun deleteDamageImage(damageImg: FileObject, callback: OnResponseCallback<FileObject?>?)

    fun saveDamageImages(jobCardId: String, imageList: List<FileObject>)

    fun clearDamageCache()

    suspend fun saveDamageImage(jobCardId: String, image: FileObject): Result<FileObject>

    fun uploadUnSavedDamage(image: FileObject, callback: OnResponseCallback<FileObject>)

    fun uploadUnSavedDocument(image: FileObject, callback: OnResponseCallback<FileObject>)

    fun getInspectionGroups(
        vehicleType: String?,
        callback: OnResponseCallback<List<InspectionGroup>>
    )

    fun getInspectionItemsByGroup(
        groupId: String,
        callback: OnResponseCallback<List<InspectionItem>>
    )

    fun saveInspection(
        jobcardId: String,
        inspectionPostPOJO: InspectionPostPOJO,
        callback: OnResponseCallback<NetworkPostResponse>
    )

    fun getJobsData(jobcardId: String, callback: OnResponseCallback<JobAndVerbatim>)

    fun getJobs(
        query: String,
        vehicleType: String?,
        callback: OnResponseCallback<List<RecommendedJob>>
    )

    fun getParts(
        query: String,
        vehicleType: String?,
        vehicleAmcId: String?,
        callback: OnResponseCallback<List<Part>>
    )

    fun getLabours(
        jobCardId: String,
        query: String,
        vehicleType: String?,
        vehicleAmcId: String?,
        callback: OnResponseCallback<List<Labour>>
    )

    fun getJobCardDetails(
        jobcardId: String,
        includes: Array<String>,
        callback: OnResponseCallback<JobCard>
    )

    fun saveJobsData(
        jobCardId: String,
        obj: Jobs,
        callback: OnResponseCallback<NetworkPostResponse>
    )

    fun getJobCardById(
        jobCardId: String,
        include: ArrayList<String>?,
        callback: OnResponseCallback<JobCard>
    )


    fun completeJobCard(
        jobCardId: String,
        notify: Boolean,
        reasonForDelay: String?,
        callback: OnResponseCallback<NetworkPostResponse>
    )

    fun closeJobCard(
        jobCardId: String,
        reminderDate: String?,
        notify: Boolean,
        callback: OnResponseCallback<NetworkPostResponse>
    )

    fun getInvoices(
        status: String,
        skip: Int,
        limit: Int,
        type: String?,
        callback: OnResponseCallback<PaginationList<Invoice>>
    )

    fun cancelInvoice(
        invoiceId: String,
        reschedule: Reschedule,
        callback: OnResponseCallback<Invoice>
    )

    fun getInvoiceById(invoiceId: String, callback: OnResponseCallback<Invoice>)

    fun getInvoiceDetailsById(invoiceId: String, callback: OnResponseCallback<Invoice>)

    fun getInvoiceAndInsuranceById(invoiceId: String, callback: OnResponseCallback<Invoice>)

//    fun updatePayment(invoiceId: String, paymentType: String, method: String, amount: String, remarks: String, notifyCustomer: Boolean, callback: OnResponseCallback<NetworkPostResponse>)

    fun saveQuickJobCard(jobCardId: String, jobCard: JobCard, callback: OnResponseCallback<JobCard>)

    // Invoices
    fun searchInvoices(
        search: String,
        query: String,
        type: String,
        callback: OnResponseCallback<List<Invoice>>
    )

    fun updateProforma(invoiceId: String, invoice: Invoice, callback: OnResponseCallback<Invoice>)

    // Part
    fun savePart(id: String, part: Part, callback: OnResponseCallback<Part>)

    fun deletePart(invoiceId: String, partId: String, callback: OnResponseCallback<Invoice>)

    fun fetchBrandName(
        query: String,
        jobCardId: String,
        partNumber: String?,
        vehicleType: String?,
        callback: OnResponseCallback<List<BrandName>>
    )

    fun fetchPartNumber(
        query: String,
        partId: String?,
        jobCardId: String?,
        brandId: String?,
        vehicleType: String?,
        callback: OnResponseCallback<List<PartNumber>>
    )

    fun searchPartNumber(
        query: String?,
        partId: String?,
        jobCardId: String?,
        make: String?,
        model: String?,
        variant: String?,
        fuelType: String?,
        showStock: Boolean,
        vehicleType: String?,
        filterMode: String,
        packageId: String?,
        callback: OnResponseCallback<List<PartNumber>>
    )

    //Suggest parts
    fun searchInStockPartNumbers(
        query: String,
        jobCardId: String?,
        partId: String?,
        brandId: String?,
        vehicleType: String?,
        packageId: String?,
        callback: OnResponseCallback<List<PartNumber>>
    )

    // Labour
    fun saveLabour(id: String, labour: Labour, callback: OnResponseCallback<Labour>)

    fun deleteLabour(invoiceId: String, labourId: String, callback: OnResponseCallback<Invoice>)

    // PDF

    fun getProformaPDF(invoiceId: String, callback: OnResponseCallback<List<PDF>>)

    fun getProformaEstimatePdf(invoiceId: String, callback: OnResponseCallback<PDF>)

    fun createInvoice(
        invoiceId: String,
        notify: Boolean,
        reminderDate: String?,
        callback: OnResponseCallback<List<PDF>>
    )

    fun getJobCardPreview(jobCardId: String, callback: OnResponseCallback<PDF>)

    fun getInvoicePreview(invoiceId: String, callback: OnResponseCallback<Invoice>)

    fun getGatePassPreview(jobCardId: String, callback: OnResponseCallback<PDF>)

    fun getAmcInvoicePdf(vehicleAmcId: String, callback: OnResponseCallback<PDF>)

    fun getPrintEstimate(jobCardId: String, callback: OnResponseCallback<PDF>)

    fun getPrintJobCardPdc(jobCardId: String, callback: OnResponseCallback<PDF>)

    fun getInsurancePdf(invoiceId: String, callback: OnResponseCallback<PDF>)

    fun getCustomerPdf(invoiceId: String, callback: OnResponseCallback<PDF>)

    fun getIgstCustomerPdf(invoiceId: String, callback: OnResponseCallback<PDF>)

    suspend fun getInvoicePdfs(invoiceId: String): Result<List<PDF>>

    // end PDF
    fun checkForceUpdate(
        appName: String,
        platform: String,
        versionCode: Int,
        callback: OnResponseCallback<AppUpdate>?
    )

    fun getUserConfig(callback: OnResponseCallback<User>)

    fun getHSN(callback: OnResponseCallback<List<HSN>>)

    fun saveHSN(hsn: List<HSN>)

    fun deleteHSN(callback: OnResponseCallback<Boolean>)

    // appointment
    fun getAppointments(
        type: String,
        skip: Int,
        limit: Int,
        callback: OnResponseCallback<PaginationList<Appointment>>
    )

    fun getAppointmentsByID(appointmentId: String, callback: OnResponseCallback<Appointment>)

    fun cancelAppointment(appointmentId: String, callback: OnResponseCallback<Appointment>)

    fun reassignAppointment(
        appointmentId: String,
        serviceAdvisorId: String,
        callback: OnResponseCallback<NetworkPostResponse>
    )

    fun getVehicleDetails(registrationNumber: String, callback: OnResponseCallback<List<Vehicle>>)

    fun getCustomerDetails(mobileNumber: String, callback: OnResponseCallback<List<Customer>>)

    fun saveDetails(appointment: Appointment, callback: OnResponseCallback<Appointment>)

    fun saveAppointmentStatus(appointment: AppointmentStatus, callback: OnResponseCallback<Any>)

    fun getPackages(id: String, callback: OnResponseCallback<Packages>)

    fun updatePackages(
        jobCardId: String,
        invoiceId: String,
        isInvoice: Boolean,
        packageIds: List<String>,
        callback: OnResponseCallback<List<ServicePackage>>
    )

    fun savePackages(
        appointmentId: String,
        appointmentPost: AppointmentPost,
        callback: OnResponseCallback<Appointment>
    )

    fun saveTimeSlot(
        appointmentId: String,
        dateTime: AppointmentPost,
        callback: OnResponseCallback<Appointment>
    )

    fun getTimeSlot(
        appointmentId: String,
        dateTime: String,
        type: String?,
        callback: OnResponseCallback<List<TimeSlot>>
    )

    fun upsertData(
        appointmentId: String,
        upsertDetails: UpsertDetails,
        callback: OnResponseCallback<UpsertDetails>
    )

    fun rescheduleAppointment(
        appointmentId: String,
        reschedule: Reschedule,
        callback: OnResponseCallback<Reschedule>
    )

    fun rejectAppointment(
        appointmentId: String,
        reschedule: Reschedule,
        callback: OnResponseCallback<Reschedule>
    )

    fun acceptAppointment(appointmentId: String, callback: OnResponseCallback<Appointment>)

    fun getCustomerVehicleList(
        query: String?,
        filterList: List<String>?,
        startDate: String?,
        endDate: String?,
        skip: Int,
        limit: Int,
        callback: OnResponseCallback<PaginationList<CustomerVehicleDetails>>
    )

    fun getCustomerVehicleHistory(id: String, callback: OnResponseCallback<CustomerVehicleDetails>)

    fun saveCostEstimation(
        @NotNull id: String,
        @NotNull costEstimate: CostEstimate,
        @NotNull callback: OnResponseCallback<JobCard>
    )

    fun saveRemarks(
        jobCardId: String,
        invoiceRemarks: InvoiceRemarks,
        @NotNull callback: OnResponseCallback<JobCard>
    )

    fun getRemarks(jobCardId: String, @NotNull callback: OnResponseCallback<InvoiceRemarks>)

    fun getWorkShopResources(callback: OnResponseCallback<WorkshopResource>)

    /*Accidental Start*/
    fun saveAccidentalData(
        jobCardId: String,
        accidental: Accidental,
        callback: OnResponseCallback<JobCard>
    )

    fun saveMissingAccidentalDetails(
        jobCardId: String,
        missingAccidentalDetails: MissingAccidentalDetails,
        callback: OnResponseCallback<JobCard>
    )

    fun uploadDocument(fileObject: FileObject, callback: OnResponseCallback<FileObject>)

    suspend fun uploadDocument(document: FileObject): Result<FileObject>

    fun saveDocuments(jobCardId: String, files: List<FileObject>)

    fun updateDocument(
        originalFileName: String,
        imageId: String,
        url: String,
        isUploaded: Boolean,
        callback: OnResponseCallback<FileObject>?
    )

    fun getDocuments(jobCardId: String, callback: OnResponseCallback<List<FileObject>>)

    fun deleteDocument(fileObject: FileObject, callback: OnResponseCallback<FileObject>)

    fun clearAccidentalCache()

    /*Accidental End*/

    fun saveLead(leadId: String, leadForm: LeadForm, callback: OnResponseCallback<LeadForm>)

    fun saveServiceReminder(
        vehicleId: String,
        date: String,
        callback: OnResponseCallback<ServiceDate>
    )

    fun addOtcProforma(
        customerId: String,
        addressId: String,
        vehicleType: String?,
        callback: OnResponseCallback<Invoice>
    )

    fun getDashBoardDetails(callback: OnResponseCallback<WorkshopResource>)

    fun saveSignature(jobCardId: String, file: File, callback: OnResponseCallback<Signature>)

    fun fetchUnSavedImages(callback: OnResponseCallback<List<FileObject>>)

    fun registerDevice(
        deviceId: String,
        fcmTokenObj: FcmTokenEntity,
        callback: OnResponseCallback<NetworkPostResponse>
    )

    fun saveCustomerFeedback(
        jobcardId: String,
        recommendedScore: Int,
        serviceQuality: Float,
        billingTransparency: Float,
        timelyDelivery: Float,
        comments: String?,
        callback: OnResponseCallback<NetworkPostResponse>
    )

    fun getServiceAdvisors(workshopId: String, callback: OnResponseCallback<List<WorkshopAdviser>>)

    fun getWhatsAppTemplate(
        templateType: String,
        id: String,
        callback: OnResponseCallback<WhatsAppTemplate>
    )

    fun getStates(callback: OnResponseCallback<List<State>>)

    fun saveStates(states: List<State>, callback: OnResponseCallback<List<State>>?)

    suspend fun saveThirdPartyDetails(invoiceId: String, thirdParty: ThirdParty): Result<ThirdParty>

    suspend fun removeThirdPartyDetails(
        invoiceId: String,
        thirdParty: ThirdParty
    ): Result<ThirdParty>

    //AMC
    fun getAMCList(
        types: String,
        limit: Int,
        skip: Int,
        query: String,
        callback: OnResponseCallback<PaginationList<AMC>>
    )

    fun getVehicleAMCById(vehicleAmcId: String?, callback: OnResponseCallback<List<AMC>>)

    fun suggestAmcPackages(vehicleId: String, callback: OnResponseCallback<List<AMCPackage>>)

    fun purchaseAMC(amcDetails: AMCPurchase, callback: OnResponseCallback<AMC>)

    fun getOtherHistory(
        registrationNumber: String,
        mobileNumber: String,
        callback: OnResponseCallback<CustomerVehicleDetails>
    )

    fun getRedemptionDetails(vehicleAmcId: String, callback: OnResponseCallback<SoldAMCDetails>)

    fun cancelAMC(
        vehicleAmcId: String,
        reason: String,
        callback: OnResponseCallback<SoldAMCDetails>
    )

    fun getVendorList(callback: OnResponseCallback<List<Vendor>>)

    fun getPdcChecklist(jobCardId: String, callback: OnResponseCallback<List<PdcBase>>)

    fun postPdcChecklist(
        jobCardId: String,
        pdcEntity: PdcEntity,
        callback: OnResponseCallback<NetworkPostResponse>
    )

    fun saveCustomerGroup(options: List<Option>)

    fun getCustomerGroup(callback: OnResponseCallback<List<Option>>)

    fun deleteCustomerGroup(callback: OnResponseCallback<Boolean>)
    fun updatePayment(
        invoiceId: String,
        paymentType: String,
        method: String,
        amount: Double,
        transactionNumber: String,
        transactionDetails: String,
        bankName: String,
        cardNumber: String,
        drawnOnDate: String,
        chequeDate: String,
        chequeNumber: String,
        remarks: String,
        notifyCustomer: Boolean,
        callback: OnResponseCallback<NetworkPostResponse>
    )

    fun getSourceTypes(callback: OnResponseCallback<List<CustomerSourceType>>)

    fun getSourcesById(sourceId: String, callback: OnResponseCallback<List<CustomerSource>>)

    fun getReceiptDetails(invoiceId: String, callback: OnResponseCallback<List<Payment>>)

    fun getReceiptPdf(receiptId: String, callback: OnResponseCallback<PDF>)


}