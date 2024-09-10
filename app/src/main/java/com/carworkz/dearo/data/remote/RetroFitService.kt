package com.carworkz.dearo.data.remote

import com.carworkz.dearo.amc.AMCPurchase
import com.carworkz.dearo.domain.entities.AMC
import com.carworkz.dearo.domain.entities.AMCPackage
import com.carworkz.dearo.domain.entities.Accidental
import com.carworkz.dearo.domain.entities.Address
import com.carworkz.dearo.domain.entities.AppUpdate
import com.carworkz.dearo.domain.entities.Appointment
import com.carworkz.dearo.domain.entities.AppointmentPost
import com.carworkz.dearo.domain.entities.AppointmentQueryWrapper
import com.carworkz.dearo.domain.entities.AppointmentStatus
import com.carworkz.dearo.domain.entities.BrandName
import com.carworkz.dearo.domain.entities.CarpmScanReport
import com.carworkz.dearo.domain.entities.CostEstimate
import com.carworkz.dearo.domain.entities.Customer
import com.carworkz.dearo.domain.entities.CustomerAndAddress
import com.carworkz.dearo.domain.entities.CustomerAndAddressResponse
import com.carworkz.dearo.domain.entities.CustomerConcern
import com.carworkz.dearo.domain.entities.CustomerSource
import com.carworkz.dearo.domain.entities.CustomerSourceType
import com.carworkz.dearo.domain.entities.CustomerVehicleDetails
import com.carworkz.dearo.domain.entities.CustomerVehicleSearch
import com.carworkz.dearo.domain.entities.FcmTokenEntity
import com.carworkz.dearo.domain.entities.FileObject
import com.carworkz.dearo.domain.entities.HSN
import com.carworkz.dearo.domain.entities.InspectionGroup
import com.carworkz.dearo.domain.entities.InspectionItem
import com.carworkz.dearo.domain.entities.InspectionPostPOJO
import com.carworkz.dearo.domain.entities.InsuranceCompany
import com.carworkz.dearo.domain.entities.InsuranceCompanyDetails
import com.carworkz.dearo.domain.entities.Inventory
import com.carworkz.dearo.domain.entities.InventoryPostPOJO
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.domain.entities.InvoiceRemarks
import com.carworkz.dearo.domain.entities.JobAndVerbatim
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.domain.entities.Jobs
import com.carworkz.dearo.domain.entities.Labour
import com.carworkz.dearo.domain.entities.LeadForm
import com.carworkz.dearo.domain.entities.Make
import com.carworkz.dearo.domain.entities.MissingAccidentalDetails
import com.carworkz.dearo.domain.entities.Model
import com.carworkz.dearo.domain.entities.NetworkPostResponse
import com.carworkz.dearo.domain.entities.Options
import com.carworkz.dearo.domain.entities.PDF
import com.carworkz.dearo.domain.entities.Packages
import com.carworkz.dearo.domain.entities.PartNumber
import com.carworkz.dearo.domain.entities.PartNumberSearchRequest
import com.carworkz.dearo.domain.entities.Payment
import com.carworkz.dearo.domain.entities.PdcBase
import com.carworkz.dearo.domain.entities.PdcEntity
import com.carworkz.dearo.domain.entities.Pincode
import com.carworkz.dearo.domain.entities.RecommendedJob
import com.carworkz.dearo.domain.entities.Reschedule
import com.carworkz.dearo.domain.entities.ServiceDate
import com.carworkz.dearo.domain.entities.Signature
import com.carworkz.dearo.domain.entities.SoldAMCDetails
import com.carworkz.dearo.domain.entities.State
import com.carworkz.dearo.domain.entities.ThirdParty
import com.carworkz.dearo.domain.entities.TimeSlot
import com.carworkz.dearo.domain.entities.UpdatePackage
import com.carworkz.dearo.domain.entities.UpsertDetails
import com.carworkz.dearo.domain.entities.User
import com.carworkz.dearo.domain.entities.Variant
import com.carworkz.dearo.domain.entities.Vehicle
import com.carworkz.dearo.domain.entities.VehicleVariantBody
import com.carworkz.dearo.domain.entities.Vendor
import com.carworkz.dearo.domain.entities.Verbatim
import com.carworkz.dearo.domain.entities.WhatsAppTemplate
import com.carworkz.dearo.domain.entities.WorkshopAdviser
import com.carworkz.dearo.domain.entities.WorkshopResource
import com.carworkz.dearo.retrofitcustomadapter.NetworkCall
import com.carworkz.dearo.utils.Constants.ApiConstants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Farhan on 1/8/17.
 */

interface RetroFitService {

