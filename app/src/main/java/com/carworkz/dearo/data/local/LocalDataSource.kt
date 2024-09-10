package com.carworkz.dearo.data.local

import com.carworkz.dearo.domain.entities.CustomerAndAddress
import com.carworkz.dearo.amc.AMCPurchase
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.DataSource.OnResponseCallback
import com.carworkz.dearo.data.Result
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.exceptions.UnavailableException
import com.carworkz.dearo.helpers.ErrorWrapperHelper
import com.carworkz.dearo.helpers.PaginationList
import com.carworkz.dearo.domain.entities.PdcBase
import com.carworkz.dearo.domain.entities.PdcEntity
import com.carworkz.dearo.utils.Constants.DatabaseConstants
import io.realm.Realm
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject

/**
 * Created by Kush on 27/7/17.
 */

class LocalDataSource @Inject constructor(private val errorWrapperHelper: ErrorWrapperHelper) : DataSource {

    override fun reassignAppointment(appointmentId: String, serviceAdvisorId: String, callback: OnResponseCallback<NetworkPostResponse>) = Unit

    override fun saveQuickJobCard(jobCardId: String, jobCard: JobCard, callback: OnResponseCallback<JobCard>) = Unit

    override fun getInvoiceDetailsById(invoiceId: String, callback: OnResponseCallback<Invoice>) = Unit

    override fun getInvoiceAndInsuranceById(invoiceId: String, callback: OnResponseCallback<Invoice>) = Unit

    override fun getCustomerPdf(invoiceId: String, callback: OnResponseCallback<PDF>) = Unit

    override fun getInsurancePdf(invoiceId: String, callback: OnResponseCallback<PDF>) = Unit

    override fun getIgstCustomerPdf(invoiceId: String, callback: OnResponseCallback<PDF>) = Unit

    override suspend fun getInvoicePdfs(invoiceId: String): Result<List<PDF>> {
        throw UnsupportedOperationException("invoice aren't stored local database")
    }

    override fun searchPartNumber(query: String?, partId: String?, jobCardId: String?, make: String?, model: String?, variant: String?, fuelType: String?, showStock: Boolean, vehicleType: String?, filterMode: String,packageId: String?, callback: OnResponseCallback<List<PartNumber>>) {
    }

    override fun searchInStockPartNumbers(query: String, jobCardId: String?, partId: String?, brandId: String?, vehicleType: String?,packageId: String?, callback: OnResponseCallback<List<PartNumber>>) {
    }

    override fun fetchPartNumber(query: String, partId: String?, jobCardId: String?, brandId: String?, vehicleType: String?, callback: OnResponseCallback<List<PartNumber>>) {
    }

    override fun updateProforma(invoiceId: String, invoice: Invoice, callback: OnResponseCallback<Invoice>) {
    }

    override fun saveCostEstimation(id: String, costEstimate: CostEstimate, callback: OnResponseCallback<JobCard>) {
    }

    override fun getProformaEstimatePdf(invoiceId: String, callback: OnResponseCallback<PDF>) {
    }

    override fun getAppointmentsByID(appointmentId: String, callback: OnResponseCallback<Appointment>) {
    }

    override fun upsertData(appointmentId: String, upsertDetails: UpsertDetails, callback: OnResponseCallback<UpsertDetails>) {
    }

    override fun getAppointments(type: String, skip: Int, limit: Int, callback: OnResponseCallback<PaginationList<Appointment>>) {
    }

    override fun getVariant(modelSlug: String, fuelType: String?, callback: OnResponseCallback<List<Variant>>) {
    }


    override fun checkForceUpdate(appName: String, platform: String, versionCode: Int, callback: OnResponseCallback<AppUpdate>?) {
    }

    override fun getUserConfig(callback: OnResponseCallback<User>) {
    }

    override fun completeJobCard(jobCardId: String, notify: Boolean,reasonForDelay: String?, callback: OnResponseCallback<NetworkPostResponse>) {
    }

    override fun closeJobCard(jobCardId: String, reminderDate: String?, notify: Boolean, callback: OnResponseCallback<NetworkPostResponse>) {
    }

    override fun saveJobCardEstimate(jobCardId: String, dateTime: String, minEstimate: Int, maxEstimate: Int, status: String?, notify: Boolean, reasonForDelay: String?, callback: OnResponseCallback<NetworkPostResponse>)
    {
    }

    override fun getVoiceFromJobCard(jobCardId: String, callback: OnResponseCallback<Verbatim?>) {
    }

    override fun saveInspection(jobcardId: String, inspectionPostPOJO: InspectionPostPOJO, callback: OnResponseCallback<NetworkPostResponse>) {
    }

    override fun getJobCardById(jobCardId: String, include: ArrayList<String>?, callback: OnResponseCallback<JobCard>) {
    }

