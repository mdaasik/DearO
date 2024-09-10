package com.carworkz.dearo.data

import com.carworkz.dearo.domain.entities.PdcBase
import com.carworkz.dearo.domain.entities.CustomerAndAddress
import com.carworkz.dearo.amc.AMCPurchase
import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.helpers.PaginationList
import com.carworkz.dearo.domain.entities.PdcEntity
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject


class DearODataRepository @Inject
constructor(@param:Local private val localDataSource: DataSource, @param:Remote private val networkDataSource: DataSource) : DataSource {

    override fun sendOtp(mobileNo: String, callback: DataSource.OnResponseCallback<NetworkPostResponse>) {
        networkDataSource.sendOtp(mobileNo, callback)
    }

    override fun loginUser(mobileNo: String, otpcode: Int, callback: DataSource.OnResponseCallback<User>) {
        networkDataSource.loginUser(mobileNo, otpcode, callback)
    }

    override fun logoutUser(callback: DataSource.OnResponseCallback<NetworkPostResponse?>) {
        networkDataSource.logoutUser(object : DataSource.OnResponseCallback<NetworkPostResponse?> {
            override fun onSuccess(obj: NetworkPostResponse?) {
                SharedPrefHelper.logout()
                callback.onSuccess(obj)
            }

            override fun onError(error: ErrorWrapper) {
                SharedPrefHelper.logout()
                callback.onError(error)
            }
        })
    }

    override fun saveVoice(jobCardId: String, verbatim: Verbatim, callback: DataSource.OnResponseCallback<Verbatim>) {
        networkDataSource.saveVoice(jobCardId, verbatim, callback)
    }

    override fun getInventory(vehicleType: String?, callback: DataSource.OnResponseCallback<List<Inventory>>) {
        networkDataSource.getInventory(vehicleType, callback)
    }

    override fun initiateJobCard(customerId: String, vehicleId: String, appointmentId: String?, vehicleAmcId: String?, type: String, callback: DataSource.OnResponseCallback<JobCard>) {
        networkDataSource.initiateJobCard(customerId, vehicleId, appointmentId, vehicleAmcId,type, callback)

    }
    override fun saveJobCardInventory(jobCardId: String, serviceType: String, fuelReading: Float, kmsReading: Int, inventory: List<Inventory>, remarks: String?, callback: DataSource.OnResponseCallback<NetworkPostResponse>) {
        networkDataSource.saveJobCardInventory(jobCardId, serviceType, fuelReading, kmsReading, inventory, remarks, callback)
    }

    override fun pinCodeCity(pinCode: Int, callback: DataSource.OnResponseCallback<Pincode>) {
        networkDataSource.pinCodeCity(pinCode, callback)
    }

    override fun addCustomer(customer: CustomerAndAddress, callback: DataSource.OnResponseCallback<CustomerAndAddressResponse>) {
        networkDataSource.addCustomer(customer, callback)
    }

    override fun getCustomerById(Id: String, callback: DataSource.OnResponseCallback<Customer>) {
        networkDataSource.getCustomerById(Id, callback)
    }

    override fun addCustomerAddress(address: Address, callback: DataSource.OnResponseCallback<Address>) {
        networkDataSource.addCustomerAddress(address, callback)
    }

    override fun updateCustomerAddress(addressId: String, address: Address, callback: DataSource.OnResponseCallback<Address>) {
        networkDataSource.updateCustomerAddress(addressId, address, callback)
    }

    override fun getCustomerConcernSuggestions(query: String, callback: DataSource.OnResponseCallback<List<CustomerConcern>>) {
        networkDataSource.getCustomerConcernSuggestions(query, callback)
    }

    override fun getMake(vehicleType: String?, callback: DataSource.OnResponseCallback<List<Make>>) {
        networkDataSource.getMake(vehicleType, callback)
    }

    override fun getModel(makeSlug: String, callback: DataSource.OnResponseCallback<List<Model>>) {
        networkDataSource.getModel(makeSlug, callback)
    }

    override fun getVariant(modelSlug: String, fuelType: String?, callback: DataSource.OnResponseCallback<List<Variant>>) {
        networkDataSource.getVariant(modelSlug, fuelType, callback)
    }

    override fun addVehicle(vehicle: Vehicle, callback: DataSource.OnResponseCallback<Vehicle>) {
        networkDataSource.addVehicle(vehicle, callback)
    }

    override fun updateVehicle(vehicle: Vehicle, callback: DataSource.OnResponseCallback<Vehicle>) {
        networkDataSource.updateVehicle(vehicle, callback)
    }

    override suspend fun updateVehicleVariantInfo(vehicleId: String, VehicleVariantBody: VehicleVariantBody): Result<NetworkPostResponse> {
        return networkDataSource.updateVehicleVariantInfo(vehicleId, VehicleVariantBody)
    }

    override fun getCompanyNames(callback: DataSource.OnResponseCallback<List<InsuranceCompany>>) {
        networkDataSource.getCompanyNames(callback)
    }

    override fun getInsuranceCompanyAddresses(insuranceCompany: InsuranceCompany, callback: DataSource.OnResponseCallback<List<InsuranceCompanyDetails>>) {
        networkDataSource.getInsuranceCompanyAddresses(insuranceCompany, callback)
    }

    override fun getJobCards(types: List<String>, limit: Int, skip: Int, callback: DataSource.OnResponseCallback<PaginationList<JobCard>>) {
        networkDataSource.getJobCards(types, limit, skip, callback)
    }

    override fun searchJobCards(search: String, query: String, callback: DataSource.OnResponseCallback<List<JobCard>>) {
        networkDataSource.searchJobCards(search, query, callback)
    }

    override fun cancelJobCard(jobCardId: String, callback: DataSource.OnResponseCallback<NetworkPostResponse>) {
        networkDataSource.cancelJobCard(jobCardId, callback)
    }

    //new search api call
    override fun newSearchApiCall(
        mobileNo: String,
        registrationNumber: String,
        callback: DataSource.OnResponseCallback<ResponseBody>
    ) {
        networkDataSource.newSearchApiCall(mobileNo,registrationNumber,callback)
    }

    override fun searchCustomerAndCarDetails(mobileNo: String, registrationNumber: String, callback: DataSource.OnResponseCallback<CustomerVehicleSearch>) {
        networkDataSource.searchCustomerAndCarDetails(mobileNo, registrationNumber, callback)
    }

    override fun addAlternate(phone: String, customerId: String, callback: DataSource.OnResponseCallback<Customer>) {
        networkDataSource.addAlternate(phone, customerId, callback)
    }

    override fun updateNumber(phone: String, customerId: String, callback: DataSource.OnResponseCallback<Customer>) {
        networkDataSource.updateNumber(phone, customerId, callback)
    }

    override fun updateCustomer(id: String, customer: Customer, address: Address, callback: DataSource.OnResponseCallback<Customer>) {
        networkDataSource.updateCustomer(id, customer, address, callback)
    }


    override fun getInspectionGroups(vehicleType: String?, callback: DataSource.OnResponseCallback<List<InspectionGroup>>) {
        networkDataSource.getInspectionGroups(vehicleType, callback)
    }

    override fun getInspectionItemsByGroup(groupId: String, callback: DataSource.OnResponseCallback<List<InspectionItem>>) {
        networkDataSource.getInspectionItemsByGroup(groupId, callback)
    }

    override fun saveInspection(jobcardId: String, inspectionPostPOJO: InspectionPostPOJO, callback: DataSource.OnResponseCallback<NetworkPostResponse>) {
        networkDataSource.saveInspection(jobcardId, inspectionPostPOJO, callback)
    }

    override fun getJobsData(jobcardId: String, callback: DataSource.OnResponseCallback<JobAndVerbatim>) {
        networkDataSource.getJobsData(jobcardId, callback)
    }

    override fun getJobs(query: String, vehicleType: String?, callback: DataSource.OnResponseCallback<List<RecommendedJob>>) {
        networkDataSource.getJobs(query, vehicleType, callback)
    }

    override fun getParts(query: String, vehicleType: String?,vehicleAmcId: String?, callback: DataSource.OnResponseCallback<List<Part>>) {
        networkDataSource.getParts(query, vehicleType,vehicleAmcId, callback)
    }

    override fun getLabours(jobCardId: String, query: String, vehicleType: String?,vehicleAmcId: String?, callback: DataSource.OnResponseCallback<List<Labour>>) {
        networkDataSource.getLabours(jobCardId, query, vehicleType,vehicleAmcId, callback)
    }

    override fun saveJobsData(jobCardId: String, obj: Jobs, callback: DataSource.OnResponseCallback<NetworkPostResponse>) {
        networkDataSource.saveJobsData(jobCardId, obj, callback)
    }

    override fun getJobCardDetails(jobcardId: String, includes: Array<String>, callback: DataSource.OnResponseCallback<JobCard>) {
        networkDataSource.getJobCardDetails(jobcardId, includes, callback)
    }

    override fun getVoiceFromJobCard(jobCardId: String, callback: DataSource.OnResponseCallback<Verbatim?>) {
        networkDataSource.getVoiceFromJobCard(jobCardId, callback)
    }

    override fun getJobCardById(jobCardId: String, include: ArrayList<String>?, callback: DataSource.OnResponseCallback<JobCard>) {
        networkDataSource.getJobCardById(jobCardId, include, callback)
    }

    /*damages start*/
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun saveDamageImage(image: FileObject, callback: DataSource.OnResponseCallback<FileObject>) {
        localDataSource.saveDamageImage(image, object : DataSource.OnResponseCallback<FileObject> {
            override fun onSuccess(obj: FileObject) {
                callback.onSuccess(obj)
                networkDataSource.saveDamageImage(image, object : DataSource.OnResponseCallback<FileObject> {
                    override fun onSuccess(fileObject: FileObject) {
                        localDataSource.updateDamageImage(image.originalName, Objects.requireNonNull<String>(fileObject.id), Objects.requireNonNull<String>(fileObject.url), true, object : DataSource.OnResponseCallback<FileObject> {
                            override fun onSuccess(obj: FileObject) {
                                val storage = File(Objects.requireNonNull<String>(obj.uri))
                                storage.delete()
                            }

                            override fun onError(error: ErrorWrapper) {
                                callback.onError(error)
                            }
                        })
                    }

                    override fun onError(error: ErrorWrapper) {
                        callback.onError(error)
                    }
                })
            }

            override fun onError(error: ErrorWrapper) {
                callback.onError(error)
            }
        })
    }

    override fun savePDCImage(
        image: FileObject,
        callback: DataSource.OnResponseCallback<FileObject>
    ) {
        localDataSource.savePDCImage(image, object : DataSource.OnResponseCallback<FileObject> {
            override fun onSuccess(obj: FileObject) {
                callback.onSuccess(obj)
                networkDataSource.savePDCImage(image, object : DataSource.OnResponseCallback<FileObject> {
                    override fun onSuccess(obj: FileObject) {
                        localDataSource.updateDamageImage(image.originalName, Objects.requireNonNull<String>(obj.id), Objects.requireNonNull<String>(obj.url), true, object : DataSource.OnResponseCallback<FileObject> {
                            override fun onSuccess(obj: FileObject) {
                                val storage = File(Objects.requireNonNull<String>(obj.uri))
                                storage.delete()
                            }

                            override fun onError(error: ErrorWrapper) {
                                callback.onError(error)
                            }
                        })
                    }

                    override fun onError(error: ErrorWrapper) {
                        callback.onError(error)
                    }
                })
            }

            override fun onError(error: ErrorWrapper) {
                callback.onError(error)
            }
        })
    }

    override fun deleteDamageImage(damageImg: FileObject, callback: DataSource.OnResponseCallback<FileObject?>?) {
        if (damageImg.isUploaded) {
            networkDataSource.deleteDamageImage(damageImg, object : DataSource.OnResponseCallback<FileObject?> {
                override fun onSuccess(obj: FileObject?) {
                    localDataSource.deleteDamageImage(damageImg, callback)
                }

                override fun onError(error: ErrorWrapper) {
                    localDataSource.deleteDamageImage(damageImg, callback)
                }
            })
        } else {
            localDataSource.deleteDamageImage(damageImg, callback)
        }
    }

    override fun updateDamageImage(originalFileName: String, imageId: String, url: String, isUploaded: Boolean, callback: DataSource.OnResponseCallback<FileObject>?) {
        localDataSource.updateDamageImage(originalFileName, imageId, url, isUploaded, callback)
    }

    override fun saveDamageImages(jobCardId: String, imageList: List<FileObject>) {
        Timber.d("Saving damages" + imageList.size)
        localDataSource.saveDamageImages(jobCardId, imageList)
    }

    override fun updateCaption(caption: String, jobCardId: String, imageId: String, callback: DataSource.OnResponseCallback<FileObject>) {
        localDataSource.updateCaption(caption, jobCardId, imageId, object : DataSource.OnResponseCallback<FileObject> {
            override fun onSuccess(obj: FileObject) {
                callback.onSuccess(obj)
                if (obj.isUploaded) {
                    networkDataSource.updateCaption(caption, jobCardId, imageId, object : DataSource.OnResponseCallback<FileObject> {
                        override fun onSuccess(obj: FileObject) {
                        }

                        override fun onError(error: ErrorWrapper) {
                            // callback.onError(error)
                        }
                    })
                }
            }

            override fun onError(error: ErrorWrapper) {
                callback.onError(error)
            }
        })
    }

    override fun clearDamageCache() {
        localDataSource.clearDamageCache()
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun getDamages(jobcardId: String, sort: String?, callback: DataSource.OnResponseCallback<List<FileObject>>) {
        Timber.d("get Damages calling Local")
        // getDamagesFromLocal(jobcardId, sort, callback)
        networkDataSource.getDamages(jobcardId, sort, object : DataSource.OnResponseCallback<List<FileObject>> {
            override fun onSuccess(nwklist: List<FileObject>) {
                Timber.d("get Damages calling Network")
                if (nwklist.isNotEmpty()) {
                    for (x in nwklist.indices) {
                        nwklist[x].jobCardID = jobcardId
                        nwklist[x].type = FileObject.FILE_TYPE_DAMAGE
                    }
                    saveDamageImages(jobcardId, nwklist)
                }
                getDamagesFromLocal(jobcardId, sort, callback)
            }

            override fun onError(error: ErrorWrapper) {
                getDamagesFromLocal(jobcardId, sort, callback)
            }
        })
    }

    override fun fetchUnSavedImages(callback: DataSource.OnResponseCallback<List<FileObject>>) {
        localDataSource.fetchUnSavedImages(callback)
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun uploadUnSavedDamage(image: FileObject, callback: DataSource.OnResponseCallback<FileObject>) {
        networkDataSource.uploadUnSavedDamage(image, object : DataSource.OnResponseCallback<FileObject> {
            override fun onSuccess(fileObject: FileObject) {
                localDataSource.updateDamageImage(image.originalName, Objects.requireNonNull<String>(fileObject.id), Objects.requireNonNull<String>(fileObject.url), true, object : DataSource.OnResponseCallback<FileObject> {
                    override fun onSuccess(obj: FileObject) {
                        val storage = File(Objects.requireNonNull<String>(obj.uri))
                        storage.delete()
                    }

                    override fun onError(error: ErrorWrapper) {
                        callback.onError(error)
                    }
                })
            }

            override fun onError(error: ErrorWrapper) {
                callback.onError(error)
            }
        })
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun uploadUnSavedDocument(image: FileObject, callback: DataSource.OnResponseCallback<FileObject>) {
        networkDataSource.uploadUnSavedDocument(image, object : DataSource.OnResponseCallback<FileObject> {
            override fun onSuccess(fileObject: FileObject) {
                localDataSource.updateDocument(image.originalName, Objects.requireNonNull<String>(fileObject.id), Objects.requireNonNull<String>(fileObject.url), true, object : DataSource.OnResponseCallback<FileObject> {
                    override fun onSuccess(obj: FileObject) {
                        val storage = File(Objects.requireNonNull<String>(obj.uri))
                        storage.delete()
                    }

                    override fun onError(error: ErrorWrapper) {
                        callback.onError(error)
                    }
                })
            }

            override fun onError(error: ErrorWrapper) {
                callback.onError(error)
            }
        })
    }

    private fun getDamagesFromLocal(id: String, sort: String?, callback: DataSource.OnResponseCallback<List<FileObject>>) {
        localDataSource.getDamages(id, sort, object : DataSource.OnResponseCallback<List<FileObject>> {
            override fun onSuccess(obj: List<FileObject>) {
                Timber.d("get Damages", "onSuccess: Result for Local " + obj.size)
                callback.onSuccess(obj)
            }

            override fun onError(error: ErrorWrapper) {
                callback.onError(error)
            }
        })
    }

    override suspend fun saveDamageImage(jobCardId: String, image: FileObject): Result<FileObject> {
        val result = networkDataSource.saveDamageImage(jobCardId, image)
        if (result is Result.Success) {
            val savedImage = result.data
            localDataSource.updateDamageImage(savedImage.originalName, requireNotNull(savedImage.id) { "saved image id is null $savedImage" }, requireNotNull(savedImage.url) { "saved image url is null $savedImage" }, true, null)
        }
        return result
    }

    override suspend fun getDamages(jobcardId: String, sort: String?): Result<List<FileObject>> {
        val result = networkDataSource.getDamages(jobcardId, sort)
        return if (result is Result.Success) {
            result.data.forEach {
                Timber.d("adding damage image ${it.caption}")
                localDataSource.saveDamageImage(jobcardId, it)
            }
            Timber.d("adding damage image from local")
            localDataSource.getDamages(jobcardId, sort)
        } else {
            Timber.d("adding damage image from local")
            localDataSource.getDamages(jobcardId, sort)
        }
    }

    override suspend fun getPdcImages(jobcardId: String, sort: String?): Result<List<FileObject>> {
        val result = networkDataSource.getPdcImages(jobcardId, sort)
        return if (result is Result.Success) {
            result.data.forEach {
                Timber.d("adding PDC image ${it.caption}")
                localDataSource.saveDamageImage(jobcardId, it)
            }
            Timber.d("adding PDC image from local")
            localDataSource.getPdcImages(jobcardId, sort)
        } else {
            Timber.d("adding PDC image from local")
            localDataSource.getPdcImages(jobcardId, sort)
        }
    }

    /*damages end*/

    // Invoices

    override fun getInvoices(status: String, skip: Int, limit: Int, type: String?, callback: DataSource.OnResponseCallback<PaginationList<Invoice>>) {
        networkDataSource.getInvoices(status, skip, limit, type, callback)
    }

    override fun cancelInvoice(invoiceId: String, reschedule: Reschedule, callback: DataSource.OnResponseCallback<Invoice>) {
        networkDataSource.cancelInvoice(invoiceId, reschedule, callback)
    }

    override fun getInvoiceById(invoiceId: String, callback: DataSource.OnResponseCallback<Invoice>) {
        networkDataSource.getInvoiceById(invoiceId, callback)
    }

    override fun getInvoiceDetailsById(invoiceId: String, callback: DataSource.OnResponseCallback<Invoice>) {
        networkDataSource.getInvoiceDetailsById(invoiceId, callback)
    }

    override fun searchInvoices(search: String, query: String, type: String, callback: DataSource.OnResponseCallback<List<Invoice>>) {
        networkDataSource.searchInvoices(search, query, type, callback)
    }

    // Parts

    override fun fetchBrandName(query: String, jobCardId: String, partNumber: String?, vehicleType: String?, callback: DataSource.OnResponseCallback<List<BrandName>>) {
        networkDataSource.fetchBrandName(query, jobCardId, partNumber, vehicleType, callback)
    }

    override fun fetchPartNumber(query: String, partId: String?, jobCardId: String?, brandId: String?, vehicleType: String?, callback: DataSource.OnResponseCallback<List<PartNumber>>) {
        networkDataSource.fetchPartNumber(query, partId, jobCardId, brandId, vehicleType, callback)
    }

    override fun searchPartNumber(query: String?, partId: String?, jobCardId: String?, make: String?, model: String?, variant: String?, fuelType: String?, showStock: Boolean, vehicleType: String?, filterMode: String,packageId: String?, callback: DataSource.OnResponseCallback<List<PartNumber>>) {
        networkDataSource.searchPartNumber(query, partId, jobCardId, make, model, variant, fuelType, showStock, vehicleType, filterMode,packageId, callback)
    }

    override fun searchInStockPartNumbers(query: String, jobCardId: String?, partId: String?, brandId: String?, vehicleType: String?,packageId:String?, callback: DataSource.OnResponseCallback<List<PartNumber>>) {
        networkDataSource.searchInStockPartNumbers(query, jobCardId, partId, brandId, vehicleType,packageId, callback)
    }

    override fun savePart(id: String, part: Part, callback: DataSource.OnResponseCallback<Part>) {
        networkDataSource.savePart(id, part, callback)
    }

    override fun deletePart(invoiceId: String, partId: String, callback: DataSource.OnResponseCallback<Invoice>) {
        networkDataSource.deletePart(invoiceId, partId, callback)
    }

    // Labour

    override fun saveLabour(id: String, labour: Labour, callback: DataSource.OnResponseCallback<Labour>) {
        networkDataSource.saveLabour(id, labour, callback)
    }

    override fun deleteLabour(invoiceId: String, labourId: String, callback: DataSource.OnResponseCallback<Invoice>) {
        networkDataSource.deleteLabour(invoiceId, labourId, callback)
    }

    override fun saveJobCardEstimate(jobCardId: String, dateTime: String, minEstimate: Int, maxEstimate: Int, status: String?, notify: Boolean, reasonForDelay: String?, callback: DataSource.OnResponseCallback<NetworkPostResponse>)
    {
        networkDataSource.saveJobCardEstimate(jobCardId, dateTime, minEstimate, maxEstimate, status, notify,reasonForDelay, callback)
    }

    override fun completeJobCard(jobCardId: String, notify: Boolean,reasonForDelay: String?, callback: DataSource.OnResponseCallback<NetworkPostResponse>) {
        networkDataSource.completeJobCard(jobCardId, notify,reasonForDelay, callback)
    }

    override fun closeJobCard(jobCardId: String, reminderDate: String?, notify: Boolean, callback: DataSource.OnResponseCallback<NetworkPostResponse>) {
        networkDataSource.closeJobCard(jobCardId, reminderDate, notify, callback)
    }

    // pdf

    override fun getPrintEstimate(jobCardId: String, callback: DataSource.OnResponseCallback<PDF>) {
        networkDataSource.getPrintEstimate(jobCardId, callback)
    }

    override fun getPrintJobCardPdc(
        jobCardId: String,
        callback: DataSource.OnResponseCallback<PDF>
    ) {
        networkDataSource.getPrintJobCardPdc(jobCardId, callback)
    }

    override fun getProformaPDF(invoiceId: String, callback: DataSource.OnResponseCallback<List<PDF>>) {
        networkDataSource.getProformaPDF(invoiceId, callback)
    }

    override fun createInvoice(invoiceId: String, notify: Boolean, reminderDate: String?, callback: DataSource.OnResponseCallback<List<PDF>>) {
        networkDataSource.createInvoice(invoiceId, notify, reminderDate, callback)
    }

    override fun getJobCardPreview(jobCardId: String, callback: DataSource.OnResponseCallback<PDF>) {
        networkDataSource.getJobCardPreview(jobCardId, callback)
    }

    override fun getInvoicePreview(invoiceId: String, callback: DataSource.OnResponseCallback<Invoice>) {
        networkDataSource.getInvoicePreview(invoiceId, callback)
    }

    override fun getGatePassPreview(jobCardId: String, callback: DataSource.OnResponseCallback<PDF>) {
        networkDataSource.getGatePassPreview(jobCardId, callback)
    }

    override fun getAmcInvoicePdf(vehicleAmcId: String, callback: DataSource.OnResponseCallback<PDF>) {
        networkDataSource.getAmcInvoicePdf(vehicleAmcId,callback)
    }

    override suspend fun getInvoicePdfs(invoiceId: String): Result<List<PDF>> {
        return networkDataSource.getInvoicePdfs(invoiceId)
    }

    // Splash Screen
    override fun getUserConfig(callback: DataSource.OnResponseCallback<User>) {
        networkDataSource.getUserConfig(object : DataSource.OnResponseCallback<User>{
            override fun onSuccess(obj: User) {
                if(obj.customerGroup!=null)
                saveCustomerGroup(obj.customerGroup.options)
                callback.onSuccess(obj)
            }

            override fun onError(error: ErrorWrapper) {
               callback.onError(error)
            }

        })
    }

    override fun checkForceUpdate(appName: String, platform: String, versionCode: Int, callback: DataSource.OnResponseCallback<AppUpdate>?) {
        networkDataSource.checkForceUpdate(appName, platform, versionCode, object : DataSource.OnResponseCallback<AppUpdate> {
            override fun onSuccess(obj: AppUpdate) {
                Timber.d("Force update " + obj.forceUpdate!!)
                SharedPrefHelper.setVersionCode(obj.versionCode!!)
                SharedPrefHelper.setForceUpdate(obj.forceUpdate!!)
                SharedPrefHelper.setMinimumVersion(obj.minVersionCode!!)
                SharedPrefHelper.setOptionalUpdate(obj.updateAvailable!!)
                SharedPrefHelper.setForceUpdateText(obj.forceText)
                SharedPrefHelper.setForceUpdateLink(obj.link)
                callback?.onSuccess(obj)
            }

            override fun onError(error: ErrorWrapper) {
                //                Timber.d("Force update error " + error.getErrorMessage());                if (callback != null)
                callback?.onError(error)
            }
        })
    }

    override fun getHSN(callback: DataSource.OnResponseCallback<List<HSN>>) {
        localDataSource.getHSN(object : DataSource.OnResponseCallback<List<HSN>> {
            override fun onSuccess(obj: List<HSN>) {
                if (obj.isNotEmpty())
                {
                    if (SharedPrefHelper.isCompositeEnabled()) {
                        obj.forEach {
                            it.cgst = 3.0
                            it.sgst = 3.0
                        }
                    }
                    callback.onSuccess(obj)
                } else {
                    networkDataSource.getHSN(object : DataSource.OnResponseCallback<List<HSN>> {
                        override fun onSuccess(obj: List<HSN>)
                        {
                            if (SharedPrefHelper.isCompositeEnabled()) {
                                obj.forEach {
                                    it.cgst = 3.0
                                    it.sgst = 3.0
                                }
                            }
                            callback.onSuccess(obj)
                            saveHSN(obj)
                        }

                        override fun onError(error: ErrorWrapper) {
                            callback.onError(error)
                        }
                    })
                }
            }

            override fun onError(error: ErrorWrapper) {
                callback.onError(error)
            }
        })
    }

    override fun deleteHSN(callback: DataSource.OnResponseCallback<Boolean>) {
        localDataSource.deleteHSN(callback)
    }

    override fun saveHSN(hsn: List<HSN>) {
        localDataSource.saveHSN(hsn)
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
        callback: DataSource.OnResponseCallback<NetworkPostResponse>
    ) {
        networkDataSource.updatePayment(
            invoiceId=invoiceId,
            paymentType=paymentType,
            method=method,
            amount=amount,
            transactionNumber=transactionNumber,
            transactionDetails=transactionDetails,
            bankName=bankName,
            cardNumber=cardNumber,
            drawnOnDate=drawnOnDate,
            chequeDate=chequeDate,
            chequeNumber=chequeNumber,
            remarks=remarks,
            notifyCustomer=notifyCustomer,
            callback=callback)
    }

    override fun getSourceTypes(callback: DataSource.OnResponseCallback<List<CustomerSourceType>>) {
        networkDataSource.getSourceTypes(callback)
    }

    override fun getSourcesById(
        sourceId: String,
        callback: DataSource.OnResponseCallback<List<CustomerSource>>
    ) {
        networkDataSource.getSourcesById(sourceId,callback)
    }

    override fun getReceiptDetails(
        invoiceId: String,
        callback: DataSource.OnResponseCallback<List<Payment>>
    ) {
        networkDataSource.getReceiptDetails(invoiceId,callback)
    }

    override fun getReceiptPdf(receiptId: String, callback: DataSource.OnResponseCallback<PDF>) {
       networkDataSource.getReceiptPdf(receiptId,callback)
    }

    // Appointments
    override fun getAppointments(type: String, skip: Int, limit: Int, callback: DataSource.OnResponseCallback<PaginationList<Appointment>>) {
        networkDataSource.getAppointments(type, skip, limit, callback)
    }

    override fun cancelAppointment(appointmentId: String, callback: DataSource.OnResponseCallback<Appointment>) {
        networkDataSource.cancelAppointment(appointmentId, callback)
    }

    override fun getAppointmentsByID(appointmentId: String, callback: DataSource.OnResponseCallback<Appointment>) {
        networkDataSource.getAppointmentsByID(appointmentId, callback)
    }

    override fun getVehicleDetails(registrationNumber: String, callback: DataSource.OnResponseCallback<List<Vehicle>>) {
        networkDataSource.getVehicleDetails(registrationNumber, callback)
    }

    override fun getCustomerDetails(mobileNumber: String, callback: DataSource.OnResponseCallback<List<Customer>>) {
        networkDataSource.getCustomerDetails(mobileNumber, callback)
    }

    override fun saveDetails(appointment: Appointment, callback: DataSource.OnResponseCallback<Appointment>) {
        networkDataSource.saveDetails(appointment, callback)
    }

    override fun saveAppointmentStatus(
        appointment: AppointmentStatus,
        callback: DataSource.OnResponseCallback<Any>
    ) {
        networkDataSource.saveAppointmentStatus(appointment,callback)
    }

    override fun getPackages(id: String, callback: DataSource.OnResponseCallback<Packages>) {
        networkDataSource.getPackages(id, callback)
    }

    override fun updatePackages(jobCardId: String, invoiceId: String, isInvoice: Boolean, packageIds: List<String>, callback: DataSource.OnResponseCallback<List<ServicePackage>>) {
        networkDataSource.updatePackages(jobCardId, invoiceId, isInvoice, packageIds, callback)
    }

    override fun savePackages(appointmentId: String, appointmentPost: AppointmentPost, callback: DataSource.OnResponseCallback<Appointment>) {
        networkDataSource.savePackages(appointmentId, appointmentPost, callback)
    }

    override fun getTimeSlot(appointmentId: String, dateTime: String, type: String?, callback: DataSource.OnResponseCallback<List<TimeSlot>>) {
        networkDataSource.getTimeSlot(appointmentId, dateTime, type, callback)
    }

    override fun saveTimeSlot(appointmentId: String, dateTime: AppointmentPost, callback: DataSource.OnResponseCallback<Appointment>) {
        networkDataSource.saveTimeSlot(appointmentId, dateTime, callback)
    }

    override fun upsertData(appointmentId: String, upsertDetails: UpsertDetails, callback: DataSource.OnResponseCallback<UpsertDetails>) {
        networkDataSource.upsertData(appointmentId, upsertDetails, callback)
    }

    override fun getProformaEstimatePdf(invoiceId: String, callback: DataSource.OnResponseCallback<PDF>) {
        networkDataSource.getProformaEstimatePdf(invoiceId, callback)
    }

    override fun rescheduleAppointment(appointmentId: String, reschedule: Reschedule, callback: DataSource.OnResponseCallback<Reschedule>) {
        networkDataSource.rescheduleAppointment(appointmentId, reschedule, callback)
    }

    override fun rejectAppointment(appointmentId: String, reschedule: Reschedule, callback: DataSource.OnResponseCallback<Reschedule>) {
        networkDataSource.rejectAppointment(appointmentId, reschedule, callback)
    }

    override fun acceptAppointment(appointmentId: String, callback: DataSource.OnResponseCallback<Appointment>) {
        networkDataSource.acceptAppointment(appointmentId, callback)
    }

    override fun getCustomerVehicleList(query: String?, filterList: List<String>?, startDate: String?, endDate: String?, skip: Int, limit: Int, callback: DataSource.OnResponseCallback<PaginationList<CustomerVehicleDetails>>) {
        networkDataSource.getCustomerVehicleList(query, filterList, startDate, endDate, skip, limit, callback)
    }

    override fun getCustomerVehicleHistory(id: String, callback: DataSource.OnResponseCallback<CustomerVehicleDetails>) {
        networkDataSource.getCustomerVehicleHistory(id, callback)
    }

    override fun saveCostEstimation(id: String, costEstimate: CostEstimate, callback: DataSource.OnResponseCallback<JobCard>) {
        networkDataSource.saveCostEstimation(id, costEstimate, callback)
    }

    override fun updateProforma(invoiceId: String, invoice: Invoice, callback: DataSource.OnResponseCallback<Invoice>) {
        networkDataSource.updateProforma(invoiceId, invoice, callback)
    }

    override fun saveRemarks(jobCardId: String, invoiceRemarks: InvoiceRemarks, callback: DataSource.OnResponseCallback<JobCard>) {
        networkDataSource.saveRemarks(jobCardId, invoiceRemarks, callback)
    }

    override fun getRemarks(jobCardId: String, callback: DataSource.OnResponseCallback<InvoiceRemarks>) {
        networkDataSource.getRemarks(jobCardId, callback)
    }

    override fun getWorkShopResources(callback: DataSource.OnResponseCallback<WorkshopResource>) {
        networkDataSource.getWorkShopResources(callback)
    }

    /*Accidental Start*/

    override fun saveAccidentalData(jobCardId: String, accidental: Accidental, callback: DataSource.OnResponseCallback<JobCard>) {
        networkDataSource.saveAccidentalData(jobCardId, accidental, callback)
    }

    override fun saveMissingAccidentalDetails(jobCardId: String, missingAccidentalDetails: MissingAccidentalDetails, callback: DataSource.OnResponseCallback<JobCard>) {
        networkDataSource.saveMissingAccidentalDetails(jobCardId, missingAccidentalDetails, callback)
    }

    override fun updateDocument(originalFileName: String, imageId: String, url: String, isUploaded: Boolean, callback: DataSource.OnResponseCallback<FileObject>?) {
        localDataSource.updateDocument(originalFileName, Objects.requireNonNull(imageId), Objects.requireNonNull(url), true, object : DataSource.OnResponseCallback<FileObject> {
            override fun onSuccess(obj: FileObject) {
//                val storage = File(Objects.requireNonNull<String>(obj.uri))
//                storage.delete()
                callback?.onSuccess(obj)
            }

            override fun onError(error: ErrorWrapper) {
                callback?.onError(error)
            }
        })
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun uploadDocument(file: FileObject, callback: DataSource.OnResponseCallback<FileObject>) {
        localDataSource.uploadDocument(file, object : DataSource.OnResponseCallback<FileObject> {
            override fun onSuccess(obj: FileObject) {
                callback.onSuccess(obj)
                networkDataSource.uploadDocument(obj, object : DataSource.OnResponseCallback<FileObject> {
                    override fun onSuccess(fileObject: FileObject) {
                        updateDocument(fileObject.originalName, Objects.requireNonNull<String>(fileObject.id), Objects.requireNonNull<String>(fileObject.url), true, null)
                    }

                    override fun onError(error: ErrorWrapper) {
                        callback.onError(error)
                    }
                })
            }

            override fun onError(error: ErrorWrapper) {
                callback.onError(error)
            }
        })
    }

    override suspend fun uploadDocument(document: FileObject): Result<FileObject> {
        val result = networkDataSource.uploadDocument(document)
        if (result is Result.Success) {
            val uploadedDocument = result.data
            localDataSource.updateDocument(uploadedDocument.originalName, uploadedDocument.id!!, uploadedDocument.url!!, true, null)
        }
        return result
    }

    override fun getDocuments(jobCardId: String, callback: DataSource.OnResponseCallback<List<FileObject>>) {
        localDataSource.getDocuments(jobCardId, callback)
        networkDataSource.getDocuments(jobCardId, object : DataSource.OnResponseCallback<List<FileObject>> {
            override fun onSuccess(obj: List<FileObject>) {
                if (obj.isNotEmpty()) {
                    for (file in obj) {
                        file.jobCardID = jobCardId
                        file.type = FileObject.FILE_TYPE_ACCIDENTAL
                    }
                    saveDocuments(jobCardId, obj)
                }
                localDataSource.getDocuments(jobCardId, callback)
            }

            override fun onError(error: ErrorWrapper) {
            }
        })
    }

    override fun saveDocuments(jobCardId: String, files: List<FileObject>) {
        localDataSource.saveDocuments(jobCardId, files)
    }

    override fun clearAccidentalCache() {
        localDataSource.clearAccidentalCache()
    }

    override fun deleteDocument(fileObject: FileObject, callback: DataSource.OnResponseCallback<FileObject>) {
        localDataSource.deleteDocument(fileObject, callback)
        if (fileObject.isUploaded) {
            networkDataSource.deleteDocument(fileObject, callback)
        }
    }

    /*Accidental End*/

    override fun saveLead(leadId: String, leadForm: LeadForm, callback: DataSource.OnResponseCallback<LeadForm>) {
        networkDataSource.saveLead(leadId, leadForm, callback)
    }

    override fun getInsurancePdf(invoiceId: String, callback: DataSource.OnResponseCallback<PDF>) {
        networkDataSource.getInsurancePdf(invoiceId, callback)
    }

    override fun getCustomerPdf(invoiceId: String, callback: DataSource.OnResponseCallback<PDF>) {
        networkDataSource.getCustomerPdf(invoiceId, callback)
    }

    override fun getIgstCustomerPdf(invoiceId: String, callback: DataSource.OnResponseCallback<PDF>) {
        networkDataSource.getIgstCustomerPdf(invoiceId, callback)
    }

    override fun getInvoiceAndInsuranceById(invoiceId: String, callback: DataSource.OnResponseCallback<Invoice>) {
        networkDataSource.getInvoiceAndInsuranceById(invoiceId, callback)
    }

    override fun saveServiceReminder(vehicleId: String, date: String, callback: DataSource.OnResponseCallback<ServiceDate>) {
        networkDataSource.saveServiceReminder(vehicleId, date, callback)
    }

    override fun addOtcProforma(customerId: String, addressId: String, vehicleType: String?, callback: DataSource.OnResponseCallback<Invoice>) {
        networkDataSource.addOtcProforma(customerId, addressId, vehicleType, callback)
    }

    override fun getDashBoardDetails(callback: DataSource.OnResponseCallback<WorkshopResource>) {
        networkDataSource.getDashBoardDetails(callback)
    }

    override fun saveSignature(jobCardId: String, file: File, callback: DataSource.OnResponseCallback<Signature>) {
        networkDataSource.saveSignature(jobCardId, file, callback)
    }

    override fun saveQuickJobCard(jobCardId: String, jobCard: JobCard, callback: DataSource.OnResponseCallback<JobCard>) {
        networkDataSource.saveQuickJobCard(jobCardId, jobCard, callback)
    }

    override fun saveCustomerFeedback(jobcardId: String, recommendedScore: Int, serviceQuality: Float, billingTransparency: Float, timelyDelivery: Float, comments: String?, callback: DataSource.OnResponseCallback<NetworkPostResponse>) {
        networkDataSource.saveCustomerFeedback(jobcardId, recommendedScore, serviceQuality, billingTransparency, timelyDelivery, comments, callback)
    }

    override fun getServiceAdvisors(workshopId: String, callback: DataSource.OnResponseCallback<List<WorkshopAdviser>>) {
        networkDataSource.getServiceAdvisors(workshopId, callback)
    }

    override fun registerDevice(deviceId: String, fcmTokenObj: FcmTokenEntity, callback: DataSource.OnResponseCallback<NetworkPostResponse>) {
        networkDataSource.registerDevice(deviceId, fcmTokenObj, callback)
    }

    override fun reassignAppointment(appointmentId: String, serviceAdvisorId: String, callback: DataSource.OnResponseCallback<NetworkPostResponse>) {
        networkDataSource.reassignAppointment(appointmentId, serviceAdvisorId, callback)
    }

    override fun getWhatsAppTemplate(templateType: String, id: String, callback: DataSource.OnResponseCallback<WhatsAppTemplate>) {
        networkDataSource.getWhatsAppTemplate(templateType, id, callback)
    }

    override fun getStates(callback: DataSource.OnResponseCallback<List<State>>) {
        localDataSource.getStates(object : DataSource.OnResponseCallback<List<State>> {
            override fun onSuccess(obj: List<State>) {
                if (obj.isEmpty()) {
                    networkDataSource.getStates(object : DataSource.OnResponseCallback<List<State>> {
                        override fun onSuccess(obj: List<State>) {
                            callback.onSuccess(obj)
                            localDataSource.saveStates(obj, null)
                        }

                        override fun onError(error: ErrorWrapper) {
                            callback.onError(error)
                        }
                    })
                } else {
                    callback.onSuccess(obj)
                }
            }

            override fun onError(error: ErrorWrapper) {
                callback.onError(error)
            }
        })
    }

    override fun saveStates(states: List<State>, callback: DataSource.OnResponseCallback<List<State>>?) = Unit

    override suspend fun saveThirdPartyDetails(invoiceId: String, thirdParty: ThirdParty): Result<ThirdParty> {
        return networkDataSource.saveThirdPartyDetails(invoiceId, thirdParty)
    }

    override suspend fun removeThirdPartyDetails(invoiceId: String, thirdParty: ThirdParty): Result<ThirdParty> {
        return networkDataSource.removeThirdPartyDetails(invoiceId, thirdParty)
    }

    override fun getAMCList(types: String, limit: Int, skip: Int,query: String, callback: DataSource.OnResponseCallback<PaginationList<AMC>>)
    {
        networkDataSource.getAMCList(types, limit, skip,query, callback)
    }

    override fun getVehicleAMCById(vehicleAmcId: String?, callback: DataSource.OnResponseCallback<List<AMC>>) {
        networkDataSource.getVehicleAMCById(vehicleAmcId,callback)
    }

    override fun suggestAmcPackages(vehicleId: String, callback: DataSource.OnResponseCallback<List<AMCPackage>>) {
        networkDataSource.suggestAmcPackages(vehicleId,callback)
    }

    override fun purchaseAMC(amcDetails: AMCPurchase, callback: DataSource.OnResponseCallback<AMC>) {
        networkDataSource.purchaseAMC(amcDetails, callback)
    }


    override fun getOtherHistory(registrationNumber: String, mobileNumber: String, callback: DataSource.OnResponseCallback<CustomerVehicleDetails>) {
        return networkDataSource.getOtherHistory(registrationNumber, mobileNumber, callback)
    }

    override fun getRedemptionDetails(vehicleAmcId: String, callback: DataSource.OnResponseCallback<SoldAMCDetails>)
    {
        networkDataSource.getRedemptionDetails(vehicleAmcId,callback)
    }

    override fun cancelAMC(
        vehicleAmcId: String,
        reason: String,
        callback: DataSource.OnResponseCallback<SoldAMCDetails>
    ) {
        networkDataSource.cancelAMC(vehicleAmcId,reason,callback)
    }

    override fun getVendorList(callback: DataSource.OnResponseCallback<List<Vendor>>) {
        networkDataSource.getVendorList(callback)
    }

    override fun getPdcChecklist(
        jobCardId: String,
        callback: DataSource.OnResponseCallback<List<PdcBase>>
    ) {
        networkDataSource.getPdcChecklist(jobCardId,callback)
    }

    override fun postPdcChecklist(
        jobCardId: String,
        pdcEntity: PdcEntity,
        callback: DataSource.OnResponseCallback<NetworkPostResponse>
    ) {
        networkDataSource.postPdcChecklist(jobCardId,pdcEntity,callback)
    }

    override fun saveCustomerGroup(options: List<Option>) {
        localDataSource.saveCustomerGroup(options)
    }

    override fun getCustomerGroup(callback: DataSource.OnResponseCallback<List<Option>>) {
        localDataSource.getCustomerGroup(callback)
    }

    override fun deleteCustomerGroup(callback: DataSource.OnResponseCallback<Boolean>) {
        localDataSource.deleteCustomerGroup(callback)
    }
}