    @get:GET(ApiConstants.URL_GET_WORKSHOP_CONFIG)
    val getWorkshopConfig: NetworkCall<User>

    @get:GET(ApiConstants.URL_HSN_MASTER)
    val getHsn: NetworkCall<List<HSN>>

    @get:GET(ApiConstants.URL_WORKSHOP_RESOURCES)
    val resources: NetworkCall<WorkshopResource>

    @get:GET(ApiConstants.URL_DASHBOARD)
    val dashBoardDetails: NetworkCall<WorkshopResource>

    @FormUrlEncoded
    @POST(ApiConstants.URL_SEND_OTP)
    fun sendOtp(@Field("mobile") mobileNo: String): NetworkCall<NetworkPostResponse>

    @FormUrlEncoded
    @POST(ApiConstants.URL_LOGIN)
    fun loginUser(@Field("mobile") mobileNo: String, @Field("otp") otpCode: Int): NetworkCall<User>

    @POST(ApiConstants.URL_LOGOUT)
    fun logoutUser(): NetworkCall<NetworkPostResponse>

    @Multipart
    @POST(ApiConstants.URL_DAMAGE_IMAGE)
    fun uploadDamageImage(
        @Path("id") Id: String,
        @Query("title") Caption: String,
        @Query("originalName") originalFileName: String,
        @Part image: MultipartBody.Part,
        @Part("ImageName") requestBody: RequestBody
    ): NetworkCall<FileObject>

    @Multipart
    @POST(ApiConstants.URL_DAMAGE_IMAGE)
    fun uploadPDCImage(
        @Path("id") Id: String,
        @Query("title") Caption: String,
        @Query("originalName") originalFileName: String,
        @Query("customScope") customScope: String,
        @Part image: MultipartBody.Part,
        @Part("ImageName") requestBody: RequestBody
    ): NetworkCall<FileObject>

    @Multipart
    @POST(ApiConstants.URL_DAMAGE_IMAGE)
    suspend fun uploadDamageImageAsync(
        @Path("id") Id: String,
        @Query("title") Caption: String,
        @Query("originalName") originalFileName: String,
        @Part image: MultipartBody.Part,
        @Part("ImageName") requestBody: RequestBody
    ): Response<FileObject>

    @DELETE(ApiConstants.URL_DELETE_IMAGE)
    fun deleteDamageImage(
        @Path("id") jobId: String, @Path("fk") ImageId: String
    ): NetworkCall<FileObject>

    @GET(ApiConstants.URL_MAKE)
    fun getMake(
        @Path("id") workshopId: String, @Query("vehicleType") vehicleType: String?
    ): NetworkCall<List<Make>>

    @GET(ApiConstants.URL_MODEL)
    fun getModel(
        @Path("id") workshopId: String, @Query("makeSlug") modelQuery: String
    ): NetworkCall<List<Model>>

    @GET(ApiConstants.URL_VARIANT)
    fun getVariant(
        @Path("id") workshopId: String,
        @Query("modelSlug") variantQuery: String,
        @Query("fuelType") fuelType: String?
    ): NetworkCall<List<Variant>>

    @POST(ApiConstants.URL_ADD_VEHICLE)
    fun addVehicle(@Body vehicle: Vehicle): NetworkCall<Vehicle>

    @FormUrlEncoded
    @POST(ApiConstants.URL_UPDATE_VEHICLE)
    fun updateVehicle(@Path("id") vehicleId: String, @Body vehicle: Vehicle): NetworkCall<Vehicle>

    @POST(ApiConstants.URL_UPDATE_VEHICLE_VARIANT)
    suspend fun updateVehicleVariantInfo(
        @Path("id") vehicleId: String, @Body VehicleVariantBody: VehicleVariantBody
    ): Response<NetworkPostResponse>

    @GET(ApiConstants.URL_INSURANCE_COMPANY)
    fun getInsuranceCompanies(@Header("filter") where: JSONObject): NetworkCall<List<InsuranceCompany>>

    @POST(ApiConstants.URL_INSURANCE_COMPANY_ADDRESSES)
    fun getInsuranceCompaniesAddresses(@Body companySlug: InsuranceCompany): NetworkCall<List<InsuranceCompanyDetails>>

    @GET(ApiConstants.URL_PINCODE)
    fun pinCodeCity(
        @Path("id") pinCode: Int, @Header("filter") include: JSONObject
    ): NetworkCall<Pincode>

    @POST(ApiConstants.URL_ADD_CUSTOMER)
    fun addCustomer(
        @Header("authorization") token: String, @Body customers: Customer
    ): NetworkCall<Customer>