    override fun getJobCardDetails(jobcardId: String, includes: Array<String>, callback: OnResponseCallback<JobCard>) {
    }

    override fun addAlternate(phone: String, customerId: String, callback: OnResponseCallback<Customer>) {
    }

    override fun updateNumber(phone: String, customerId: String, callback: OnResponseCallback<Customer>) {
    }

    override fun sendOtp(mobileNo: String, callback: OnResponseCallback<NetworkPostResponse>) {
    }

    override fun loginUser(mobileNo: String, otpcode: Int, callback: OnResponseCallback<User>) {
    }

    override fun logoutUser(callback: OnResponseCallback<NetworkPostResponse?>) {
    }

    override fun saveVoice(jobCardId: String, verbatim: Verbatim, callback: OnResponseCallback<Verbatim>) {
    }

    override fun getInventory(vehicleType: String?, callback: OnResponseCallback<List<Inventory>>) {
    }

    override fun initiateJobCard(customerId: String, vehicleId: String, appointmentId: String?, vehicleAmcId: String?, type: String, callback: OnResponseCallback<JobCard>) {
    }


    override fun saveJobCardInventory(jobCardId: String, serviceType: String, fuelReading: Float, kmsReading: Int, inventory: List<Inventory>, remarks: String?, callback: OnResponseCallback<NetworkPostResponse>) {
    }

    override fun addCustomer(customer: CustomerAndAddress, callback: OnResponseCallback<CustomerAndAddressResponse>) {
    }

    override fun getCustomerConcernSuggestions(query: String, callback: OnResponseCallback<List<CustomerConcern>>) {
    }

    override fun getCustomerById(Id: String, callback: OnResponseCallback<Customer>) {
    }

    override fun pinCodeCity(pinCode: Int, callback: OnResponseCallback<Pincode>) {
    }

    override fun addCustomerAddress(address: Address, callback: OnResponseCallback<Address>) {
    }

    override fun updateCustomerAddress(addressId: String, address: Address, callback: OnResponseCallback<Address>) {
    }

    override fun getMake(vehicleType: String?, callback: OnResponseCallback<List<Make>>) {
    }

    override fun getModel(makeSlug: String, callback: OnResponseCallback<List<Model>>) {
    }

    override fun addVehicle(vehicle: Vehicle, callback: OnResponseCallback<Vehicle>) {
    }

    override fun updateVehicle(vehicle: Vehicle, callback: OnResponseCallback<Vehicle>) {
    }

    override suspend fun updateVehicleVariantInfo(vehicleId: String, VehicleVariantBody: VehicleVariantBody): Result<NetworkPostResponse> {
        throw UnsupportedOperationException("local update vehicle not supported")
    }

    override fun getCompanyNames(callback: OnResponseCallback<List<InsuranceCompany>>) {
    }

    override fun getInsuranceCompanyAddresses(insuranceCompany: InsuranceCompany, callback: OnResponseCallback<List<InsuranceCompanyDetails>>) {
    }

    override fun getJobCards(types: List<String>, limit: Int, skip: Int, callback: OnResponseCallback<PaginationList<JobCard>>) {
    }

    override fun searchCustomerAndCarDetails(mobileNo: String, registrationNumber: String, callback: OnResponseCallback<CustomerVehicleSearch>) {
    }

    override fun newSearchApiCall(
        mobileNo: String,
        registrationNumber: String,
        callback: OnResponseCallback<ResponseBody>
    ) {
    }

    override fun updateCustomer(id: String, customer: Customer, address: Address, callback: OnResponseCallback<Customer>) {
    }

    override fun getInspectionGroups(vehicleType: String?, callback: OnResponseCallback<List<InspectionGroup>>) {
    }

    override fun getInspectionItemsByGroup(groupId: String, callback: OnResponseCallback<List<InspectionItem>>) {
    }

    override fun getJobsData(jobcardId: String, callback: OnResponseCallback<JobAndVerbatim>) {
    }

    override fun searchJobCards(search: String, query: String, callback: OnResponseCallback<List<JobCard>>) {
    }

    override fun getJobs(query: String, vehicleType: String?, callback: OnResponseCallback<List<RecommendedJob>>) {
    }

    override fun getParts(query: String, vehicleType: String?, vehicleAmcId: String?, callback: OnResponseCallback<List<Part>>) {
    }

    override fun getLabours(jobCardId: String, query: String, vehicleType: String?, vehicleAmcId: String?, callback: OnResponseCallback<List<Labour>>) {

    }

    override fun saveJobsData(jobCardId: String, obj: Jobs, callback: OnResponseCallback<NetworkPostResponse>) {
    }

    /*Damage Start*/