    @POST(ApiConstants.URL_ADD_CUSTOMER_AND_ADDRESS)
    fun addCustomerAndAddress(
        @Header("authorization") token: String, @Body customers: CustomerAndAddress
    ): NetworkCall<CustomerAndAddressResponse>

    @GET(ApiConstants.URL_GET_CUSTOMER_BY_ID)
    fun getCustomerById(
        @Path("id") ID: String, @Header("filter") include: JSONObject
    ): NetworkCall<Customer>

    @PATCH(ApiConstants.URL_UPDATE_CUSTOMER)
    fun updateCustomer(@Body customer: Customer, @Path("id") id: String): NetworkCall<Customer>

    @GET(ApiConstants.URL_GET_CUSTOMER_SUGGEST)
    fun getCustomerConcernSuggestions(@Query("q") query: String): NetworkCall<List<CustomerConcern>>

    @POST(ApiConstants.URL_ADD_ADDRESS)
    fun addAddress(@Body address: Address): NetworkCall<Address>

    @PATCH(ApiConstants.URL_UPDATE_ADDRESS)
    fun updateAddress(@Path("id") addressId: String, @Body address: Address): NetworkCall<Address>

    @POST(ApiConstants.URL_ADD_JOB_CARD_VOICE)
    fun saveVoice(@Path("id") jobCardId: String, @Body verbatim: Verbatim): NetworkCall<Verbatim>

    @GET(ApiConstants.URL_GET_INVENTORY)
    fun getInventories(@Header("filter") obj: JSONObject): NetworkCall<List<Inventory>>

    @FormUrlEncoded
    @POST(ApiConstants.URL_BASE_JOB_CARD)
    fun initJobCard(
        @Field("customerId") customerId: String,
        @Field("vehicleId") vehicleId: String,
        @Field("appointmentId") appointmentId: String?,
        @Field("type") type: String,
        @Field("vehicleAmcId") vehicleAmcId: String?
    ): NetworkCall<JobCard>

    @POST(ApiConstants.URL_ADD_JOB_CARD_INVENTORY)
    fun saveJobCardInventory(
        @Path("id") jobCardId: String, @Body inventoryPostPOJO: InventoryPostPOJO
    ): NetworkCall<NetworkPostResponse>

    @FormUrlEncoded
    @POST(ApiConstants.URL_ADD_JOB_CARD_ESTIMATE)
    fun saveJobCardEstimate(
        @Path("id") jobCardId: String,
        @Field("deliveryDateTime") dateTime: String,
        @Field("minCost") minCost: Int,
        @Field("maxCost") maxCost: Int,
        @Field("status") status: String?,
        @Field("notifyCustomer") notify: Boolean?
    ): NetworkCall<NetworkPostResponse>

    @GET(ApiConstants.URL_BASE_JOB_CARD)
    fun getJobCards(@Header("filter") jsonObject: JSONObject): NetworkCall<List<JobCard>>


    @GET(ApiConstants.URL_CARD_SEARCH)
    fun searchJobCards(
        @Path("path") search: String, @Query("q") query: String, @Header("order") order: String
    ): NetworkCall<List<JobCard>>

    @GET(ApiConstants.URL_CARD_SEARCH)
    fun searchInvoices(
        @Path("path") search: String,
        @Query("q") query: String,
        @Query("type") type: String,
        @Header("order") order: String
    ): NetworkCall<List<Invoice>>

    @GET(ApiConstants.URL_JOB_CARD)
    fun getJobCard(
        @Path("id") jobCardId: String, @Header("filter") jsonObject: JSONObject
    ): NetworkCall<JobCard>

    @GET(ApiConstants.URL_JOB_FROM_JOB_CARD)
    fun getJobFromJobCard(
        @Path("id") jobCardId: String, @Header("filter") jsonObject: JSONObject
    ): NetworkCall<JobCard>

    @GET(ApiConstants.URL_JOB_CARD)
    fun getVerbatimFromJobCard(
        @Path("id") jobCardId: String, @Header("filter") jsonObject: JSONObject
    ): NetworkCall<Verbatim>

    @FormUrlEncoded
    @POST(ApiConstants.URL_JOB_CARD_COMPLETE)
    fun completeJobCard(
        @Path("id") jobCardId: String,
        @Field("notifyCustomer") notify: Boolean?,
        @Field("reason") reasonForDelay: String?
    ): NetworkCall<NetworkPostResponse>

    @FormUrlEncoded
    @POST(ApiConstants.URL_JOB_CARD_CLOSE)
    fun closeJobCard(
        @Path("id") jobCardId: String,
        @Field("serviceDate") serviceDate: String?,
        @Field("notifyCustomer") notify: Boolean?
    ): NetworkCall<NetworkPostResponse>