    override fun uploadUnSavedDamage(image: FileObject, callback: OnResponseCallback<FileObject>) {
    }



    /*Damage End*/

    // Invoices

    override fun getInvoiceById(invoiceId: String, callback: OnResponseCallback<Invoice>) {
    }

    override fun getInvoices(status: String, skip: Int, limit: Int, type: String?, callback: OnResponseCallback<PaginationList<Invoice>>) {
    }

    override fun cancelInvoice(invoiceId: String, reschedule: Reschedule, callback: OnResponseCallback<Invoice>) {
    }

    override fun searchInvoices(search: String, query: String, type: String, callback: OnResponseCallback<List<Invoice>>) {
    }

    // Parts
    override fun savePart(id: String, part: Part, callback: OnResponseCallback<Part>) {
    }

    override fun deletePart(invoiceId: String, partId: String, callback: OnResponseCallback<Invoice>) {
    }

    override fun fetchBrandName(query: String, jobCardId: String, partNumber: String?, vehicleType: String?, callback: OnResponseCallback<List<BrandName>>) {
    }

    // Labour
    override fun saveLabour(id: String, labour: Labour, callback: OnResponseCallback<Labour>) {
    }

    override fun deleteLabour(invoiceId: String, labourId: String, callback: OnResponseCallback<Invoice>) {
    }

    // pdf

    override fun getPrintEstimate(jobCardId: String, callback: OnResponseCallback<PDF>) {
    }

    override fun getPrintJobCardPdc(jobCardId: String, callback: OnResponseCallback<PDF>) {
        TODO("Not yet implemented")
    }

    override fun getProformaPDF(invoiceId: String, callback: OnResponseCallback<List<PDF>>) {
    }

    override fun createInvoice(invoiceId: String, notify: Boolean, reminderDate: String?, callback: OnResponseCallback<List<PDF>>) {
    }

    override fun getJobCardPreview(jobCardId: String, callback: OnResponseCallback<PDF>) {
    }

    override fun getInvoicePreview(invoiceId: String, callback: OnResponseCallback<Invoice>) {
    }

    override fun getGatePassPreview(jobCardId: String, callback: OnResponseCallback<PDF>) {
    }

    override fun getAmcInvoicePdf(vehicleAmcId: String, callback: OnResponseCallback<PDF>) {

    }

    // Appointment

    override fun getVehicleDetails(registrationNumber: String, callback: OnResponseCallback<List<Vehicle>>) {
    }

    override fun getCustomerDetails(mobileNumber: String, callback: OnResponseCallback<List<Customer>>) {
    }

    override fun saveDetails(appointment: Appointment, callback: OnResponseCallback<Appointment>) {
    }

    override fun saveAppointmentStatus(
        appointment: AppointmentStatus,
        callback: OnResponseCallback<Any>
    ) {
        TODO("Not yet implemented")
    }

    override fun getPackages(id: String, callback: OnResponseCallback<Packages>) {
    }

    override fun updatePackages(jobCardId: String, invoiceId: String, isInvoice: Boolean, packageIds: List<String>, callback: OnResponseCallback<List<ServicePackage>>) {
    }

    override fun savePackages(appointmentId: String, appointmentPost: AppointmentPost, callback: OnResponseCallback<Appointment>) {
    }

    override fun getTimeSlot(appointmentId: String, dateTime: String, type: String?, callback: OnResponseCallback<List<TimeSlot>>) {
    }

    override fun saveTimeSlot(appointmentId: String, dateTime: AppointmentPost, callback: OnResponseCallback<Appointment>) {
    }

    override fun cancelAppointment(appointmentId: String, callback: OnResponseCallback<Appointment>) {
    }

    override fun rescheduleAppointment(appointmentId: String, reschedule: Reschedule, callback: OnResponseCallback<Reschedule>) {
    }

    override fun rejectAppointment(appointmentId: String, reschedule: Reschedule, callback: OnResponseCallback<Reschedule>) {
    }

    override fun acceptAppointment(appointmentId: String, callback: OnResponseCallback<Appointment>) {
    }

    override fun getCustomerVehicleHistory(id: String, callback: OnResponseCallback<CustomerVehicleDetails>) {
    }

    override fun getCustomerVehicleList(query: String?, filterList: List<String>?, startDate: String?, endDate: String?, skip: Int, limit: Int, callback: OnResponseCallback<PaginationList<CustomerVehicleDetails>>) {
    }

    override fun saveRemarks(jobCardId: String, invoiceRemarks: InvoiceRemarks, callback: OnResponseCallback<JobCard>) {
    }

    override fun getRemarks(jobCardId: String, callback: OnResponseCallback<InvoiceRemarks>) {
    }

    override fun getWorkShopResources(callback: OnResponseCallback<WorkshopResource>) {
    }