    @GET(ApiConstants.URL_SEARCH)
    fun searchCustomerAndCarDetails(
        @Query("mobile") mobile: String, @Query("registrationNumber") registrationNumber: String
    ): NetworkCall<CustomerVehicleSearch>


    //have to do
    @GET(ApiConstants.URL_NEW_SEARCH)
    fun searchCustomerNewApiCall(
        @Query("mobile") mobile: String, @Query("registrationNumber") registrationNumber: String
    ): NetworkCall<ResponseBody>


    @FormUrlEncoded
    @PATCH(ApiConstants.URL_UPDATE_CUSTOMER)
    fun addAlternateNumber(
        @Field("alternateMobile") number: String, @Path("id") customerId: String
    ): NetworkCall<Customer>

    @FormUrlEncoded
    @PATCH(ApiConstants.URL_UPDATE_CUSTOMER)
    fun updateNumber(
        @Field("mobile") number: String, @Path("id") customerId: String
    ): NetworkCall<Customer>

    @GET(ApiConstants.URL_INSPECTION_GROUP)
    fun getInspectionGroups(@Header("filter") filter: JSONObject): NetworkCall<List<InspectionGroup>>

    @GET(ApiConstants.URL_INSPECTION_BY_GROUP_ID)
    fun getInspectionItemByGroup(@Path("id") groupId: String): NetworkCall<List<InspectionItem>>

    @POST(ApiConstants.URL_ADD_JOB_CARD_INSPECTION)
    fun saveInspection(
        @Path("id") jobCardId: String, @Body list: InspectionPostPOJO
    ): NetworkCall<NetworkPostResponse>

    @GET(ApiConstants.URL_JOB_SEARCH)
    fun getJobs(
        @Query("q") query: String, @Query("vehicleType") vehicleType: String?
    ): NetworkCall<List<RecommendedJob>>

    @GET(ApiConstants.URL_PART_SEARCH)
    fun getParts(
        @Query("q") query: String,
        @Query("vehicleType") vehicleType: String?,
        @Query("vehicleAmcId") vehicleAmcId: String?
    ): NetworkCall<List<com.carworkz.dearo.domain.entities.Part>>

    @GET(ApiConstants.URL_LABOUR_SEARCH)
    fun getLabours(
        @Query("q") query: String,
        @Query("jobCardId") JobCardId: String,
        @Query("vehicleType") vehicleType: String?,
        @Query("vehicleAmcId") vehicleAmcId: String?
    ): NetworkCall<List<Labour>>

    @GET(ApiConstants.URL_JOB_CARD_GET_RECOMMENDED_JOBS)
    fun getJobsData(@Path("id") jobCardId: String): NetworkCall<JobAndVerbatim>

    @POST(ApiConstants.URL_JOB_CARD_SAVE_RECOMMENDED_JOBS)
    fun saveJobsData(
        @Path("id") jobCardId: String, @Body jobs: Jobs
    ): NetworkCall<NetworkPostResponse>

    @GET(ApiConstants.URL_DAMAGE_DATA)
    fun getGalleryData(@Path("id") jobCardId: String): NetworkCall<List<FileObject>>

    @GET(ApiConstants.URL_DAMAGE_DATA)
    suspend fun getDamages(@Path("id") jobCardId: String): Response<List<FileObject>>

    @GET(ApiConstants.URL_PDC_DATA)
    suspend fun getPdcImages(@Path("id") jobCardId: String): Response<List<FileObject>>

    @FormUrlEncoded
    @PUT(ApiConstants.URL_UPDATE_CAPTION)
    fun editCaption(
        @Path("id") JobCardId: String, @Path("fk") ImageId: String, @Field("title") Caption: String
    ): NetworkCall<FileObject>

    // Invoices
    @GET(ApiConstants.URL_BASE_INVOICES)
    fun getInvoiceCards(@Header("filter") jsonObject: JSONObject): NetworkCall<List<Invoice>>

    @GET(ApiConstants.URL_GET_INVOICE_BY_ID)
    fun getInvoiceById(@Path("id") id: String): NetworkCall<Invoice>

    @GET(ApiConstants.URL_GET_DETAILS_INVOICE)
    fun getInvoiceDetailsById(
        @Path("id") id: String, @Header("filter") includeFilter: JSONObject
    ): NetworkCall<Invoice>

    //    @GET(ApiConstants.URL_GET_INVOICE_BY_ID)
    //    NetworkCall<Invoice> getInvoiceAndInsuranceById(@Path("id") String id, @Header("filter") JSONObject jsonObject);

    @POST(ApiConstants.URL_INVOICES_UPDATE_PROFORMA)
    fun updateProforma(@Path("id") invoiceId: String, @Body invoice: Invoice): NetworkCall<Invoice>

    @POST(ApiConstants.URL_UPDATE_THIRD_PARTY)
    suspend fun updateThirdPartyDetails(
        @Path("id") invoiceId: String, @Body thirdParty: ThirdParty
    ): Response<ThirdParty>

    // Part
    @POST(ApiConstants.URL_ADD_PART)
    fun savePart(
        @Path("id") id: String, @Body part: com.carworkz.dearo.domain.entities.Part
    ): NetworkCall<com.carworkz.dearo.domain.entities.Part>

    @FormUrlEncoded
    @POST(ApiConstants.URL_DELETE_PART)
    fun deletePart(@Path("id") id: String, @Field("uid") partId: String): NetworkCall<Invoice>

    @GET(ApiConstants.URL_BRAND_NAME_SUGGEST)
    fun fetchBrandName(
        @Query("q") query: String,
        @Query("jobCardId") jobCardId: String,
        @Query("partNumber") partNumber: String?,
        @Query("vehicleType") vehicleType: String?
    ): NetworkCall<List<BrandName>>

    @POST(ApiConstants.URL_PART_NUMBER_SUGGEST)
    fun fetchPartNumber(@Body query: PartNumberSearchRequest): NetworkCall<List<PartNumber>>

    @POST(ApiConstants.URL_SEARCH_PART_NUMBER_SUGGEST)
    fun searchPartNumber(@Body query: PartNumberSearchRequest): NetworkCall<List<PartNumber>>

    @POST(ApiConstants.URL_SEARCH_IN_STOCK_PART_NUMBER)
    fun searchInStockPartNumbers(@Body query: PartNumberSearchRequest): NetworkCall<List<PartNumber>>

    // Labour
    @POST(ApiConstants.URL_ADD_LABOUR)
    fun saveLabour(@Path("id") id: String, @Body labour: Labour): NetworkCall<Labour>

    @FormUrlEncoded
    @POST(ApiConstants.URL_DELETE_LABOUR)
    fun deleteLabour(@Path("id") id: String, @Field("uid") labourId: String): NetworkCall<Invoice>

    // pdf
    @GET(ApiConstants.URL_CREATE_PROFORMA)
    fun createProformaPdf(@Path("id") id: String): NetworkCall<List<PDF>>

    @GET(ApiConstants.URL_CREATE_PROFORMA_ESTIMATE)
    fun getProformaEstimatePdf(@Path("id") id: String): NetworkCall<PDF>

    @FormUrlEncoded
    @POST(ApiConstants.URL_CREATE_INVOICE)
    fun createInvoice(
        @Path("id") id: String,
        @Field("notifyCustomer") Notify: Boolean?,
        @Field("serviceDate") serviceDate: String?
    ): NetworkCall<List<PDF>>

    @POST(ApiConstants.URL_JC_PREVIEW)
    fun getJobCardPreview(@Path("id") id: String): NetworkCall<PDF>

    @GET(ApiConstants.URL_GET_INVOICE_BY_ID)
    fun getPdf(@Path("id") id: String, @Header("filter") filter: JSONObject): NetworkCall<Invoice>

    @POST(ApiConstants.URL_GET_JOBCARD_ESTIMATE)
    fun createEstimatePdf(@Path("id") id: String): NetworkCall<PDF>

    @POST(ApiConstants.URL_GET_JOBCARD_PDC_PRINT)
    fun createPdcPdf(@Path("id") id: String): NetworkCall<PDF>

    @GET(ApiConstants.URL_INSURANCE_PDF)
    fun getInsurancePdf(@Path("id") id: String): NetworkCall<PDF>

    @GET(ApiConstants.URL_IGST_CUSTOMER_PDF)
    fun getIgstCustomerPdf(@Path("id") id: String): NetworkCall<PDF>

    @GET(ApiConstants.URL_CUSTOMER_PDF)
    fun getCustomerPdf(@Path("id") id: String): NetworkCall<PDF>

    @GET(ApiConstants.URL_GET_INVOICE_PDFS)
    suspend fun getInvoicePdfs(@Path("id") invoiceId: String): Response<List<PDF>>

    // pdf end

    @FormUrlEncoded
    @POST(ApiConstants.URL_FORCE_UPDATE)
    fun checkForceUpdate(
        @Field("type") appname: String,
        @Field("platform") platform: String,
        @Field("versionCode") versionCode: Int
    ): NetworkCall<AppUpdate>