    /*Accidental Start*/
    override fun saveAccidentalData(jobCardId: String, accidental: Accidental, callback: OnResponseCallback<JobCard>) {
    }

    override fun saveMissingAccidentalDetails(jobCardId: String, missingAccidentalDetails: MissingAccidentalDetails, callback: OnResponseCallback<JobCard>) {
    }

    override suspend fun uploadDocument(document: FileObject): Result<FileObject> {
        throw UnsupportedOperationException("upload document with suspend not supported yet")
    }

    /*Accidental End*/

    override fun saveLead(leadId: String, leadForm: LeadForm, callback: OnResponseCallback<LeadForm>) {
    }

    override fun saveServiceReminder(vehicleId: String, date: String, callback: OnResponseCallback<ServiceDate>) {
    }

    override fun addOtcProforma(customerId: String, addressId: String, vehicleType: String?, callback: OnResponseCallback<Invoice>) {
    }

    override fun getDashBoardDetails(callback: OnResponseCallback<WorkshopResource>) {
    }

    override fun saveSignature(jobCardId: String, file: File, callback: OnResponseCallback<Signature>) {
    }

    override fun cancelJobCard(jobCardId: String, callback: OnResponseCallback<NetworkPostResponse>) {
    }

    override fun saveCustomerFeedback(jobcardId: String, recommendedScore: Int, serviceQuality: Float, billingTransparency: Float, timelyDelivery: Float, comments: String?, callback: OnResponseCallback<NetworkPostResponse>) {
    }

    override fun getServiceAdvisors(workshopId: String, callback: OnResponseCallback<List<WorkshopAdviser>>) {
    }

    override fun registerDevice(deviceId: String, fcmTokenObj: FcmTokenEntity, callback: OnResponseCallback<NetworkPostResponse>) = Unit

    override fun getWhatsAppTemplate(templateType: String, id: String, callback: OnResponseCallback<WhatsAppTemplate>) = Unit