    @GET(ApiConstants.URL_GATE_PASS)
    fun gatePass(@Path("id") id: String): NetworkCall<PDF>

    @FormUrlEncoded
    @POST(ApiConstants.URL_INVOICES_UPDATE_PAYMENT)
    fun updatePayment(
        @Path("id") id: String,
        @Field("type") type: String,
        @Field("method") method: String,
        @Field("amount") amount: Double,
        @Field("transactionNumber") transactionNumber: String,
        @Field("transactionDetails") transactionDetails: String,
        @Field("bankName") bankName: String,
        @Field("cardNumber") cardNumber: String,
        @Field("drawnOnDate") drawnOnDate: String,
        @Field("chequeDate") chequeDate: String,
        @Field("chequeNumber") chequeNumber: String,
        @Field("remarks") remark: String,
        @Field("notifyCustomer") notify: Boolean?
    ): NetworkCall<NetworkPostResponse>


    @POST(ApiConstants.URL_INVOICES_UPDATE_PAYMENT)
    fun updatePaymentV1(
        @Path("id") id: String, @Body payment: Payment
    ): NetworkCall<NetworkPostResponse>

    @GET(ApiConstants.URL_ADD_CUSTOMER)
    fun getCustomerDetails(@Query("filter") query: JSONObject): NetworkCall<List<Customer>>

    @GET(ApiConstants.URL_ADD_VEHICLE)
    fun getVehicleDetails(@Query("filter") query: JSONObject): NetworkCall<List<Vehicle>>

    @POST(ApiConstants.URL_APPOINTMENT_DETAILS)
    fun saveDetails(@Body appointment: Appointment): NetworkCall<Appointment>

    @POST(ApiConstants.URL_APPOINTMENT_STATUS_UPDATE)
    fun updateAppointmentStatus(@Body appointmentStatus: AppointmentStatus): NetworkCall<Any>

    @POST(ApiConstants.URL_APPOINTMENT_PACKAGES)
    fun savePackages(
        @Path("id") appointmentId: String, @Body appointmentPost: AppointmentPost
    ): NetworkCall<Appointment>

    @GET(ApiConstants.URL_APPOINTMENT_GET_TIME_SLOT)
    fun getTimeSlot(
        @Path("id") appointmentId: String, @Query("date") date: String
    ): NetworkCall<List<TimeSlot>>

    @POST(ApiConstants.URL_APPOINTMENT_SAVE_TIME_SLOT)
    fun saveTimeSlot(
        @Path("id") appointmentId: String, @Body appointmentPost: AppointmentPost
    ): NetworkCall<Appointment>

    @POST(ApiConstants.URL_GET_APPOINTMENTS)
    fun getAppointment(@Body appointmentQueryWrapper: AppointmentQueryWrapper): NetworkCall<List<Appointment>>

    @POST(ApiConstants.URL_CANCEL_APPOINTMENTS)
    fun cancelAppointment(@Path("id") id: String): NetworkCall<Appointment>

    @GET(ApiConstants.URL_GET_APPOINTMENTS_BY_ID)
    fun getAppointmentById(
        @Path("id") appointmentId: String, @Header("filter") include: JSONObject
    ): NetworkCall<Appointment>

    @POST(ApiConstants.URL_APPOINTMENT_UPSERTDETAILS)
    fun upsertDetails(
        @Path("id") appointmentId: String, @Body upsertDetails: UpsertDetails
    ): NetworkCall<UpsertDetails>

    @POST(ApiConstants.URL_APPOINTMENT_RESCHEDULE)
    fun rescheduleAppointment(
        @Path("id") appointmentId: String, @Body reschedule: Reschedule
    ): NetworkCall<Reschedule>

    @POST(ApiConstants.URL_APPOINTMENT_DECLINE)
    fun rejectAppointment(
        @Path("id") appointmentId: String, @Body reschedule: Reschedule
    ): NetworkCall<Reschedule>

    @POST(ApiConstants.URL_APPOINTMENT_ACCEPT)
    fun acceptAppointment(@Path("id") appointmentId: String): NetworkCall<Appointment>

    @POST(ApiConstants.URL_CUSTOMER_VEHICLES)
    fun getCustomerVehiclesList(@Body query: com.carworkz.dearo.domain.entities.Query): NetworkCall<List<CustomerVehicleDetails>>

    @GET(ApiConstants.URL_CUSTOMER_VEHICLE_HISTORY)
    fun getCustomerVehiclesHistory(@Path("id") id: String): NetworkCall<CustomerVehicleDetails>

    @POST(ApiConstants.URL_CANCEL_INVOICE)
    fun cancelInvoice(@Path("id") id: String, @Body reschedule: Reschedule): NetworkCall<Invoice>

    @POST(ApiConstants.URL_COST_ESTIMATE)
    fun saveEstimation(
        @Path("id") id: String, @Body costEstimate: CostEstimate
    ): NetworkCall<JobCard>

    @POST(ApiConstants.URL_SAVE_REMARK)
    fun saveRemarks(
        @Path("id") id: String, @Body invoiceRemarks: InvoiceRemarks
    ): NetworkCall<JobCard>

    @GET(ApiConstants.URL_GET_REMARK)
    fun getRemarks(@Path("id") id: String): NetworkCall<InvoiceRemarks>

    /*Accidental Start*/

    @POST(ApiConstants.URL_ACCIDENTAL)
    fun saveAccidental(@Path("id") id: String, @Body accidental: Accidental): NetworkCall<JobCard>

    @POST(ApiConstants.URL_MISSING_ACCIDENTAL)
    fun saveMissingAccidentalDetails(
        @Path("id") jobCardId: String, @Body missingAccidentalDetails: MissingAccidentalDetails
    ): NetworkCall<JobCard>

    @Multipart
    @POST(ApiConstants.URl_UPLOAD_DOC)
    fun uploadDocument(
        @Path("id") Id: String,
        @Query("category") category: String,
        @Query("originalName") originalFileName: String,
        @Part file: MultipartBody.Part,
        @Part("FileName") requestBody: RequestBody
    ): NetworkCall<FileObject>

    @Multipart
    @POST(ApiConstants.URl_UPLOAD_DOC)
    suspend fun uploadDocumentAsync(
        @Path("id") Id: String,
        @Query("category") category: String,
        @Query("originalName") originalFileName: String,
        @Part file: MultipartBody.Part,
        @Part("FileName") requestBody: RequestBody
    ): Response<FileObject>

    @GET(ApiConstants.URL_GET_DOC)
    fun getDocuments(@Path("id") jobCardId: String): NetworkCall<List<FileObject>>

    @FormUrlEncoded
    @POST(ApiConstants.URL_DELETE_DOC)
    fun deleteDocument(
        @Path("id") jobCardId: String, @Field("documentId") documentId: String
    ): NetworkCall<FileObject>

    /*Accidental End*/

    @PATCH(ApiConstants.URL_SAVE_LEAD)
    fun saveLead(@Path("id") leadId: String, @Body leadForm: LeadForm): NetworkCall<LeadForm>

    @FormUrlEncoded
    @POST(ApiConstants.URL_SAVE_SEVICE_DATE)
    fun saveServiceReminder(
        @Path("id") vehicleId: String, @Field("serviceDate") date: String
    ): NetworkCall<ServiceDate>

    @FormUrlEncoded
    @POST(ApiConstants.URL_ADD_OTC_PROFORMA)
    fun addOtcProforma(
        @Field("customerId") customerId: String,
        @Field("customerAddressId") addressId: String,
        @Field("vehicleType") vehicleType: String?
    ): NetworkCall<Invoice>

    @GET(ApiConstants.URL_GET_PACKAGES)
    fun getPackages(@Path("id") id: String): NetworkCall<Packages>

    @POST(ApiConstants.URL_UPDATE_JOBCARD_PACKAGES)
    fun updatePackages(
        @Path("id") id: String, @Body updatePackage: UpdatePackage
    ): NetworkCall<JobCard>

    @POST(ApiConstants.URL_UPDATE_INVOICE_PACKAGES)
    fun updateInvoicePackages(
        @Path("id") id: String, @Body updatePackage: UpdatePackage
    ): NetworkCall<JobCard>

    @Multipart
    @POST(ApiConstants.URL_SIGNATURE)
    fun saveSignature(
        @Path("id") Id: String,
        @Part image: MultipartBody.Part,
        @Part("ImageName") requestBody: RequestBody
    ): NetworkCall<Signature>

    @POST(ApiConstants.URL_SAVE_QUICK_JOBCARD)
    fun saveQuickJobCard(@Path("id") id: String, @Body jobCard: JobCard): NetworkCall<JobCard>

    @POST(ApiConstants.URL_DEVICE_REGISTRAR)
    fun registerDevice(
        @Query("where") deviceID: JSONObject, @Body obj: FcmTokenEntity
    ): NetworkCall<NetworkPostResponse>

    @POST(ApiConstants.URL_CANCEL_JOBCARD)
    fun cancelJobCard(@Path("id") id: String): NetworkCall<NetworkPostResponse>