    override suspend fun saveThirdPartyDetails(invoiceId: String, thirdParty: ThirdParty): Result<ThirdParty> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun removeThirdPartyDetails(invoiceId: String, thirdParty: ThirdParty): Result<ThirdParty> {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getAMCList(
        types: String,
        limit: Int,
        skip: Int,
        query: String,
        callback: OnResponseCallback<PaginationList<AMC>>
    ) {
        TODO("Not yet implemented")
    }

    override fun getVehicleAMCById(vehicleAmcId: String?, callback: OnResponseCallback<List<AMC>>) {
        TODO("Not yet implemented")
    }

    override fun suggestAmcPackages(vehicleId: String, callback: OnResponseCallback<List<AMCPackage>>) {
        TODO("Not yet implemented")
    }

    override fun purchaseAMC(amcDetails: AMCPurchase, callback: OnResponseCallback<AMC>) {
        TODO("Not yet implemented")
    }

    override fun getOtherHistory(registrationNumber: String, mobileNumber: String, callback: OnResponseCallback<CustomerVehicleDetails>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRedemptionDetails(vehicleAmcId: String, callback: OnResponseCallback<SoldAMCDetails>)
    {
        TODO("Not yet implemented")
    }

    override fun cancelAMC(
        vehicleAmcId: String,
        reason: String,
        callback: OnResponseCallback<SoldAMCDetails>
    ) {
        TODO("Not yet implemented")
    }

    override fun getVendorList(callback: OnResponseCallback<List<Vendor>>) {
    }

    override fun getPdcChecklist(jobCardId: String, callback: OnResponseCallback<List<PdcBase>>) {
        TODO("Not yet implemented")
    }

    override fun postPdcChecklist(
        jobCardId: String,
        pdcEntity: PdcEntity,
        callback: OnResponseCallback<NetworkPostResponse>
    ) {
        TODO("Not yet implemented")
    }


    override fun uploadUnSavedDocument(image: FileObject, callback: OnResponseCallback<FileObject>) {
    }

    //REALM

    override fun saveDamageImage(image: FileObject, callback: OnResponseCallback<FileObject>) {
        val realm = Realm.getDefaultInstance()
        image.pid = UUID.randomUUID().toString()
        image.type = FileObject.FILE_TYPE_DAMAGE
        image.customScope = FileObject.FILE_TYPE_DAMAGE
        realm.executeTransaction {
            it.copyToRealm(image)
        }
        callback.onSuccess(image)
        realm.close()
    }

    override fun savePDCImage(image: FileObject, callback: OnResponseCallback<FileObject>) {
        val realm = Realm.getDefaultInstance()
        image.pid = UUID.randomUUID().toString()
        image.type = FileObject.FILE_TYPE_PDC
        image.customScope = FileObject.FILE_TYPE_PDC
        realm.executeTransaction {
            it.copyToRealm(image)
        }
        callback.onSuccess(image)
        realm.close()
    }

    override fun deleteDamageImage(damageImg: FileObject, callback: OnResponseCallback<FileObject?>?) {
        val realm = Realm.getDefaultInstance()
        realm.use { it ->
            it.executeTransaction { realmObj ->
                val data = realmObj.where(FileObject::class.java)
                    .equalTo(DatabaseConstants.COLUMN_FILE_PRIMARY_KEY, damageImg.pid)
                    .findFirst()
                data?.run {
                    this.isDeleted = true
                    realmObj.insertOrUpdate(this)
                }
                damageImg.uri?.let {
                    val fileToDelete = File(it)
                    if (fileToDelete.exists()) {
                        fileToDelete.delete()
                    }
                }
                val dat = realmObj.copyFromRealm(data!!)
                Timber.d("deleted data $dat")
                callback?.onSuccess(dat)
            }
        }
    }

    override fun saveDamageImages(jobCardId: String, imageList: List<FileObject>) {
        val realm = Realm.getDefaultInstance()
        realm.run {
            this.executeTransaction { realm ->
                imageList.forEach {
                    val oldCopy = realm.where(FileObject::class.java)
                        .equalTo(DatabaseConstants.COLUMN_FILE_IS_DELETED, false)
                        .equalTo(DatabaseConstants.COLUMN_FILE_ORIGINAL_NAME, it.originalName)
                        .equalTo(DatabaseConstants.COLUMN_FILE_TYPE, FileObject.FILE_TYPE_DAMAGE)
                        .findFirst()
                    Timber.d("Is Old Copy Present :$oldCopy")
                    oldCopy?.let { obj ->
                        obj.jobCardID = jobCardId
                        obj.isUploaded = true
                        obj.type = FileObject.FILE_TYPE_DAMAGE
                        realm.copyToRealmOrUpdate(obj)
                    } ?: run {
                        it.pid = UUID.randomUUID().toString()
                        it.jobCardID = jobCardId
                        it.isUploaded = true
                        it.type = FileObject.FILE_TYPE_DAMAGE
                        realm.copyToRealm(it)
                    }
                }
            }
            Timber.d("check closing saveDamageImages")
            this.close()
        }
    }

    override fun getDamages(jobcardId: String, sort: String?, callback: OnResponseCallback<List<FileObject>>) {
        Timber.d("Ting", "GG")
        val realm = Realm.getDefaultInstance()
        Timber.d("check opening getDamages")
        val imageData = realm.where(FileObject::class.java)
            .equalTo(DatabaseConstants.COLUMN_FILE_JOB_CARD_ID, jobcardId)
            .equalTo(DatabaseConstants.COLUMN_FILE_TYPE, FileObject.FILE_TYPE_DAMAGE)
            .equalTo(DatabaseConstants.COLUMN_CUSTOM_SCOPE, FileObject.FILE_TYPE_DAMAGE)
            .equalTo(DatabaseConstants.COLUMN_FILE_IS_DELETED, false)
            .isNotEmpty(DatabaseConstants.COLUMN_FILE_PRIMARY_KEY)
            .findAll()
        val list = realm.copyFromRealm(imageData)
        if (sort != null) {
            callback.onSuccess(list.filter { it.meta?.category == sort })
        } else {
            callback.onSuccess(list)
        }
        Timber.d("check clsoing getDamages")
        realm.close()
    }

    override fun updateDamageImage(originalFileName: String, imageId: String, url: String, isUploaded: Boolean, callback: OnResponseCallback<FileObject>?) {
        val realm = Realm.getDefaultInstance()
        Timber.d("check opening updateDamageImage")

        try {
            realm.executeTransaction {
                val data = it.where(FileObject::class.java).equalTo(DatabaseConstants.COLUMN_FILE_ORIGINAL_NAME, originalFileName).findFirst()
                Timber.d("is update row found", "$data")
                data?.let { damageImage ->
                    damageImage.id = imageId
                    damageImage.url = url
                    damageImage.isUploaded = isUploaded
                    val updatedCopy = it.copyToRealmOrUpdate(damageImage)
                    Timber.d("Damage updated copy $updatedCopy")
                    callback?.onSuccess(updatedCopy)
                } ?: run {
                    Timber.d("Damage updated error")
                    callback?.onError(errorWrapperHelper.transformToErrorWrapper(UnavailableException()))
                }

                // it.close()
            }
        } catch (e: ArrayIndexOutOfBoundsException) {
            realm.cancelTransaction()
        } catch (e: NullPointerException) {
            Timber.d("Listen", "Image Already Deleted")
        } finally {
            realm.close()
        }
    }

    override suspend fun saveDamageImage(jobCardId: String, image: FileObject): Result<FileObject> {
        val updatedCopy: FileObject = image
        return Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction {
                val oldCopy = realm.where(FileObject::class.java)
                    .equalTo(DatabaseConstants.COLUMN_FILE_IS_DELETED, false)
                    .equalTo(DatabaseConstants.COLUMN_FILE_ORIGINAL_NAME, image.originalName)
                    .equalTo(DatabaseConstants.COLUMN_FILE_TYPE, FileObject.FILE_TYPE_DAMAGE)
                    .equalTo(DatabaseConstants.COLUMN_CUSTOM_SCOPE, FileObject.FILE_TYPE_DAMAGE)
                    .findFirst()
                Timber.d("Is Old Copy Present :$oldCopy")
                oldCopy?.let { obj ->
                    obj.jobCardID = jobCardId
                    obj.isUploaded = true
                    obj.type = FileObject.FILE_TYPE_DAMAGE
                    obj.customScope = FileObject.FILE_TYPE_DAMAGE
                    realm.copyToRealmOrUpdate(obj)
                } ?: run {
                    image.pid = UUID.randomUUID().toString()
                    image.jobCardID = jobCardId
                    image.isUploaded = true
                    image.type = FileObject.FILE_TYPE_DAMAGE
                    image.customScope = FileObject.FILE_TYPE_DAMAGE
                    realm.copyToRealm(image)
                }
            }
            Result.Success(updatedCopy)
        }
    }

    override fun clearDamageCache() {
        val realm = Realm.getDefaultInstance()
        Timber.d("check opening clearDamageCache")
        realm.executeTransaction {
            val uploadedImages = it.where(FileObject::class.java)
                .equalTo(DatabaseConstants.COLUMN_FILE_TYPE, FileObject.FILE_TYPE_DAMAGE)
                .equalTo(DatabaseConstants.COLUMN_FILE_IS_UPLOADED, true)
                .findAll()
            Timber.d("clearDamageCache ${uploadedImages.size}")

            uploadedImages.deleteAllFromRealm()
        }
        Timber.d("check closing clearDamageCache")

        realm.close()
    }

    override fun updateCaption(caption: String, jobCardId: String, imageId: String, callback: OnResponseCallback<FileObject>) {
        val realm = Realm.getDefaultInstance()
        Timber.d("check opening updateCaption")

        realm.executeTransaction {
            val image = it.where(FileObject::class.java)
                .equalTo(DatabaseConstants.COLUMN_FILE_FILE_ID, imageId)
                .findFirst()
            image?.title = caption
            if (image != null) {
                callback.onSuccess(realm.copyToRealmOrUpdate(image))
            }
        }
        Timber.d("check closing updateCaption")
        realm.close()
    }

    override suspend fun getDamages(jobcardId: String, sort: String?): Result<List<FileObject>> {
        val realm=Realm.getDefaultInstance()

        try {
            return realm.use {
                val realmDamages = it.where(FileObject::class.java)
                    .equalTo(DatabaseConstants.COLUMN_FILE_JOB_CARD_ID, jobcardId)
                    .equalTo(DatabaseConstants.COLUMN_FILE_TYPE, FileObject.FILE_TYPE_DAMAGE)
                    .equalTo(DatabaseConstants.COLUMN_CUSTOM_SCOPE, FileObject.FILE_TYPE_DAMAGE)
                    .equalTo(DatabaseConstants.COLUMN_FILE_IS_DELETED, false)
                    .isNotEmpty(DatabaseConstants.COLUMN_FILE_PRIMARY_KEY)
                    .findAll()
                val damages = it.copyFromRealm(realmDamages)
                if (sort != null) {
                    Result.Success(damages.filter { it.meta?.category == sort })
                } else {
                    Result.Success(damages)
                }
            }
        }
        finally {
            realm.close()
        }


    }

    override suspend fun getPdcImages(jobcardId: String, sort: String?): Result<List<FileObject>> {
        val realm=Realm.getDefaultInstance()

        try {
            return realm.use {
                val realmDamages = it.where(FileObject::class.java)
                    .equalTo(DatabaseConstants.COLUMN_FILE_JOB_CARD_ID, jobcardId)
                    .equalTo(DatabaseConstants.COLUMN_FILE_TYPE, FileObject.FILE_TYPE_PDC)
                    .equalTo(DatabaseConstants.COLUMN_CUSTOM_SCOPE, FileObject.FILE_TYPE_PDC)
                    .equalTo(DatabaseConstants.COLUMN_FILE_IS_DELETED, false)
                    .isNotEmpty(DatabaseConstants.COLUMN_FILE_PRIMARY_KEY)
                    .findAll()
                val damages = it.copyFromRealm(realmDamages)
                if (sort != null) {
                    Result.Success(damages.filter { it.meta?.category == sort })
                } else {
                    Result.Success(damages)
                }
            }
        }
        finally {
            realm.close()
        }
    }

    override fun saveHSN(hsn: List<HSN>) {
        val realm = Realm.getDefaultInstance()
        Timber.d("check opening saveHSN")
        realm.executeTransaction {
            //   val hsnObj = hsn[items]
            // Timber.d(hsnObj.toString(), "saved")
            it.copyToRealm(hsn)
        }
        Timber.d("check closing saveHSN")
        realm.close()
    }

    override fun getHSN(callback: OnResponseCallback<List<HSN>>) {
        val realm = Realm.getDefaultInstance()
        Timber.d("check opening gethsn")
        val hsnList = realm.where(HSN::class.java).findAll()
        val list = realm.copyFromRealm(hsnList)
        callback.onSuccess(list)
        realm.close()
//        realm.executeTransaction {
//            Timber.d("Local Fetch")
//
//        }
        Timber.d("check closing gethsn")
        // realm.close()
    }

    override fun deleteHSN(callback: OnResponseCallback<Boolean>) {
        val realm = Realm.getDefaultInstance()
        Timber.d("check opening deleteHSN")

        realm.executeTransactionAsync(Realm.Transaction {
            val hsnList = it.where(HSN::class.java)?.findAll()
            Timber.d("deleting", hsnList.toString())
            hsnList?.deleteAllFromRealm()
        }, Realm.Transaction.OnSuccess {
            callback.onSuccess(true)
        })
        Timber.d("check closing deleteHSN")
        realm.close()
    }

    override fun uploadDocument(fileObject: FileObject, callback: OnResponseCallback<FileObject>) {
        val realm = Realm.getDefaultInstance()
        Timber.d("check opening uploadDocument")
        fileObject.pid = UUID.randomUUID().toString()
        realm.executeTransaction {
            it.copyToRealm(fileObject)
        }
        callback.onSuccess(fileObject)
        Timber.d("check closing uploadDocument")
        realm.close()
    }

    override fun updateDocument(originalFileName: String, imageId: String, url: String, isUploaded: Boolean, callback: OnResponseCallback<FileObject>?) {
        val realm = Realm.getDefaultInstance()
        Timber.d("check opening updateDocument")
        try {
            realm.executeTransaction {
                val data = it.where(FileObject::class.java).equalTo(DatabaseConstants.COLUMN_FILE_ORIGINAL_NAME, originalFileName).findFirst()
                Timber.d("File Found", "$data")
                data?.let { damageImage ->
                    damageImage.id = imageId
                    damageImage.url = url
                    damageImage.isUploaded = true
                    it.copyToRealmOrUpdate(damageImage)
                }
                callback?.onSuccess(data!!)
            }
        } catch (e: ArrayIndexOutOfBoundsException) {
            realm.cancelTransaction()
        } catch (e: NullPointerException) {
            Timber.e("Error", "File Already Deleted")
        } finally {
            Timber.d("check closing updateDocument")
            realm.close()
        }
    }

    override fun getDocuments(jobCardId: String, callback: OnResponseCallback<List<FileObject>>) {
        Timber.d("Get Documents from Local")

        val realm = Realm.getDefaultInstance()
        Timber.d("check opening getDocuments")
        val imageData = realm.where(FileObject::class.java)
            .equalTo(DatabaseConstants.COLUMN_FILE_TYPE, FileObject.FILE_TYPE_ACCIDENTAL)
            .equalTo(DatabaseConstants.COLUMN_FILE_IS_DELETED, false)
            .isNotEmpty(DatabaseConstants.COLUMN_FILE_PRIMARY_KEY)
            .isNotEmpty(DatabaseConstants.COLUMN_FILE_FILE_ID)
            .equalTo(DatabaseConstants.COLUMN_FILE_JOB_CARD_ID, jobCardId)
            .findAll()
        val list = realm.copyFromRealm(imageData)
        // list.addAll(imageData)
        callback.onSuccess(list)
        Timber.d("check closing getDocuments")
        realm.close()
    }

    override fun saveDocuments(jobCardId: String, files: List<FileObject>) {
        val realm = Realm.getDefaultInstance()
        realm.run {
            this.executeTransaction { realm ->
                files.forEach {
                    val oldCopy = realm.where(FileObject::class.java)
                        .equalTo(DatabaseConstants.COLUMN_FILE_IS_DELETED, false)
                        .equalTo(DatabaseConstants.COLUMN_FILE_FILE_ID, it.id)
                        .isNotEmpty(DatabaseConstants.COLUMN_FILE_ORIGINAL_NAME)
                        .findFirst()
                    if (oldCopy == null) {
                        it.pid = UUID.randomUUID().toString()
                        it.jobCardID = jobCardId
                        it.isUploaded = true
                        realm.copyToRealm(it)
                    }
                }
            }
            this.close()
        }
    }

    override fun deleteDocument(fileObject: FileObject, callback: OnResponseCallback<FileObject>) {
        val realm = Realm.getDefaultInstance()
        Timber.d("check opening deleteDocument")
        realm.executeTransaction {
            val data = it.where(FileObject::class.java)
                .equalTo(DatabaseConstants.COLUMN_FILE_PRIMARY_KEY, fileObject.pid)
                .findFirst()
            data?.run {
                this.isDeleted = true
                it.insertOrUpdate(this)
            }
        }
        callback.onSuccess(fileObject)
//        fileObject.uri?.let {
//            val fileToDelete = File(it)
//            if (fileToDelete.exists()) {
//                fileToDelete.delete()
//            }
//        }
        Timber.d("check closing deleteDocument")
        realm.close()
    }

    override fun clearAccidentalCache() {
        val realm = Realm.getDefaultInstance()
        Timber.d("check opening clearAccidentalCache")
        realm.executeTransaction {
            val uploadedImages = it.where(FileObject::class.java)
                .equalTo(DatabaseConstants.COLUMN_FILE_TYPE, "ACCIDENTAL")
                .equalTo(DatabaseConstants.COLUMN_FILE_IS_UPLOADED, true).findAll()
            uploadedImages.deleteAllFromRealm()
        }
        Timber.d("check closing clearAccidentalCache")
        realm.close()
    }

    override fun fetchUnSavedImages(callback: OnResponseCallback<List<FileObject>>) {
        val realm = Realm.getDefaultInstance()
        Timber.d("check opening fetchUnSavedImages")

        val imageData = realm.where(FileObject::class.java)
            .equalTo(DatabaseConstants.COLUMN_FILE_IS_UPLOADED, false)
            .equalTo(DatabaseConstants.COLUMN_FILE_IS_DELETED, false)
            .isNotEmpty(DatabaseConstants.COLUMN_FILE_PRIMARY_KEY)
            .findAll()
        val list = realm.copyFromRealm(imageData)
        // list.addAll(imageData)
        callback.onSuccess(list)
        Timber.d("check closing fetchUnSavedImages")
        realm.close()
    }

    override fun getStates(callback: OnResponseCallback<List<State>>) {
        Realm.getDefaultInstance().use {
            val realmStates = it.where(State::class.java).findAll()
            val states = it.copyFromRealm(realmStates)
            callback.onSuccess(states)
        }
    }

    override fun saveStates(states: List<State>, callback: OnResponseCallback<List<State>>?) {
        Realm.getDefaultInstance().use { realm ->
            realm.executeTransaction() {
                it.copyToRealm(states)
            }
        }
    }

    override fun saveCustomerGroup(options: List<Option>) {
        val realm = Realm.getDefaultInstance()
        Timber.d("check opening options")
        realm.executeTransaction {
            it.copyToRealmOrUpdate(options)
        }
        Timber.d("check closing options")
        realm.close()
    }

    override fun getCustomerGroup(callback: OnResponseCallback<List<Option>>) {
        Realm.getDefaultInstance().use {
            val realmOptions = it.where(Option::class.java).findAll()
            val options = it.copyFromRealm(realmOptions)
            callback.onSuccess(options)
        }
    }

    override fun deleteCustomerGroup(callback: OnResponseCallback<Boolean>) {
        val realm = Realm.getDefaultInstance()
        Timber.d("check opening delete Option")
        realm.executeTransactionAsync(Realm.Transaction {
            val optionList = it.where(Option::class.java)?.findAll()
            Timber.d("deleting", optionList.toString())
            optionList?.deleteAllFromRealm()
        }, Realm.Transaction.OnSuccess {
            callback.onSuccess(true)
        })
        Timber.d("check closing delete Customer")
        realm.close()
    }

    override fun updatePayment(
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
    ) {
        TODO("Not yet implemented")
    }

    override fun getSourceTypes(callback: OnResponseCallback<List<CustomerSourceType>>) {
        TODO("Not yet implemented")
    }

    override fun getSourcesById(
        sourceId: String,
        callback: OnResponseCallback<List<CustomerSource>>
    ) {
        TODO("Not yet implemented")
    }

    override fun getReceiptDetails(invoiceId: String, callback: OnResponseCallback<List<Payment>>) {
        TODO("Not yet implemented")
    }

    override fun getReceiptPdf(receiptId: String, callback: OnResponseCallback<PDF>) {
        TODO("Not yet implemented")
    }
}