    @FormUrlEncoded
    @POST(ApiConstants.URL_SAVE_FEEDBACK)
    fun saveFeedback(
        @Path("id") id: String,
        @Field("recommendedScore") recommendedScore: Int,
        @Field("serviceQuality") serviceQuality: Float,
        @Field("billingTransparency") billingTransparency: Float,
        @Field("timelyDelivery") timelyDelivery: Float,
        @Field("comment") comments: String?,
        @Field("source") source: String
    ): NetworkCall<NetworkPostResponse>

    @GET(ApiConstants.URL_GET_SERVICE_ADVISORS)
    fun getWorkshopAdvisers(@Path("id") id: String): NetworkCall<List<WorkshopAdviser>>

    @FormUrlEncoded
    @POST(ApiConstants.URL_APPOINTMENT_REASSIGN)
    fun reassignAppointment(
        @Path("id") id: String, @Field("serviceAdvisorId") serviceAdvisorId: String
    ): NetworkCall<NetworkPostResponse>

    @GET(ApiConstants.URL_WHATSAPP_TEMPLATE)
    fun getWhatsAppTemplate(@Query("option") option: JSONObject): NetworkCall<WhatsAppTemplate>

    @GET(ApiConstants.URL_GET_STATES)
    fun getStates(): NetworkCall<List<State>>

    @GET(ApiConstants.URL_GET_AMC_LIST)
    fun getAmcCards(@Query("options") jsonObject: JSONObject): NetworkCall<List<AMC>>

    @GET(ApiConstants.URL_GET_AMC_LIST)
    fun getAmcCardById(@Header("filter") jsonObject: JSONObject): NetworkCall<List<AMC>>

    @GET(ApiConstants.URL_SUGGEST_AMC_LIST)
    fun suggestAmcPackages(@Header("options") jsonObject: JSONObject): NetworkCall<List<AMCPackage>>

    @POST(ApiConstants.URL_PURCHASE_AMC)
    fun purchaseAMC(@Body amcDetails: AMCPurchase): NetworkCall<AMC>

    @POST(ApiConstants.URL_VEHICLE_AMC_PDF)
    fun getAmcInvoice(
        @Path("id") vehicleAmcId: String, @Header("filter") include: JSONObject
    ): NetworkCall<PDF>

    @GET(ApiConstants.URL_GET_AMC_REDEMPTION)
    fun getRedemptionDetails(@Path("id") id: String): NetworkCall<SoldAMCDetails>

    @GET(ApiConstants.URL_GET_JOBCARD_PDC_CHECKLIST)
    fun getPdcChecklist(@Path("id") id: String): NetworkCall<List<PdcBase>>

    @POST(ApiConstants.URL_POST_JOBCARD_PDC_CHECKLIST)
    fun postPdcChecklist(
        @Path("id") jobCardId: String, @Body pdcEntity: PdcEntity
    ): NetworkCall<NetworkPostResponse>

    @FormUrlEncoded
    @POST(ApiConstants.URL_VEHICLE_AMC_CANCEL)
    fun cancelVehicleAMC(
        @Path("id") vehicleAmcId: String, @Field("reason") reason: String
    ): NetworkCall<SoldAMCDetails>

    /* @FormUrlEncoded
     @POST(ApiConstants.URL_GET_OTHER_SYS_HISTORY)
     fun getOtherSysHistory(@Field("registrationNumber")registrationNumber: String,@Field("mobile") mobileNumber: String):NetworkCall<OtherSysHistory>
     */

    @POST(ApiConstants.URL_GET_OTHER_SYS_HISTORY)
    fun getOtherSysHistory(@Body option: Options): NetworkCall<CustomerVehicleDetails>

    @GET(ApiConstants.URL_GET_VENDOR_LIST)
    fun getVendorList(): NetworkCall<List<Vendor>>

    @GET(ApiConstants.URL_CUSTOMER_SOURCE_TYPES)
    fun getSourceTypes(): NetworkCall<List<CustomerSourceType>>

    @GET(ApiConstants.URL_CUSTOMER_SOURCE_BY_ID)
    fun getSourcesById(@Path("id") id: String): NetworkCall<List<CustomerSource>>

    @GET(ApiConstants.URL_GET_RECEIPT_DETAILS)
    fun getReceiptDetails(@Path("id") invoiceId: String): NetworkCall<List<Payment>>

    @GET(ApiConstants.URL_GET_RECEIPT_PDF)
    fun getReceiptPDF(@Path("id") receiptId: String): NetworkCall<PDF>

    @GET(ApiConstants.REPORT_CAR_PM)
    fun getScannedData(
        @Query("scan_id") scanId: String,
        @Header("Authorization") authToken: String,
        @Header("Another-Header") anotherHeader: String
    ): NetworkCall<CarpmScanReport>

}
