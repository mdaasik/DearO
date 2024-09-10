package com.carworkz.dearo.data.remote

import com.carworkz.dearo.domain.entities.PdcBase
import com.carworkz.dearo.domain.entities.CustomerAndAddress
import com.carworkz.dearo.amc.AMCPurchase
import com.carworkz.dearo.base.ErrorWrapper
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.cardslisting.CardListingFragment
import com.carworkz.dearo.data.DataSource
import com.carworkz.dearo.data.Result
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.events.ToggleMenuItemsEvent
import com.carworkz.dearo.helpers.ErrorWrapperHelper
import com.carworkz.dearo.helpers.MetaDataMapper
import com.carworkz.dearo.helpers.PaginationList
import com.carworkz.dearo.domain.entities.PdcEntity
import com.carworkz.dearo.retrofitcustomadapter.NetworkCall
import com.carworkz.dearo.retrofitcustomadapter.NetworkCall.NetworkCallBack
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class NetworkDataSource @Inject
constructor(
    private val retroFitService: RetroFitService,
    private val nonIdempotentService: RetroFitService,
    private val errorWrapperHelper: ErrorWrapperHelper
) : DataSource {

    private val reqCache: MutableMap<String, NetworkCall<*>>

    private val reqCacheCoroutine: MutableMap<Int, Job>

    init {
        reqCache = HashMap()
        reqCacheCoroutine = HashMap()
        Timber.d(TAG, "NetworkDataSource: $retroFitService")
    }

    override fun sendOtp(
        mobileNo: String,
        callback: DataSource.OnResponseCallback<NetworkPostResponse>
    ) {
        retroFitService.sendOtp(mobileNo).enqueue(object : NetworkCallBack<NetworkPostResponse> {
            override fun success(response: Response<NetworkPostResponse>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                try {
                    if (response.errorBody() != null)
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string(), MetaDataMapper.toObject(response)
                            )
                        )
                    else {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(""))
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun clientError(response: Response<*>) {
                try {
                    val errorWrapper =
                        errorWrapperHelper.transformToErrorWrapper(response.errorBody()!!.string())
                    callback.onError(errorWrapper)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun loginUser(
        mobileNo: String,
        otpcode: Int,
        callback: DataSource.OnResponseCallback<User>
    ) {
        retroFitService.loginUser(mobileNo, otpcode).enqueue(object : NetworkCallBack<User> {
            override fun success(response: Response<User>) {
                SharedPrefHelper.setUser(response.body())
                SharedPrefHelper.setDeviceId(UUID.randomUUID().toString())
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                try {

                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun clientError(response: Response<*>) {
                try {
                    val wrapper =
                        errorWrapperHelper.transformToErrorWrapper(response.errorBody()!!.string())
                    callback.onError(wrapper)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun logoutUser(callback: DataSource.OnResponseCallback<NetworkPostResponse?>) {
        retroFitService.logoutUser().enqueue(object : NetworkCallBack<NetworkPostResponse> {
            override fun success(response: Response<NetworkPostResponse>) {
                callback.onSuccess(response.body())
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                try {
                    val wrapper =
                        errorWrapperHelper.transformToErrorWrapper(response.errorBody()!!.string())
                    callback.onError(wrapper)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun getMake(
        vehicleType: String?,
        callback: DataSource.OnResponseCallback<List<Make>>
    ) {
        retroFitService.getMake(SharedPrefHelper.getWorkshopId(), vehicleType)
            .enqueue(object : NetworkCallBack<List<Make>> {
                override fun success(response: Response<List<Make>>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    try {
                        val wrapper = errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                        callback.onError(wrapper)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun getModel(makeSlug: String, callback: DataSource.OnResponseCallback<List<Model>>) {
        retroFitService.getModel(SharedPrefHelper.getWorkshopId(), makeSlug)
            .enqueue(object : NetworkCallBack<List<Model>> {
                override fun success(response: Response<List<Model>>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    try {
                        val wrapper = errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                        callback.onError(wrapper)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun getVariant(
        modelSlug: String,
        fuelType: String?,
        callback: DataSource.OnResponseCallback<List<Variant>>
    ) {
        retroFitService.getVariant(SharedPrefHelper.getWorkshopId(), modelSlug, fuelType)
            .enqueue(object : NetworkCallBack<List<Variant>> {
                override fun success(response: Response<List<Variant>>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    try {
                        val wrapper = errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                        callback.onError(wrapper)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun addVehicle(vehicle: Vehicle, callback: DataSource.OnResponseCallback<Vehicle>) {
        retroFitService.addVehicle(vehicle).enqueue(object : NetworkCallBack<Vehicle> {
            override fun success(response: Response<Vehicle>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun updateVehicle(vehicle: Vehicle, callback: DataSource.OnResponseCallback<Vehicle>) {
        retroFitService.updateVehicle(vehicle.id, vehicle)
            .enqueue(object : NetworkCallBack<Vehicle> {
                override fun success(response: Response<Vehicle>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                }

                override fun unexpectedError(t: Throwable) {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                }
            })
    }

    override suspend fun updateVehicleVariantInfo(
        vehicleId: String,
        VehicleVariantBody: VehicleVariantBody
    ): Result<NetworkPostResponse> {
        return try {
            val response = retroFitService.updateVehicleVariantInfo(vehicleId, VehicleVariantBody)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(
                    errorWrapperHelper.transformToErrorWrapper(
                        response.errorBody()?.string()
                    )
                )
            }
        } catch (e: Exception) {
            Result.Error(errorWrapperHelper.transformToErrorWrapper(e))
        }
    }

    override fun getCompanyNames(callback: DataSource.OnResponseCallback<List<InsuranceCompany>>) {
        val where = JSONObject()
        val status = JSONObject()
        try {
            status.put("status", "ACTIVE")
            where.put("where", status)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        retroFitService.getInsuranceCompanies(where)
            .enqueue(object : NetworkCallBack<List<InsuranceCompany>> {
                override fun success(response: Response<List<InsuranceCompany>>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                }

                override fun unexpectedError(t: Throwable) {
                }
            })
    }

    override fun getInsuranceCompanyAddresses(
        insuranceCompany: InsuranceCompany,
        callback: DataSource.OnResponseCallback<List<InsuranceCompanyDetails>>
    ) {
        retroFitService.getInsuranceCompaniesAddresses(insuranceCompany)
            .enqueue(object : NetworkCallBack<List<InsuranceCompanyDetails>> {
                override fun success(response: Response<List<InsuranceCompanyDetails>>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                }

                override fun unexpectedError(t: Throwable) {
                }
            })
    }

    override fun uploadUnSavedDocument(
        image: FileObject,
        callback: DataSource.OnResponseCallback<FileObject>
    ) {
        uploadDocument(image, callback)
    }


    override fun newSearchApiCall(
        mobileNo: String,
        registrationNumber: String,
        callback: DataSource.OnResponseCallback<ResponseBody>
    ) {

        retroFitService.searchCustomerNewApiCall(mobileNo, registrationNumber)
            .enqueue(object : NetworkCallBack<ResponseBody> {
                override fun success(response: Response<ResponseBody>?) {
                    if (response != null) {
                        callback.onSuccess(response.body()!!)
                    }
                }

                override fun unauthenticated(response: Response<*>?) {
                    if (response != null) {
                        if (response.errorBody() != null) {
                            try {
                                callback.onError(
                                    errorWrapperHelper.transformToErrorWrapper(
                                        response.errorBody()!!.string()
                                    )
                                )
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }

                override fun clientError(response: Response<*>?) {
                    if (response != null) {
                        if (response.errorBody() != null) {
                            try {
                                callback.onError(
                                    errorWrapperHelper.transformToErrorWrapper(
                                        response.errorBody()!!.string()
                                    )
                                )
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }

                override fun serverError(response: Response<*>?) {

                    if (response != null) {
                        if (response.errorBody() != null) {
                            try {
                                callback.onError(
                                    errorWrapperHelper.transformToErrorWrapper(
                                        response.errorBody()!!.string()
                                    )
                                )
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }

                override fun networkError(e: IOException?) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e.toString())
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable?) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t.toString())
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

            })

    }

    override fun searchCustomerAndCarDetails(
        mobileNo: String,
        registrationNumber: String,
        callback: DataSource.OnResponseCallback<CustomerVehicleSearch>
    ) {
        retroFitService.searchCustomerAndCarDetails(mobileNo, registrationNumber)
            .enqueue(object : NetworkCallBack<CustomerVehicleSearch> {
                override fun success(response: Response<CustomerVehicleSearch>) {
                    if (!response.headers().get("x-history-count")
                            .isNullOrEmpty() && !response.headers().get("x-service-history-count")
                            .isNullOrEmpty()
                    ) {
                        response.body()!!.historyCount =
                            response.headers().get("x-history-count")!!.toInt()
                        response.body()!!.otherServiceHistoryCount =
                            response.headers().get("x-service-history-count")!!.toInt()
                    }
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun saveVoice(
        jobCardId: String,
        verbatim: Verbatim,
        callback: DataSource.OnResponseCallback<Verbatim>
    ) {
        retroFitService.saveVoice(jobCardId, verbatim).enqueue(object : NetworkCallBack<Verbatim> {
            override fun success(response: Response<Verbatim>) {
                Timber.e(TAG + "success: veoice")
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun getInventory(
        vehicleType: String?,
        callback: DataSource.OnResponseCallback<List<Inventory>>
    ) {
        val obj = JSONObject()
        val vehicleFilter = JSONObject()
        try {
            obj.put("order", "rank asc")
            vehicleFilter.put("vehicleType", vehicleType)
            obj.put("where", vehicleFilter)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        retroFitService.getInventories(obj).enqueue(object : NetworkCallBack<List<Inventory>> {
            override fun success(response: Response<List<Inventory>>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                Timber.d(TAG + " unauthenticated: " + response.errorBody())
                try {
                    val errorWrapper =
                        errorWrapperHelper.transformToErrorWrapper(response.errorBody()!!.string())
                    callback.onError(errorWrapper)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun clientError(response: Response<*>) {
                Timber.d(TAG + " clientError: " + response.errorBody())
                try {
                    val errorWrapper =
                        errorWrapperHelper.transformToErrorWrapper(response.errorBody()!!.string())
                    callback.onError(errorWrapper)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun serverError(response: Response<*>) {
                Timber.d(TAG + " serverError: " + response.errorBody())
                try {
                    val errorWrapper =
                        errorWrapperHelper.transformToErrorWrapper(response.errorBody()!!.string())
                    callback.onError(errorWrapper)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun saveJobCardInventory(
        jobCardId: String,
        serviceType: String,
        fuelReading: Float,
        kmsReading: Int,
        inventory: List<Inventory>,
        remarks: String?,
        callback: DataSource.OnResponseCallback<NetworkPostResponse>
    ) {
        val postPOJO = InventoryPostPOJO(serviceType, remarks, fuelReading, kmsReading, inventory)
        retroFitService.saveJobCardInventory(jobCardId, postPOJO)
            .enqueue(object : NetworkCallBack<NetworkPostResponse> {
                override fun success(response: Response<NetworkPostResponse>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun pinCodeCity(pinCode: Int, callback: DataSource.OnResponseCallback<Pincode>) {
        val include = JSONObject()
        try {
            include.put("include", "city")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        retroFitService.pinCodeCity(pinCode, include).enqueue(object : NetworkCallBack<Pincode> {
            override fun success(response: Response<Pincode>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                callback.onError(
                    errorWrapperHelper.transformToErrorWrapper(
                        response.errorBody()!!.toString()
                    )
                )
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun addCustomer(
        customer: CustomerAndAddress,
        callback: DataSource.OnResponseCallback<CustomerAndAddressResponse>
    ) {
        retroFitService.addCustomerAndAddress(SharedPrefHelper.getUserAccessToken(), customer)
            .enqueue(object : NetworkCallBack<CustomerAndAddressResponse> {
                override fun success(response: Response<CustomerAndAddressResponse>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

            })
    }

    override fun getCustomerById(Id: String, callback: DataSource.OnResponseCallback<Customer>) {
        val include = JSONObject()
        try {
            include.put("include", "addresses")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        retroFitService.getCustomerById(Id, include).enqueue(object : NetworkCallBack<Customer> {
            override fun success(response: Response<Customer>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
            }

            override fun unexpectedError(t: Throwable) {
                callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
            }
        })
    }

    override fun addCustomerAddress(
        address: Address,
        callback: DataSource.OnResponseCallback<Address>
    ) {
        retroFitService.addAddress(address).enqueue(object : NetworkCallBack<Address> {
            override fun success(response: Response<Address>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun getCustomerConcernSuggestions(
        query: String,
        callback: DataSource.OnResponseCallback<List<CustomerConcern>>
    ) {
        retroFitService.getCustomerConcernSuggestions(query)
            .enqueue(object : NetworkCallBack<List<CustomerConcern>> {
                override fun success(response: Response<List<CustomerConcern>>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun addAlternate(
        phone: String,
        customerId: String,
        callback: DataSource.OnResponseCallback<Customer>
    ) {
        retroFitService.addAlternateNumber(phone, customerId)
            .enqueue(object : NetworkCallBack<Customer> {
                override fun success(response: Response<Customer>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun updateNumber(
        phone: String,
        customerId: String,
        callback: DataSource.OnResponseCallback<Customer>
    ) {
        retroFitService.updateNumber(phone, customerId).enqueue(object : NetworkCallBack<Customer> {
            override fun success(response: Response<Customer>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun updateCustomer(
        id: String,
        customer: Customer,
        address: Address,
        callback: DataSource.OnResponseCallback<Customer>
    ) {
        retroFitService.updateCustomer(customer, id).enqueue(object : NetworkCallBack<Customer> {
            override fun success(response: Response<Customer>) {
                updateCustomerAddress(
                    address.id!!,
                    address,
                    object : DataSource.OnResponseCallback<Address> {
                        override fun onSuccess(obj: Address) {
                            callback.onSuccess(response.body()!!)
                        }

                        override fun onError(error: ErrorWrapper) {
                            callback.onError(error)
                        }
                    })
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun updateCustomerAddress(
        addressId: String,
        address: Address,
        callback: DataSource.OnResponseCallback<Address>
    ) {
        retroFitService.updateAddress(addressId, address)
            .enqueue(object : NetworkCallBack<Address> {
                override fun success(response: Response<Address>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                }

                override fun unexpectedError(t: Throwable) {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                }
            })
    }

    override fun saveJobCardEstimate(
        jobCardId: String,
        dateTime: String,
        minEstimate: Int,
        maxEstimate: Int,
        status: String?,
        notify: Boolean,
        reasonForDelay: String?,
        callback: DataSource.OnResponseCallback<NetworkPostResponse>
    ) {
        retroFitService.saveJobCardEstimate(
            jobCardId,
            dateTime,
            minEstimate,
            maxEstimate,
            status,
            notify
        ).enqueue(object : NetworkCallBack<NetworkPostResponse> {
            override fun success(response: Response<NetworkPostResponse>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun getAMCList(
        status: String,
        limit: Int,
        skip: Int,
        query: String,
        callback: DataSource.OnResponseCallback<PaginationList<AMC>>
    ) {
        val includeString = ArrayList<String>(2)
        includeString.add("vehicle")
        includeString.add("customer")

        val filterObj = JSONObject()
//        filterObj.put("include", JSONArray(includeString))
        filterObj.put("limit", limit)
        filterObj.put("skip", skip)
        filterObj.put("status", status)
        filterObj.put("q", query)

        retroFitService.getAmcCards(filterObj).enqueue(object : NetworkCallBack<List<AMC>> {
            override fun success(response: Response<List<AMC>>) {
                callback.onSuccess(
                    PaginationList.getPaginationList(
                        response.body()!!,
                        MetaDataMapper.toObject(response)
                    )
                )
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    Timber.d("unexpectedError: " + t.message)
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun getVehicleAMCById(
        vehicleAmcId: String?,
        callback: DataSource.OnResponseCallback<List<AMC>>
    ) {
        val include = JSONArray()
        include.apply {
            put(JSONObject().apply {
                put("relation", "invoice")
                put("scope", JSONObject().apply { put("include", "pdf") })
            })
            put("vehicle")
            put("customer")
        }
        val filterObj = JSONObject()
        filterObj.put("include", include)
        filterObj.put("where", JSONObject().apply {
            put("id", vehicleAmcId)
        })

        //{"include":[{"relation":"invoice","scope":{"include":"pdf"}},"vehicle","customer"]}

        retroFitService.getAmcCardById(filterObj).enqueue(object : NetworkCallBack<List<AMC>> {
            override fun success(response: Response<List<AMC>>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    Timber.d("unexpectedError: " + t.message)
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun suggestAmcPackages(
        vehicleId: String,
        callback: DataSource.OnResponseCallback<List<AMCPackage>>
    ) {
        val options = JSONObject()
        options.put("vehicleId", vehicleId)

        retroFitService.suggestAmcPackages(options)
            .enqueue(object : NetworkCallBack<List<AMCPackage>> {
                override fun success(response: Response<List<AMCPackage>>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        Timber.d("unexpectedError: " + t.message)
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun purchaseAMC(
        amcDetails: AMCPurchase,
        callback: DataSource.OnResponseCallback<AMC>
    ) {
        retroFitService.purchaseAMC(amcDetails).enqueue(object : NetworkCallBack<AMC> {
            override fun success(response: Response<AMC>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    Timber.d("unexpectedError: " + t.message)
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

        })
    }

    override fun getJobCards(
        types: List<String>,
        limit: Int,
        skip: Int,
        callback: DataSource.OnResponseCallback<PaginationList<JobCard>>
    ) {
        val includeString = ArrayList<String>(5)
        val order = ArrayList<String>()
        includeString.add("vehicle")
        includeString.add("customer")
        includeString.add("workshop")
        includeString.add("invoice")
        includeString.add("customerAddress")
        when (types.last()) {
            JobCard.STATUS_IN_PROGRESS -> order.add("estimate.deliveryDateTime ASC")
            JobCard.STATUS_COMPLETED -> order.add("completionDate DESC")
            JobCard.STATUS_CLOSED, JobCard.STATUS_CANCELLED -> includeString.add("feedback")
            else -> order.add("createdOn DESC")
        }
        // val queryWrapper = QueryWrapper(includeString, jobCardTypes, order, limit, skip)

        val filterObj = JSONObject()
        filterObj.put("include", JSONArray(includeString))
        filterObj.put("limit", limit)
        filterObj.put("skip", skip)
        filterObj.put("order", JSONArray(order))
        filterObj.put("where", JSONObject().apply {
            put("status", JSONObject().apply {
                put("inq", JSONArray(types))
            })
        })
        retroFitService.getJobCards(filterObj).enqueue(object : NetworkCallBack<List<JobCard>> {
            override fun success(response: Response<List<JobCard>>) {
                callback.onSuccess(
                    PaginationList.getPaginationList(
                        response.body()!!,
                        MetaDataMapper.toObject(response)
                    )
                )
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    Timber.d("unexpectedError: " + t.message)
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }


    override fun cancelJobCard(
        jobCardId: String,
        callback: DataSource.OnResponseCallback<NetworkPostResponse>
    ) {
        retroFitService.cancelJobCard(jobCardId)
            .enqueue(object : NetworkCallBack<NetworkPostResponse> {
                override fun success(response: Response<NetworkPostResponse>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        Timber.d("Throwable : " + t.message)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun searchJobCards(
        search: String,
        query: String,
        callback: DataSource.OnResponseCallback<List<JobCard>>
    ) {
        val order = "createdOn DESC"
        retroFitService.searchJobCards(search, query, order)
            .enqueue(object : NetworkCallBack<List<JobCard>> {
                override fun success(response: Response<List<JobCard>>) {
                    //                Timber.d("response : " + response.body()!!.toString())
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        Timber.d("Throwable : " + t.message)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun initiateJobCard(
        customerId: String,
        vehicleId: String,
        appointmentId: String?,
        vehicleAmcId: String?,
        type: String,
        callback: DataSource.OnResponseCallback<JobCard>
    ) {
        retroFitService.initJobCard(customerId, vehicleId, appointmentId, type, vehicleAmcId)
            .enqueue(object : NetworkCallBack<JobCard> {
                override fun success(response: Response<JobCard>) {
                    if (response.body()!!.type == JobCard.TYPE_ACCIDENTAL) {
                        response.body()!!.step = JobCard.TYPE_ACCIDENTAL
                    }
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun getInspectionGroups(
        vehicleType: String?,
        callback: DataSource.OnResponseCallback<List<InspectionGroup>>
    ) {
        val filter = JSONObject()
        val vehicleFilter = JSONObject()
        try {
            vehicleFilter.put("vehicleType", vehicleType)
            filter.put("where", vehicleFilter)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        retroFitService.getInspectionGroups(filter)
            .enqueue(object : NetworkCallBack<List<InspectionGroup>> {
                override fun success(response: Response<List<InspectionGroup>>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun getInspectionItemsByGroup(
        groupId: String,
        callback: DataSource.OnResponseCallback<List<InspectionItem>>
    ) {
        retroFitService.getInspectionItemByGroup(groupId)
            .enqueue(object : NetworkCallBack<List<InspectionItem>> {
                override fun success(response: Response<List<InspectionItem>>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun saveInspection(
        jobcardId: String,
        inspectionPostPOJO: InspectionPostPOJO,
        callback: DataSource.OnResponseCallback<NetworkPostResponse>
    ) {
        retroFitService.saveInspection(jobcardId, inspectionPostPOJO)
            .enqueue(object : NetworkCallBack<NetworkPostResponse> {
                override fun success(response: Response<NetworkPostResponse>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun getJobsData(
        jobcardId: String,
        callback: DataSource.OnResponseCallback<JobAndVerbatim>
    ) {
        retroFitService.getJobsData(jobcardId).enqueue(object : NetworkCallBack<JobAndVerbatim> {
            override fun success(response: Response<JobAndVerbatim>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    Timber.d("Error : " + e.localizedMessage)
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    Timber.d("Error : " + t.localizedMessage)
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun getJobs(
        query: String,
        vehicleType: String?,
        callback: DataSource.OnResponseCallback<List<RecommendedJob>>
    ) {
        retroFitService.getJobs(query, vehicleType)
            .enqueue(object : NetworkCallBack<List<RecommendedJob>> {
                override fun success(response: Response<List<RecommendedJob>>) {
                    Timber.d("response : " + response.body()!!.toString())
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        Timber.d("Throwable : " + t.message)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun getParts(
        query: String,
        vehicleType: String?,
        vehicleAmcId: String?,
        callback: DataSource.OnResponseCallback<List<Part>>
    ) {
        retroFitService.getParts(query, vehicleType, vehicleAmcId)
            .enqueue(object : NetworkCallBack<List<Part>> {
                override fun success(response: Response<List<Part>>) {
                    Timber.d("response : " + response.body()!!.toString())
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        Timber.d("Throwable : " + t.message)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun getLabours(
        jobCardId: String,
        query: String,
        vehicleType: String?,
        vehicleAmcId: String?,
        callback: DataSource.OnResponseCallback<List<Labour>>
    ) {
        retroFitService.getLabours(query, jobCardId, vehicleType, vehicleAmcId)
            .enqueue(object : NetworkCallBack<List<Labour>> {
                override fun success(response: Response<List<Labour>>) {
                    Timber.d("response : " + response.body()!!.toString())
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        Timber.d("Throwable : " + t.message)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun saveJobsData(
        jobCardId: String,
        obj: Jobs,
        callback: DataSource.OnResponseCallback<NetworkPostResponse>
    ) {
        retroFitService.saveJobsData(jobCardId, obj)
            .enqueue(object : NetworkCallBack<NetworkPostResponse> {
                override fun success(response: Response<NetworkPostResponse>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {

                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {

                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {

                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun getJobCardDetails(
        jobcardId: String,
        includes: Array<String>,
        callback: DataSource.OnResponseCallback<JobCard>
    ) {
        val jsonObject = JSONObject()
        val fields = JSONArray()
        val include = JSONArray()
        for (field in includes) {
            include.put(field)
        }
        try {
            jsonObject.put("include", include)
            jsonObject.put("fields", fields)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        retroFitService.getJobFromJobCard(jobcardId, jsonObject)
            .enqueue(object : NetworkCallBack<JobCard> {
                override fun success(response: Response<JobCard>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                }

                override fun unexpectedError(t: Throwable) {
                    errorWrapperHelper.transformToErrorWrapper(t)
                }
            })
    }

    override fun getVoiceFromJobCard(
        jobCardId: String,
        callback: DataSource.OnResponseCallback<Verbatim?>
    ) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("fields", "verbatim")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        retroFitService.getVerbatimFromJobCard(jobCardId, jsonObject)
            .enqueue(object : NetworkCallBack<Verbatim> {
                override fun success(response: Response<Verbatim>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                }

                override fun unexpectedError(t: Throwable) {
                    errorWrapperHelper.transformToErrorWrapper(t)
                }
            })
    }

    override fun getJobCardById(
        jobCardId: String,
        include: ArrayList<String>?,
        callback: DataSource.OnResponseCallback<JobCard>
    ) {
        val includeObj = JSONObject()
        val array = JSONArray()
        try {
            if (include != null) {
                for (string in include) {
                    array.put(string)
                }
                includeObj.put("include", array)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        retroFitService.getJobCard(jobCardId, includeObj)
            .enqueue(object : NetworkCallBack<JobCard> {
                override fun success(response: Response<JobCard>) {
                    if (response.body()!!.type === JobCard.TYPE_ACCIDENTAL && response.body()!!.stepLabel === "INITIATED") {
                        response.body()!!.step = "Accidental"
                    }
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {

                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    /*Damages Start*/

    override fun saveDamageImages(jobCardId: String, imageList: List<FileObject>) {
        throw UnsupportedOperationException("Bulk upload of images to network not supported.")
    }

    override fun savePDCImage(
        image: FileObject,
        callback: DataSource.OnResponseCallback<FileObject>
    ) {
        if (!reqCache.containsKey(image.originalName)) {
            val file = File(image.uri!!)
            val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)

            val fileToUpload = MultipartBody.Part.createFormData("file", file.name, requestBody)
            val reqFileName = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file.name)

            val networkCall = nonIdempotentService.uploadPDCImage(
                Id = image.jobCardID!!,
                Caption = image.title!!,
                originalFileName = image.originalName,
                customScope = image.type!!, fileToUpload, reqFileName
            )
            reqCache[image.originalName] = networkCall
            networkCall.enqueue(object : NetworkCallBack<FileObject> {
                override fun success(response: Response<FileObject>) {
                    callback.onSuccess(response.body()!!)
                    reqCache.remove(image.originalName)
                }

                override fun unauthenticated(response: Response<*>) {

                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.toString()
                        )
                    )
                    reqCache.remove(image.originalName)
                }

                override fun clientError(response: Response<*>) {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.toString()
                        )
                    )
                    reqCache.remove(image.originalName)
                }

                override fun serverError(response: Response<*>) {
                    reqCache.remove(image.originalName)
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    reqCache.remove(image.originalName)
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    reqCache.remove(image.originalName)
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
        }
    }

    override fun updateDamageImage(
        originalFileName: String,
        imageId: String,
        url: String,
        isUploaded: Boolean,
        callback: DataSource.OnResponseCallback<FileObject>?
    ) {
    }

    override fun updateCaption(
        caption: String,
        jobCardId: String,
        imageId: String,
        callback: DataSource.OnResponseCallback<FileObject>
    ) {
        retroFitService.editCaption(jobCardId, imageId, caption)
            .enqueue(object : NetworkCallBack<FileObject> {
                override fun success(response: Response<FileObject>) {
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun getDamages(
        jobcardId: String,
        sort: String?,
        callback: DataSource.OnResponseCallback<List<FileObject>>
    ) {
        Timber.d("Farhan getting damages")
        retroFitService.getGalleryData(jobcardId)
            .enqueue(object : NetworkCallBack<List<FileObject>> {
                override fun success(response: Response<List<FileObject>>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun saveDamageImage(
        image: FileObject,
        callback: DataSource.OnResponseCallback<FileObject>
    ) {
        if (!reqCache.containsKey(image.originalName)) {
            val file = File(image.uri!!)
            val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)

            val fileToUpload = MultipartBody.Part.createFormData("file", file.name, requestBody)
            val reqFileName = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file.name)

            val networkCall = nonIdempotentService.uploadDamageImage(
                image.jobCardID!!,
                image.title!!,
                image.originalName,
                fileToUpload,
                reqFileName
            )
            reqCache[image.originalName] = networkCall
            networkCall.enqueue(object : NetworkCallBack<FileObject> {
                override fun success(response: Response<FileObject>) {
                    callback.onSuccess(response.body()!!)
                    reqCache.remove(image.originalName)
                }

                override fun unauthenticated(response: Response<*>) {

                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.toString()
                        )
                    )
                    reqCache.remove(image.originalName)
                }

                override fun clientError(response: Response<*>) {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.toString()
                        )
                    )
                    reqCache.remove(image.originalName)
                }

                override fun serverError(response: Response<*>) {
                    reqCache.remove(image.originalName)
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    reqCache.remove(image.originalName)
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    reqCache.remove(image.originalName)
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
        }
    }

    override suspend fun saveDamageImage(jobCardId: String, image: FileObject): Result<FileObject> {
        val job = GlobalScope.async {
            val file = File(image.uri!!)
            val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
            val fileToUpload = MultipartBody.Part.createFormData("file", file.name, requestBody)
            val reqFileName = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file.name)
            nonIdempotentService.uploadDamageImageAsync(
                image.jobCardID!!,
                image.title!!,
                image.originalName,
                fileToUpload,
                reqFileName
            )
        }
        reqCacheCoroutine[image.hashCode()] = job
        return try {
            val response = job.await()
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(
                    errorWrapperHelper.transformToErrorWrapper(
                        response.errorBody()?.string()
                    )
                )
            }
        } catch (e: Exception) {
            Result.Error(errorWrapperHelper.transformToErrorWrapper(e))
        } finally {
            reqCacheCoroutine.remove(image.hashCode())
        }
    }

    override fun deleteDamageImage(
        damageImg: FileObject,
        callback: DataSource.OnResponseCallback<FileObject?>?
    ) {
        if (reqCache.containsKey(damageImg.originalName)) {
            Timber.d("$TAG deleteDamageImage: from cache")
            reqCache[damageImg.originalName]?.cancel()
        } else {
            if (reqCacheCoroutine.containsKey(damageImg.hashCode())) {
                reqCacheCoroutine[damageImg.hashCode()]?.cancel()
            } else {
                Timber.d("$TAG farhan deleteDamageImage: from network")
                retroFitService.deleteDamageImage(damageImg.jobCardID!!, damageImg.id!!)
                    .enqueue(object : NetworkCallBack<FileObject> {
                        override fun success(response: Response<FileObject?>) {
                            callback?.onSuccess(response.body())
                        }

                        override fun unauthenticated(response: Response<*>) {
                            try {
                                callback?.onError(
                                    errorWrapperHelper.transformToErrorWrapper(
                                        response.errorBody()!!.string()
                                    )
                                )
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }

                        override fun clientError(response: Response<*>) {
                            try {
                                callback?.onError(
                                    errorWrapperHelper.transformToErrorWrapper(
                                        response.errorBody()!!.string()
                                    )
                                )
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }

                        override fun serverError(response: Response<*>) {
                            try {
                                callback?.onError(
                                    errorWrapperHelper.transformToErrorWrapper(
                                        response.errorBody()!!.string()
                                    )
                                )
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }

                        override fun networkError(e: IOException) {
                            try {
                                val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                                callback?.onError(errorWrapper)
                            } catch (exp: Exception) {
                                exp.printStackTrace()
                            }
                        }

                        override fun unexpectedError(t: Throwable) {
                            try {
                                val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                                callback?.onError(errorWrapper)
                            } catch (exp: Exception) {
                                exp.printStackTrace()
                            }
                        }
                    })
            }
        }
    }

    override fun clearDamageCache() {
    }

    override fun uploadUnSavedDamage(
        image: FileObject,
        callback: DataSource.OnResponseCallback<FileObject>
    ) {
        saveDamageImage(image, callback)
    }

    override suspend fun getDamages(jobcardId: String, sort: String?): Result<List<FileObject>> {
        return try {
            val response = retroFitService.getDamages(jobcardId)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(
                    errorWrapperHelper.transformToErrorWrapper(
                        response.errorBody()?.string()
                    )
                )
            }
        } catch (e: Exception) {
            Result.Error(errorWrapperHelper.transformToErrorWrapper(e))
        }
    }

    override suspend fun getPdcImages(jobcardId: String, sort: String?): Result<List<FileObject>> {
        return try {
            val response = retroFitService.getPdcImages(jobcardId)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(
                    errorWrapperHelper.transformToErrorWrapper(
                        response.errorBody()?.string()
                    )
                )
            }
        } catch (e: Exception) {
            Result.Error(errorWrapperHelper.transformToErrorWrapper(e))
        }
    }

    /*Damage Ends*/

    // Invoices

    override fun getInvoices(
        status: String,
        skip: Int,
        limit: Int,
        type: String?,
        callback: DataSource.OnResponseCallback<PaginationList<Invoice>>
    ) {

        val includeOption = ArrayList<String>(4)
        includeOption.add("vehicle")
        includeOption.add("customer")
        includeOption.add("customerAddress")
        if (status == Invoice.STATUS_INVOICED || status == Invoice.STATUS_PAID || status == Invoice.STATUS_PAID_PARTIAL) {
            includeOption.add("feedback")
        }

        try {
            val jsonArray = JSONArray(includeOption)

            val parentObj = JSONObject()

            val scope = JSONObject()

            val include = JSONArray()
            if (type == CardListingFragment.ARG_TYPE_JC) {
                scope.put("include", jsonArray)
                val relation = JSONObject()
                relation.put("relation", "jobCard")
                relation.put("scope", scope)
                include.put(relation)
            } else {
                include.put("customer")
                include.put("customerAddress")
            }

            val where = JSONObject()
            val inqStatus = JSONObject()
            val statusList = JSONArray()
            if (status == Invoice.STATUS_INVOICED) {
                statusList.put(status)
                statusList.put(Invoice.STATUS_PAID_PARTIAL)
            } else {
                statusList.put(status)
            }

            inqStatus.put("inq", statusList)
            where.put("status", inqStatus)
            where.put("type", type)
            parentObj.put("include", include)
            parentObj.put("where", where)

            //            if (status.equals(Invoice.CREATOR.getStatusInvoice())) {
            //                parentObj.put("order", "date DESC");
            //            } else {
            if (status == Invoice.STATUS_PROFORMA)
                parentObj.put("order", "proformaDate DESC")
            else {
                parentObj.put("order", "date DESC")
            }
            // }
            parentObj.put("skip", skip)
            parentObj.put("limit", limit)
            retroFitService.getInvoiceCards(parentObj)
                .enqueue(object : NetworkCallBack<List<Invoice>> {
                    override fun success(response: Response<List<Invoice>>) {
                        callback.onSuccess(
                            PaginationList.getPaginationList(
                                response.body()!!,
                                MetaDataMapper.toObject(response)
                            )
                        )
                    }

                    override fun unauthenticated(response: Response<*>) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    override fun clientError(response: Response<*>) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    override fun serverError(response: Response<*>) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    override fun networkError(e: IOException) {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                    }

                    override fun unexpectedError(t: Throwable) {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
//                    errorWrapperHelper.transformToErrorWrapper(t)
                    }
                })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun cancelInvoice(
        invoiceId: String,
        reschedule: Reschedule,
        callback: DataSource.OnResponseCallback<Invoice>
    ) {
        retroFitService.cancelInvoice(invoiceId, reschedule)
            .enqueue(object : NetworkCallBack<Invoice> {
                override fun success(response: Response<Invoice>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun getInvoiceById(
        invoiceId: String,
        callback: DataSource.OnResponseCallback<Invoice>
    ) {
        //        val include = JSONObject()
        //        try {
        //            include.put("include", "payments")
        //        } catch (e: JSONException) {
        //            e.printStackTrace()
        //        }
        retroFitService.getInvoiceById(invoiceId).enqueue(object : NetworkCallBack<Invoice> {
            override fun success(response: Response<Invoice>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun getInvoiceDetailsById(
        invoiceId: String,
        callback: DataSource.OnResponseCallback<Invoice>
    ) {
        val include = JSONObject()
        try {
            include.put("include", JSONArray().apply {
                put("payments")
                put("jobCard")
            })

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        retroFitService.getInvoiceDetailsById(invoiceId, include)
            .enqueue(object : NetworkCallBack<Invoice> {
                override fun success(response: Response<Invoice>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun getInvoiceAndInsuranceById(
        invoiceId: String,
        callback: DataSource.OnResponseCallback<Invoice>
    ) {
        val include = JSONObject()

        try {
            include.put("include", JSONArray().apply {
                put("insurancePdf")
                put("igstCustomerPdf")
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        retroFitService.getPdf(invoiceId, include).enqueue(object : NetworkCallBack<Invoice> {
            override fun success(response: Response<Invoice>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun searchInvoices(
        search: String,
        query: String,
        type: String,
        callback: DataSource.OnResponseCallback<List<Invoice>>
    ) {
        val order = "createdOn DESC"
        retroFitService.searchInvoices(search, query, type, order)
            .enqueue(object : NetworkCallBack<List<Invoice>> {
                override fun success(response: Response<List<Invoice>>) {
                    Timber.d("response : " + response.body()!!.toString())
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        Timber.d("Throwable : " + t.message)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    // Part

    override fun savePart(id: String, part: Part, callback: DataSource.OnResponseCallback<Part>) {
        retroFitService.savePart(id, part).enqueue(object : NetworkCallBack<Part> {
            override fun success(response: Response<Part>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun deletePart(
        invoiceId: String,
        partId: String,
        callback: DataSource.OnResponseCallback<Invoice>
    ) {
        retroFitService.deletePart(invoiceId, partId).enqueue(object : NetworkCallBack<Invoice> {
            override fun success(response: Response<Invoice>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun fetchBrandName(
        query: String,
        jobCardId: String,
        partNumber: String?,
        vehicleType: String?,
        callback: DataSource.OnResponseCallback<List<BrandName>>
    ) {
        retroFitService.fetchBrandName(query, jobCardId, partNumber, vehicleType)
            .enqueue(object : NetworkCallBack<List<BrandName>> {
                override fun success(response: Response<List<BrandName>>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun fetchPartNumber(
        query: String,
        partId: String?,
        jobCardId: String?,
        brandId: String?,
        vehicleType: String?,
        callback: DataSource.OnResponseCallback<List<PartNumber>>
    ) {
        retroFitService.fetchPartNumber(
            PartNumberSearchRequest(
                query,
                partId,
                jobCardId,
                brandId,
                false,
                vehicleType
            )
        ).enqueue(object : NetworkCallBack<List<PartNumber>> {
            override fun success(response: Response<List<PartNumber>>) {
                callback.onSuccess(response.body()!!)
                Timber.d("NetWork Success$query")
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun searchPartNumber(
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
        callback: DataSource.OnResponseCallback<List<PartNumber>>
    ) {
        val partNumberSearchRequest: PartNumberSearchRequest = if (make != null) {
            PartNumberSearchRequest(
                query!!,
                make,
                model!!,
                variant!!,
                fuelType!!,
                showStock,
                vehicleType,
                filterMode
            )
        } else {
//            PartNumberSearchRequest(query, jobCardId, partId, showStock, vehicleType, filterMode)
            PartNumberSearchRequest(
                query,
                jobCardId,
                showStock,
                vehicleType,
                filterMode,
                packageId,
                partId
            )
        }
        partNumberSearchRequest.showStock
        retroFitService.searchPartNumber(partNumberSearchRequest)
            .enqueue(object : NetworkCallBack<List<PartNumber>> {
                override fun success(response: Response<List<PartNumber>>) {
                    callback.onSuccess(response.body()!!)
                    /*Timber.d("NetWork Success" + query);*/
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun searchInStockPartNumbers(
        query: String,
        jobCardId: String?,
        partId: String?,
        brandId: String?,
        vehicleType: String?,
        packageId: String?,
        callback: DataSource.OnResponseCallback<List<PartNumber>>
    ) {
        val partNumberSearchRequest =
            PartNumberSearchRequest(query, partId, jobCardId, brandId, true, vehicleType)
        retroFitService.searchInStockPartNumbers(partNumberSearchRequest)
            .enqueue(object : NetworkCallBack<List<PartNumber>> {
                override fun success(response: Response<List<PartNumber>>?) {
                    callback.onSuccess(response!!.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    // Labour

    override fun saveLabour(
        id: String,
        labour: Labour,
        callback: DataSource.OnResponseCallback<Labour>
    ) {
        retroFitService.saveLabour(id, labour).enqueue(object : NetworkCallBack<Labour> {
            override fun success(response: Response<Labour>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun deleteLabour(
        invoiceId: String,
        labourId: String,
        callback: DataSource.OnResponseCallback<Invoice>
    ) {
        retroFitService.deleteLabour(invoiceId, labourId)
            .enqueue(object : NetworkCallBack<Invoice> {
                override fun success(response: Response<Invoice>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun completeJobCard(
        jobCardId: String,
        notify: Boolean,
        reasonForDelay: String?,
        callback: DataSource.OnResponseCallback<NetworkPostResponse>
    ) {

        retroFitService.completeJobCard(jobCardId, notify, reasonForDelay)
            .enqueue(object : NetworkCallBack<NetworkPostResponse> {
                override fun success(response: Response<NetworkPostResponse>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun closeJobCard(
        jobCardId: String,
        reminderDate: String?,
        notify: Boolean,
        callback: DataSource.OnResponseCallback<NetworkPostResponse>
    ) {
        retroFitService.closeJobCard(jobCardId, reminderDate, notify)
            .enqueue(object : NetworkCallBack<NetworkPostResponse> {
                override fun success(response: Response<NetworkPostResponse>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun getPrintEstimate(jobCardId: String, callback: DataSource.OnResponseCallback<PDF>) {
        retroFitService.createEstimatePdf(jobCardId).enqueue(object : NetworkCallBack<PDF> {
            override fun success(response: Response<PDF>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun getPrintJobCardPdc(
        jobCardId: String,
        callback: DataSource.OnResponseCallback<PDF>
    ) {
        retroFitService.createPdcPdf(jobCardId).enqueue(object : NetworkCallBack<PDF> {
            override fun success(response: Response<PDF>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun getProformaPDF(
        invoiceId: String,
        callback: DataSource.OnResponseCallback<List<PDF>>
    ) {
        retroFitService.createProformaPdf(invoiceId).enqueue(object : NetworkCallBack<List<PDF>> {
            override fun success(response: Response<List<PDF>>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun createInvoice(
        invoiceId: String,
        notify: Boolean,
        reminderDate: String?,
        callback: DataSource.OnResponseCallback<List<PDF>>
    ) {
        retroFitService.createInvoice(invoiceId, notify, reminderDate)
            .enqueue(object : NetworkCallBack<List<PDF>> {
                override fun success(response: Response<List<PDF>>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun getJobCardPreview(
        jobCardId: String,
        callback: DataSource.OnResponseCallback<PDF>
    ) {
        retroFitService.getJobCardPreview(jobCardId).enqueue(object : NetworkCallBack<PDF> {
            override fun success(response: Response<PDF>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun getInvoicePreview(
        invoiceId: String,
        callback: DataSource.OnResponseCallback<Invoice>
    ) {
        val filter = JSONObject()
        try {
            filter.put("include", JSONArray().apply {
                put("pdf")
                put("insurancePdf")
                put("igstCustomerPdf")
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        retroFitService.getPdf(invoiceId, filter).enqueue(object : NetworkCallBack<Invoice> {
            override fun success(response: Response<Invoice>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun getGatePassPreview(
        jobCardId: String,
        callback: DataSource.OnResponseCallback<PDF>
    ) {
        retroFitService.gatePass(jobCardId).enqueue(object : NetworkCallBack<PDF> {
            override fun success(response: Response<PDF>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun clientError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun serverError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun networkError(e: IOException) {
                callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun getAmcInvoicePdf(
        vehicleAmcId: String,
        callback: DataSource.OnResponseCallback<PDF>
    ) {
        //{"include":{"relation":"invoice","scope":{"include":"pdf"}}}

        val filter = JSONObject()
        try {
            filter.put("include", JSONObject().apply {
                put("relation", "invoice")
                put("scope", JSONObject().apply {
                    put("include", "pdf")
                })
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        retroFitService.getAmcInvoice(vehicleAmcId, filter).enqueue(object : NetworkCallBack<PDF> {
            override fun success(response: Response<PDF>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun clientError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun serverError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun networkError(e: IOException) {
                callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

        })
    }

    override fun getProformaEstimatePdf(
        invoiceId: String,
        callback: DataSource.OnResponseCallback<PDF>
    ) {
        retroFitService.getProformaEstimatePdf(invoiceId).enqueue(object : NetworkCallBack<PDF> {
            override fun success(response: Response<PDF>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun clientError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun serverError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun networkError(e: IOException) {
                callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override suspend fun getInvoicePdfs(invoiceId: String): Result<List<PDF>> {
        return try {
            val response = retroFitService.getInvoicePdfs(invoiceId)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(
                    errorWrapperHelper.transformToErrorWrapper(
                        response.errorBody()?.string()
                    )
                )
            }
        } catch (e: Exception) {
            Result.Error(errorWrapperHelper.transformToErrorWrapper(e))
        }
    }

    override fun getUserConfig(callback: DataSource.OnResponseCallback<User>) {
        retroFitService.getWorkshopConfig.enqueue(object : NetworkCallBack<User> {
            override fun success(response: Response<User>) {
                if (response.body() != null) {
                    //JobCard config
                    if (response.body()!!.customerGroup != null) {
                        SharedPrefHelper.customerGroup(response.body()!!.customerGroup.enabled)
                    }
                    //JobCard config
                    if (response.body()!!.getJCConfig() != null) {
                        SharedPrefHelper.setAllowAddCustomPart(
                            response.body()!!.getJCConfig().allowNewPart!!
                        )
                        SharedPrefHelper.setIsLabourEnabled(
                            response.body()!!.getJCConfig().allowNewLabour!!
                        )
                        SharedPrefHelper.setIsPartPriceEditable(
                            response.body()!!.getJCConfig().allowPartRateChange!!
                        )
                        SharedPrefHelper.setIsLabourRateEditable(
                            response.body()!!.getJCConfig().allowLabourRateChange!!
                        )
                        SharedPrefHelper.setIsJobCardClosureAllowed(
                            response.body()!!.getJCConfig().allowJobCardClosure!!
                        )
                        SharedPrefHelper.setHsnEnabled(
                            response.body()!!.getJCConfig().hsnEditable!!.enabled!!
                        )
                        SharedPrefHelper.setMrnEnabled(
                            response.body()!!.getJCConfig().allowMrnEstimate
                                ?: false
                        )
                        SharedPrefHelper.setHsnPartEnabled(
                            response.body()!!.getJCConfig().hsnEditable!!.part!!
                        )
                        SharedPrefHelper.setIsHsnLabourEnabled(
                            response.body()!!.getJCConfig().hsnEditable!!.labour!!
                        )
                        SharedPrefHelper.setJcFlow(
                            JC_FLOW_FLEXIBLE == response.body()!!.getJCConfig().flow
                        )
                        SharedPrefHelper.setLabourSurcharge(
                            response.body()!!.getJCConfig().allowLabourSurcharges?.enabled
                                ?: false
                        )
                        //Labour Surcharge
                        SharedPrefHelper.setLabourSurchargeMaxAmount(
                            response.body()!!
                                .getJCConfig().allowLabourSurcharges?.maxMechanicalLimit
                                ?: 0.0
                        )
                        //Labour Reduction
                        SharedPrefHelper.setLabourReduction(
                            response.body()!!.getJCConfig().allowLabourReduction?.enabled
                                ?: false
                        )
                        //pre delivery check
                        SharedPrefHelper.preDeliveryCheckEnabled(
                            response.body()!!.getJCConfig().preDelivery?.enabled
                                ?: false
                        )
                        //pre delivery strict
                        SharedPrefHelper.preDeliveryCheckMode(
                            response.body()!!.getJCConfig().preDelivery?.mode
                                ?: ""
                        )
                        //Customer Approval
                        SharedPrefHelper.customerApproval(
                            response.body()!!.getJCConfig().approvals?.enabled ?: false
                        )
                        //OSL Work Order config
                        SharedPrefHelper.setOSLWorkOrder(
                            response.body()!!.getJCConfig().workOrder?.enabled
                                ?: false
                        )
                        //Approvals config
                        SharedPrefHelper.setApproval(
                            response.body()!!.getJCConfig().approvals?.enabled
                                ?: false
                        )
                    }

                    if (response.body()!!.miscLabour != null) {
                        Timber.d(response.body()!!.miscLabour.toString())
                        SharedPrefHelper.setMiscEnabled(response.body()!!.miscLabour.enabled!!)
                        SharedPrefHelper.setMaxMiscPrice(response.body()!!.miscLabour.maxPrice!!)
                        SharedPrefHelper.setMiscId(response.body()!!.miscLabour.id)
                    }

                    //Appointment Config
                    if (response.body()!!.appointmentConfig != null) {
                        SharedPrefHelper.isAppointmentActive(response.body()!!.appointmentConfig.enabled)
                        SharedPrefHelper.setAppointmentServicePackageActive(response.body()!!.appointmentConfig.packages.enabled)
                        SharedPrefHelper.setAppointmentServicePackageMandatory(response.body()!!.appointmentConfig.packages.mandatory)
                    } else {
                        SharedPrefHelper.isAppointmentActive(false)
                    }
                    //Access to service advisor
                    response.body()!!.accessConfig?.let {
                        SharedPrefHelper.setServiceAdvisorEnabled(it.serviceAdvisor && it.enabled)
                    }

                    //vehicle type values
                    if (response.body()!!.vehicleType != null) {
                        SharedPrefHelper.setWorkshopVehicleType(response.body()!!.vehicleType.values)
                    }
                    //GST config
                    SharedPrefHelper.setGstEnabled(response.body()!!.isGstEnabled)
                    if (response.body()!!.inventoryConfig != null) {
                        SharedPrefHelper.isInventoryEnabled(response.body()!!.inventoryConfig.isEnabled)
                    }

                    //Part finder
                    if (response.body()!!.partFinder != null) {
                        SharedPrefHelper.isPartFinderEnable(response.body()!!.partFinder.enabled)
                    }

                    //Accidental config
                    if (response.body()!!.accidentalConfig != null) {
                        SharedPrefHelper.setAccidentalEnabled(response.body()!!.accidentalConfig.enabled)
                    }

                    //AMC config
                    if (response.body()!!.amc != null) {
                        SharedPrefHelper.setAmcEnabled(response.body()!!.amc.enabled)
                    }

                    //Service Package config
                    if (response.body()!!.servicePackageConfig != null) {
                        SharedPrefHelper.setPackagesEnabled(response.body()!!.servicePackageConfig!!.enabled)
                    }

                    //OTC config
                    if (response.body()!!.otcConfig != null) {
                        SharedPrefHelper.setOtcEnabled(response.body()!!.otcConfig.enabled)
                    }
                    //door step service
                    response.body()!!.doorStep?.let {
                        SharedPrefHelper.setDoorStepEnabled(it.enabled)
                    }

                    //customer source values
                    if (response.body()!!.customerSource != null && response.body()!!.customerSource!!.enabled!!) {
                        SharedPrefHelper.setCustomerSource(response.body()!!.customerSource!!.options)
                    }

                    //branding enabled
                    if (response.body()!!.branding != null) {
                        SharedPrefHelper.setBrandingEnabled(response.body()!!.branding.enabled)
                    } else {
                        SharedPrefHelper.setBrandingEnabled(false)
                    }
                    //Notification config
                    response.body()?.notification?.let { notificationConfig ->

                        SharedPrefHelper.setNotifyEnabled(notificationConfig.enabled)

                        notificationConfig.whatsapp?.let { whatsAppConfig ->

                            SharedPrefHelper.setNotifyByWhatsApp(whatsAppConfig.enabled)

                            // create jobcard config
                            SharedPrefHelper.setNotifyOnCreateJC(
                                whatsAppConfig.createJobCard?.enabled
                                    ?: false
                            )
                            SharedPrefHelper.setDefaultOptionCreateJC(
                                whatsAppConfig.createJobCard?.checked
                                    ?: false
                            )

                            // complete jobcard config
                            SharedPrefHelper.setNotifyOnCompleteJC(
                                whatsAppConfig.completeJobCard?.enabled
                                    ?: false
                            )
                            SharedPrefHelper.setDefaultOptionCompleteJC(
                                whatsAppConfig.completeJobCard?.checked
                                    ?: false
                            )

                            // close jobcard config
                            SharedPrefHelper.setNotifyOnCloseJC(
                                whatsAppConfig.closeJobCard?.enabled
                                    ?: false
                            )
                            SharedPrefHelper.setDefaultOptionCloseJC(
                                whatsAppConfig.closeJobCard?.checked
                                    ?: false
                            )

                            // cancel invoice config
                            SharedPrefHelper.setNotifyOnCancelInvoice(
                                whatsAppConfig.cancelInvoice?.enabled
                                    ?: false
                            )
                            SharedPrefHelper.setDefaultOptionCancelInvoice(
                                whatsAppConfig.cancelInvoice?.checked
                                    ?: false
                            )

                            // create invoice config
                            SharedPrefHelper.setNotifyOnCreateInvoice(
                                whatsAppConfig.createInvoice?.enabled
                                    ?: false
                            )
                            SharedPrefHelper.setDefaultOptionCreateInvoice(
                                whatsAppConfig.createInvoice?.checked
                                    ?: false
                            )
                        }

                        notificationConfig.sms?.let { smsConfig ->

                            SharedPrefHelper.setSmsEnabled(smsConfig.enabled)

                            // create jobcard config
                            SharedPrefHelper.setNotifyOnCreateJC(
                                smsConfig.createJobCard?.enabled
                                    ?: false
                            )
                            SharedPrefHelper.setDefaultOptionCreateJC(
                                smsConfig.createJobCard?.checked
                                    ?: false
                            )

                            // complete jobcard config
                            SharedPrefHelper.setNotifyOnCompleteJC(
                                smsConfig.completeJobCard?.enabled
                                    ?: false
                            )
                            SharedPrefHelper.setDefaultOptionCompleteJC(
                                smsConfig.completeJobCard?.checked
                                    ?: false
                            )

                            // close jobcard config
                            SharedPrefHelper.setNotifyOnCloseJC(
                                smsConfig.closeJobCard?.enabled
                                    ?: false
                            )
                            SharedPrefHelper.setDefaultOptionCloseJC(
                                smsConfig.closeJobCard?.checked
                                    ?: false
                            )

                            // cancel invoice config
                            SharedPrefHelper.setNotifyOnCancelInvoice(
                                smsConfig.cancelInvoice?.enabled
                                    ?: false
                            )
                            SharedPrefHelper.setDefaultOptionCancelInvoice(
                                smsConfig.cancelInvoice?.checked
                                    ?: false
                            )

                            // create invoice config
                            SharedPrefHelper.setNotifyOnCreateInvoice(
                                smsConfig.createInvoice?.enabled
                                    ?: false
                            )
                            SharedPrefHelper.setDefaultOptionCreateInvoice(
                                smsConfig.createInvoice?.checked
                                    ?: false
                            )
                        }
                    }
                    //invoice config
                    if (response.body()!!.invoiceConfig != null) {
                        SharedPrefHelper.setCompositeEnabled(
                            response.body()?.invoiceConfig?.compositeScheme?.enabled
                                ?: false
                        )
                    }

                    //validation config
                    if (response.body()!!.validationConfig != null) {
                        SharedPrefHelper.setInsuranceValidationEnabled(response.body()!!.validationConfig.insurance)
                    }

                    //reason for delay
                    if (response.body()!!.getJCConfig().allowReasonForDelay != null) {
                        SharedPrefHelper.allowReasonForDelay(
                            response.body()!!.getJCConfig().allowReasonForDelay?.enabled!!
                        )
                    }

                    //FOC
                    if (response.body()!!.isAllowFOC != null) {
                        SharedPrefHelper.setFOC(response.body()!!.isAllowFOC.enabled)
                    }



                    callback.onSuccess(response.body()!!)
                    Timber.d("UserConfig", response.body())
                }

                EventsManager.postSticky(ToggleMenuItemsEvent())
            }

            override fun unauthenticated(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("get gst status error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun clientError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("get gst status error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun serverError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("get gst status error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    Timber.d("get gst status error", e)

                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun checkForceUpdate(
        appName: String,
        platform: String,
        versionCode: Int,
        callback: DataSource.OnResponseCallback<AppUpdate>?
    ) {
        retroFitService.checkForceUpdate(appName, platform, versionCode)
            .enqueue(object : NetworkCallBack<AppUpdate> {
                override fun success(response: Response<AppUpdate>) {
                    callback?.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback?.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback?.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback?.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    callback?.onError(errorWrapperHelper.transformToErrorWrapper(e))
                }

                override fun unexpectedError(t: Throwable) {
                    callback?.onError(errorWrapperHelper.transformToErrorWrapper(t))
                }
            })
    }

    override fun getHSN(callback: DataSource.OnResponseCallback<List<HSN>>) {
        retroFitService.getHsn.enqueue(object : NetworkCallBack<List<HSN>> {
            override fun success(response: Response<List<HSN>>) {
                Timber.d("fetched network", response.body()!!.toString())
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
            }

            override fun unexpectedError(t: Throwable) {
                callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
            }
        })
    }

    override fun saveHSN(hsn: List<HSN>) {
    }

    override fun deleteHSN(callback: DataSource.OnResponseCallback<Boolean>) {
    }

    override fun getAppointments(
        type: String,
        skip: Int,
        limit: Int,
        callback: DataSource.OnResponseCallback<PaginationList<Appointment>>
    ) {
        retroFitService.getAppointment(AppointmentQueryWrapper(type, skip, limit))
            .enqueue(object : NetworkCallBack<List<Appointment>> {
                override fun success(response: Response<List<Appointment>>) {
                    callback.onSuccess(
                        PaginationList.getPaginationList(
                            response.body()!!,
                            MetaDataMapper.toObject(response)
                        )
                    )
                }

                override fun unauthenticated(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error" + response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Timber.e(e.message)
                    }
                }

                override fun clientError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error" + response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Timber.e(e.message)
                    }
                }

                override fun serverError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error" + response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Timber.e(e.message)
                    }
                }

                override fun networkError(e: IOException) {

                    callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                    Timber.d("Error" + e.message)
                    e.printStackTrace()
                }

                override fun unexpectedError(t: Throwable) {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                    Timber.d("Error" + t.message)
                    t.printStackTrace()
                }
            })
    }

    override fun cancelAppointment(
        appointmentId: String,
        callback: DataSource.OnResponseCallback<Appointment>
    ) {
        retroFitService.cancelAppointment(appointmentId)
            .enqueue(object : NetworkCallBack<Appointment> {
                override fun success(response: Response<Appointment>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        Timber.d("Error", e)

                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        Timber.d("Error", t)

                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun reassignAppointment(
        appointmentId: String,
        serviceAdvisorId: String,
        callback: DataSource.OnResponseCallback<NetworkPostResponse>
    ) {
        retroFitService.reassignAppointment(appointmentId, serviceAdvisorId)
            .enqueue(object : NetworkCallBack<NetworkPostResponse> {
                override fun success(response: Response<NetworkPostResponse>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        Timber.d("Error", e)

                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        Timber.d("Error", t)

                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun getAppointmentsByID(
        appointmentId: String,
        callback: DataSource.OnResponseCallback<Appointment>
    ) {
        val filter = JSONObject()
        try {
            filter.put("include", "serviceAdvisor")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        retroFitService.getAppointmentById(appointmentId, filter)
            .enqueue(object : NetworkCallBack<Appointment> {
                override fun success(response: Response<Appointment>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun clientError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun serverError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        Timber.d("Error", e)

                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
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
        val payment = Payment(
            null, paymentType, method,
            amount,
            invoiceId, transactionNumber, transactionDetails, bankName, cardNumber,
            drawnOnDate, chequeDate, chequeNumber, remarks, null, null, notifyCustomer
        )

        retroFitService.updatePaymentV1(
            id = invoiceId,
            payment
        ).enqueue(object : NetworkCallBack<NetworkPostResponse> {
            override fun success(response: Response<NetworkPostResponse>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun clientError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun serverError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    Timber.d("Error", e)

                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun getSourceTypes(callback: DataSource.OnResponseCallback<List<CustomerSourceType>>) {
        retroFitService.getSourceTypes()
            .enqueue(object : NetworkCallBack<List<CustomerSourceType>> {
                override fun success(response: Response<List<CustomerSourceType>>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun clientError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun serverError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        Timber.d("Error", e)

                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun getSourcesById(
        sourceId: String,
        callback: DataSource.OnResponseCallback<List<CustomerSource>>
    ) {
        retroFitService.getSourcesById(sourceId)
            .enqueue(object : NetworkCallBack<List<CustomerSource>> {
                override fun success(response: Response<List<CustomerSource>>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun clientError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun serverError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        Timber.d("Error", e)

                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun getReceiptDetails(
        invoiceId: String,
        callback: DataSource.OnResponseCallback<List<Payment>>
    ) {
        retroFitService.getReceiptDetails(invoiceId)
            .enqueue(object : NetworkCallBack<List<Payment>> {
                override fun success(response: Response<List<Payment>>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun clientError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun serverError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        Timber.d("Error", e)

                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

            })
    }

    override fun getReceiptPdf(receiptId: String, callback: DataSource.OnResponseCallback<PDF>) {
        retroFitService.getReceiptPDF(receiptId).enqueue(object : NetworkCallBack<PDF> {
            override fun success(response: Response<PDF>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun clientError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun serverError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    Timber.d("Error", e)

                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

        })
    }

    override fun getVehicleDetails(
        registrationNumber: String,
        callback: DataSource.OnResponseCallback<List<Vehicle>>
    ) {
        val registration = JSONObject()
        val query = JSONObject()
        try {
            registration.put("registrationNumber", registrationNumber.toUpperCase())
            query.put("where", registration)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        retroFitService.getVehicleDetails(query).enqueue(object : NetworkCallBack<List<Vehicle>> {
            override fun success(response: Response<List<Vehicle>>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun clientError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun serverError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    Timber.d("Error", e)

                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun getCustomerDetails(
        mobileNumber: String,
        callback: DataSource.OnResponseCallback<List<Customer>>
    ) {
        val mobile = JSONObject()
        val query = JSONObject()
        val include = JSONArray()
        try {
            mobile.put("mobile", mobileNumber)
            query.put("where", mobile)
            include.put("addresses")
            query.put("include", include)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        retroFitService.getCustomerDetails(query).enqueue(object : NetworkCallBack<List<Customer>> {
            override fun success(response: Response<List<Customer>>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun clientError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun serverError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    Timber.d("Error", e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun saveDetails(
        appointment: Appointment,
        callback: DataSource.OnResponseCallback<Appointment>
    ) {
        retroFitService.saveDetails(appointment).enqueue(object : NetworkCallBack<Appointment> {
            override fun success(response: Response<Appointment>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun clientError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun serverError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    Timber.d("Error", e)

                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun saveAppointmentStatus(
        appointmentStatus: AppointmentStatus,
        callback: DataSource.OnResponseCallback<Any>
    ) {
        retroFitService.updateAppointmentStatus(appointmentStatus)
            .enqueue(object : NetworkCallBack<Any> {
                override fun success(response: Response<Any>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun clientError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun serverError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        Timber.d("Error", e)

                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun getPackages(id: String, callback: DataSource.OnResponseCallback<Packages>) {
        retroFitService.getPackages(id).enqueue(object : NetworkCallBack<Packages> {
            override fun success(response: Response<Packages>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun clientError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun serverError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    Timber.d("Error", e)

                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun updatePackages(
        jobCardId: String,
        invoiceId: String,
        isInvoice: Boolean,
        packageIds: List<String>,
        callback: DataSource.OnResponseCallback<List<ServicePackage>>
    ) {
        val updatePackage = UpdatePackage(packageIds)
        val nwcallback = object : NetworkCallBack<JobCard> {
            override fun success(response: Response<JobCard>) {
                callback.onSuccess(response.body()!!.packages)
            }

            override fun unauthenticated(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun clientError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun serverError(response: Response<*>) {
                try {
                    callback.onError(
                        errorWrapperHelper.transformToErrorWrapper(
                            response.errorBody()!!.string()
                        )
                    )
                    Timber.d("Error", response.errorBody()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    Timber.d("Error", e)

                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        }
        if (isInvoice)
            retroFitService.updateInvoicePackages(invoiceId, updatePackage).enqueue(nwcallback)
        else
            retroFitService.updatePackages(jobCardId, updatePackage).enqueue(nwcallback)
    }

    override fun savePackages(
        appointmentId: String,
        appointmentPost: AppointmentPost,
        callback: DataSource.OnResponseCallback<Appointment>
    ) {

        retroFitService.savePackages(appointmentId, appointmentPost)
            .enqueue(object : NetworkCallBack<Appointment> {
                override fun success(response: Response<Appointment>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun clientError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun serverError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        Timber.d("Error", e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun getTimeSlot(
        appointmentId: String,
        dateTime: String,
        type: String?,
        callback: DataSource.OnResponseCallback<List<TimeSlot>>
    ) {
        retroFitService.getTimeSlot(appointmentId, dateTime)
            .enqueue(object : NetworkCallBack<List<TimeSlot>> {
                override fun success(response: Response<List<TimeSlot>>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun clientError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun serverError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        Timber.d("error", e)

                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun saveTimeSlot(
        appointmentId: String,
        dateTime: AppointmentPost,
        callback: DataSource.OnResponseCallback<Appointment>
    ) {
        retroFitService.saveTimeSlot(appointmentId, dateTime)
            .enqueue(object : NetworkCallBack<Appointment> {
                override fun success(response: Response<Appointment>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error" + response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun clientError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error" + response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun serverError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error" + response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        Timber.d("Error$e")

                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                        Timber.e(t.message)
                        t.printStackTrace()
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun upsertData(
        appointmentId: String,
        upsertDetails: UpsertDetails,
        callback: DataSource.OnResponseCallback<UpsertDetails>
    ) {
        retroFitService.upsertDetails(appointmentId, upsertDetails)
            .enqueue(object : NetworkCallBack<UpsertDetails> {
                override fun success(response: Response<UpsertDetails>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun clientError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun serverError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        Timber.d("Error", e)

                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun rescheduleAppointment(
        appointmentId: String,
        reschedule: Reschedule,
        callback: DataSource.OnResponseCallback<Reschedule>
    ) {
        retroFitService.rescheduleAppointment(appointmentId, reschedule)
            .enqueue(object : NetworkCallBack<Reschedule> {
                override fun success(response: Response<Reschedule>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun clientError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun serverError(response: Response<*>) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                        Timber.d("Error", response.errorBody()!!.string())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun networkError(e: IOException) {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun rejectAppointment(
        appointmentId: String,
        reschedule: Reschedule,
        callback: DataSource.OnResponseCallback<Reschedule>
    ) {
        retroFitService.rejectAppointment(appointmentId, reschedule)
            .enqueue(object : NetworkCallBack<Reschedule> {
                override fun success(response: Response<Reschedule>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun acceptAppointment(
        appointmentId: String,
        callback: DataSource.OnResponseCallback<Appointment>
    ) {
        retroFitService.acceptAppointment(appointmentId)
            .enqueue(object : NetworkCallBack<Appointment> {
                override fun success(response: Response<Appointment>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun getCustomerVehicleList(
        query: String?,
        filterList: List<String>?,
        startDate: String?,
        endDate: String?,
        skip: Int,
        limit: Int,
        callback: DataSource.OnResponseCallback<PaginationList<CustomerVehicleDetails>>
    ) {
        val obj = Query()
        val filter = FilterCustomer()
        val dateRange = DateRange()

        if (startDate != null || endDate != null) {
            dateRange.from = startDate
            dateRange.to = endDate
        }
        if (!filterList.isNullOrEmpty()) {
            when (filterList.first()) {
                FilterCustomer.IN_PROGRESS -> {
                    filter.inProgressDate = dateRange
                }
                FilterCustomer.COMPLETION_DATE -> {
                    filter.completionDate = dateRange
                }
                FilterCustomer.INVOICE_DATE -> {
                    filter.invoiceDate = dateRange
                }
                FilterCustomer.SERVICE_DATE -> {
                    filter.serviceDate = dateRange
                }
            }
        }

        if (query != null) {
            obj.q = query
        }

        obj.from = skip
        obj.size = limit
        obj.filter = filter

        retroFitService.getCustomerVehiclesList(obj)
            .enqueue(object : NetworkCallBack<List<CustomerVehicleDetails>> {
                override fun success(response: Response<List<CustomerVehicleDetails>>) {
                    callback.onSuccess(
                        PaginationList.getPaginationList(
                            response.body()!!,
                            MetaDataMapper.toObject(response)
                        )
                    )
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun getCustomerVehicleHistory(
        id: String,
        callback: DataSource.OnResponseCallback<CustomerVehicleDetails>
    ) {
        retroFitService.getCustomerVehiclesHistory(id)
            .enqueue(object : NetworkCallBack<CustomerVehicleDetails> {
                override fun success(response: Response<CustomerVehicleDetails>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun saveCostEstimation(
        id: String,
        costEstimate: CostEstimate,
        callback: DataSource.OnResponseCallback<JobCard>
    ) {
        retroFitService.saveEstimation(id, costEstimate).enqueue(object : NetworkCallBack<JobCard> {
            override fun success(response: Response<JobCard>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun updateProforma(
        invoiceId: String,
        invoice: Invoice,
        callback: DataSource.OnResponseCallback<Invoice>
    ) {
        retroFitService.updateProforma(invoiceId, invoice)
            .enqueue(object : NetworkCallBack<Invoice> {
                override fun success(response: Response<Invoice>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun saveRemarks(
        jobCardId: String,
        invoiceRemarks: InvoiceRemarks,
        callback: DataSource.OnResponseCallback<JobCard>
    ) {
        retroFitService.saveRemarks(jobCardId, invoiceRemarks)
            .enqueue(object : NetworkCallBack<JobCard> {
                override fun success(response: Response<JobCard>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    override fun getRemarks(
        jobCardId: String,
        callback: DataSource.OnResponseCallback<InvoiceRemarks>
    ) {
        retroFitService.getRemarks(jobCardId).enqueue(object : NetworkCallBack<InvoiceRemarks> {
            override fun success(response: Response<InvoiceRemarks>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun getWorkShopResources(callback: DataSource.OnResponseCallback<WorkshopResource>) {
        retroFitService.resources.enqueue(object : NetworkCallBack<WorkshopResource> {
            override fun success(response: Response<WorkshopResource>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun saveServiceReminder(
        vehicleId: String,
        date: String,
        callback: DataSource.OnResponseCallback<ServiceDate>
    ) {
        retroFitService.saveServiceReminder(vehicleId, date)
            .enqueue(object : NetworkCallBack<ServiceDate> {
                override fun success(response: Response<ServiceDate>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                        callback.onError(errorWrapper)
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }
            })
    }

    /*Accidental Start*/

    override fun saveAccidentalData(
        jobCardId: String,
        accidental: Accidental,
        callback: DataSource.OnResponseCallback<JobCard>
    ) {
        retroFitService.saveAccidental(jobCardId, accidental)
            .enqueue(object : NetworkCallBack<JobCard> {
                override fun success(response: Response<JobCard>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    override fun saveMissingAccidentalDetails(
        jobCardId: String,
        missingAccidentalDetails: MissingAccidentalDetails,
        callback: DataSource.OnResponseCallback<JobCard>
    ) {
        retroFitService.saveMissingAccidentalDetails(jobCardId, missingAccidentalDetails)
            .enqueue(object : NetworkCallBack<JobCard> {
                override fun success(response: Response<JobCard>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    override fun uploadDocument(
        fileObject: FileObject,
        callback: DataSource.OnResponseCallback<FileObject>
    ) {
        Timber.d("Uri : " + fileObject.uri!!)

        val file = File(fileObject.uri!!)
        val requestBody = RequestBody.create(fileObject.mime!!.toMediaTypeOrNull(), file)
        val fileToUpload =
            MultipartBody.Part.createFormData("file", fileObject.originalName, requestBody)
        val reqFileName =
            RequestBody.create(fileObject.mime!!.toMediaTypeOrNull(), fileObject.originalName)
        val networkCall = nonIdempotentService.uploadDocument(
            fileObject.jobCardID!!,
            fileObject.meta!!.category,
            fileObject.originalName,
            fileToUpload,
            reqFileName
        )
        reqCache[fileObject.originalName] = networkCall
        networkCall.enqueue(object : NetworkCallBack<FileObject> {
            override fun success(response: Response<FileObject>) {
                callback.onSuccess(response.body()!!)
                reqCache.remove(fileObject.originalName)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    override suspend fun uploadDocument(document: FileObject): Result<FileObject> {
        val job = GlobalScope.async {
            val file = File(document.uri!!)
            val requestBody = RequestBody.create(document.mime!!.toMediaTypeOrNull(), file)
            val fileToUpload =
                MultipartBody.Part.createFormData("file", document.originalName, requestBody)
            val reqFileName =
                RequestBody.create(document.mime!!.toMediaTypeOrNull(), document.originalName)
            nonIdempotentService.uploadDocumentAsync(
                document.jobCardID!!,
                document.meta!!.category,
                document.originalName,
                fileToUpload,
                reqFileName
            )
        }
        reqCacheCoroutine[document.hashCode()] = job
        return try {
            val response = job.await()
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(
                    errorWrapperHelper.transformToErrorWrapper(
                        response.errorBody()?.string()
                    )
                )
            }
        } catch (e: Exception) {
            Result.Error(errorWrapperHelper.transformToErrorWrapper(e))
        } finally {
            reqCacheCoroutine.remove(document.hashCode())
        }
    }

    override fun updateDocument(
        originalFileName: String,
        imageId: String,
        url: String,
        isUploaded: Boolean,
        callback: DataSource.OnResponseCallback<FileObject>?
    ) {
    }

    override fun getDocuments(
        jobCardId: String,
        callback: DataSource.OnResponseCallback<List<FileObject>>
    ) {
        retroFitService.getDocuments(jobCardId).enqueue(object : NetworkCallBack<List<FileObject>> {
            override fun success(response: Response<List<FileObject>>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun saveDocuments(jobCardId: String, files: List<FileObject>) {
    }

    override fun deleteDocument(
        fileObject: FileObject,
        callback: DataSource.OnResponseCallback<FileObject>
    ) {
        if (reqCache.containsKey(fileObject.originalName)) {
            reqCache[fileObject.originalName]?.cancel()
        } else {
            retroFitService.deleteDocument(fileObject.jobCardID!!, fileObject.id!!)
                .enqueue(object : NetworkCallBack<FileObject> {
                    override fun success(response: Response<FileObject>) {
                        callback.onSuccess(response.body()!!)
                    }

                    override fun unauthenticated(response: Response<*>) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    override fun clientError(response: Response<*>) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    override fun serverError(response: Response<*>) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    override fun networkError(e: IOException) {
                        try {
                            callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                        } catch (exp: Exception) {
                            exp.printStackTrace()
                        }
                    }

                    override fun unexpectedError(t: Throwable) {
                        try {
                            callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
        }
    }

    override fun clearAccidentalCache() {
    }

    /*Accidental End*/

    override fun getInsurancePdf(invoiceId: String, callback: DataSource.OnResponseCallback<PDF>) {
        retroFitService.getInsurancePdf(invoiceId).enqueue(object : NetworkCallBack<PDF> {
            override fun success(response: Response<PDF>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun getCustomerPdf(
        invoiceId: String,
        callback: DataSource.OnResponseCallback<PDF>
    ) {
        retroFitService.getCustomerPdf(invoiceId).enqueue(object : NetworkCallBack<PDF> {
            override fun success(response: Response<PDF>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun getIgstCustomerPdf(
        invoiceId: String,
        callback: DataSource.OnResponseCallback<PDF>
    ) {
        retroFitService.getIgstCustomerPdf(invoiceId).enqueue(object : NetworkCallBack<PDF> {
            override fun success(response: Response<PDF>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(e)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    val errorWrapper = errorWrapperHelper.transformToErrorWrapper(t)
                    callback.onError(errorWrapper)
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }
        })
    }

    override fun saveLead(
        leadId: String,
        leadForm: LeadForm,
        callback: DataSource.OnResponseCallback<LeadForm>
    ) {
        retroFitService.saveLead(leadId, leadForm).enqueue(object : NetworkCallBack<LeadForm> {
            override fun success(response: Response<LeadForm>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun addOtcProforma(
        customerId: String,
        addressId: String,
        vehicleType: String?,
        callback: DataSource.OnResponseCallback<Invoice>
    ) {
        retroFitService.addOtcProforma(customerId, addressId, vehicleType)
            .enqueue(object : NetworkCallBack<Invoice> {
                override fun success(response: Response<Invoice>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    override fun getDashBoardDetails(callback: DataSource.OnResponseCallback<WorkshopResource>) {
        retroFitService.dashBoardDetails.enqueue(object : NetworkCallBack<WorkshopResource> {
            override fun success(response: Response<WorkshopResource>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun saveSignature(
        jobCardId: String,
        file: File,
        callback: DataSource.OnResponseCallback<Signature>
    ) {
        val requestBody = RequestBody.create("image/svg+xml".toMediaTypeOrNull(), file)
        val fileToUpload = MultipartBody.Part.createFormData("file", file.name, requestBody)
        val reqFileName = RequestBody.create("image/svg+xml".toMediaTypeOrNull(), file.name)

        retroFitService.saveSignature(jobCardId, fileToUpload, reqFileName)
            .enqueue(object : NetworkCallBack<Signature> {
                override fun success(response: Response<Signature>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    override fun saveQuickJobCard(
        jobCardId: String,
        jobCard: JobCard,
        callback: DataSource.OnResponseCallback<JobCard>
    ) {
        retroFitService.saveQuickJobCard(jobCardId, jobCard)
            .enqueue(object : NetworkCallBack<JobCard> {
                override fun success(response: Response<JobCard>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    override fun fetchUnSavedImages(callback: DataSource.OnResponseCallback<List<FileObject>>) {
        throw UnsupportedOperationException("cannot fetch un saved images from network")
    }

    override fun saveCustomerFeedback(
        jobcardId: String,
        recommendedScore: Int,
        serviceQuality: Float,
        billingTransparency: Float,
        timelyDelivery: Float,
        comments: String?,
        callback: DataSource.OnResponseCallback<NetworkPostResponse>
    ) {
        retroFitService.saveFeedback(
            jobcardId,
            recommendedScore,
            serviceQuality,
            billingTransparency,
            timelyDelivery,
            comments,
            "APP"
        ).enqueue(object : NetworkCallBack<NetworkPostResponse> {
            override fun success(response: Response<NetworkPostResponse>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun getServiceAdvisors(
        workshopId: String,
        callback: DataSource.OnResponseCallback<List<WorkshopAdviser>>
    ) {
        retroFitService.getWorkshopAdvisers(workshopId)
            .enqueue(object : NetworkCallBack<List<WorkshopAdviser>> {
                override fun success(response: Response<List<WorkshopAdviser>>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    override fun registerDevice(
        deviceId: String,
        fcmTokenObj: FcmTokenEntity,
        callback: DataSource.OnResponseCallback<NetworkPostResponse>
    ) {
        val deviceUid = JSONObject().apply {
            put("deviceId", deviceId)
        }
        retroFitService.registerDevice(deviceUid, fcmTokenObj)
            .enqueue(object : NetworkCallBack<NetworkPostResponse> {
                override fun success(response: Response<NetworkPostResponse>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    override fun getWhatsAppTemplate(
        templateType: String,
        id: String,
        callback: DataSource.OnResponseCallback<WhatsAppTemplate>
    ) {

        val json = JSONObject()
            .apply {
                put("action", templateType)
                put("id", id)
            }
        retroFitService.getWhatsAppTemplate(json)
            .enqueue(object : NetworkCallBack<WhatsAppTemplate> {
                override fun success(response: Response<WhatsAppTemplate>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    override fun getStates(callback: DataSource.OnResponseCallback<List<State>>) {
        retroFitService.getStates().enqueue(object : NetworkCallBack<List<State>> {
            override fun success(response: Response<List<State>>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun saveStates(
        states: List<State>,
        callback: DataSource.OnResponseCallback<List<State>>?
    ) = Unit

    override suspend fun saveThirdPartyDetails(
        invoiceId: String,
        thirdParty: ThirdParty
    ): Result<ThirdParty> {
        val job = GlobalScope.async {
            retroFitService.updateThirdPartyDetails(invoiceId, thirdParty)
        }
        return try {
            val response = job.await()
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(
                    errorWrapperHelper.transformToErrorWrapper(
                        response.errorBody()?.string()
                    )
                )
            }
        } catch (e: Exception) {
            Result.Error(errorWrapperHelper.transformToErrorWrapper(e))
        }
    }

    override suspend fun removeThirdPartyDetails(
        invoiceId: String,
        thirdParty: ThirdParty
    ): Result<ThirdParty> {
        TODO("NOT IMPLEMENTED")
    }

    override fun getOtherHistory(
        registrationNumber: String,
        mobileNumber: String,
        callback: DataSource.OnResponseCallback<CustomerVehicleDetails>
    ) {
        /*val body =HashMap<String,String>()
        body.put("registrationNumber",registrationNumber)
        body.put("mobile",mobileNumber)*/
        retroFitService.getOtherSysHistory(Options(registrationNumber, mobileNumber))
            .enqueue(object : NetworkCallBack<CustomerVehicleDetails> {
                override fun success(response: Response<CustomerVehicleDetails>) {
                    callback.onSuccess(response.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    override fun getRedemptionDetails(
        vehicleAmcId: String,
        callback: DataSource.OnResponseCallback<SoldAMCDetails>
    ) {
        retroFitService.getRedemptionDetails(vehicleAmcId)
            .enqueue(object : NetworkCallBack<SoldAMCDetails> {
                override fun success(response: Response<SoldAMCDetails>?) {
                    callback.onSuccess(response?.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    override fun cancelAMC(
        vehicleAmcId: String,
        reason: String,
        callback: DataSource.OnResponseCallback<SoldAMCDetails>
    ) {
        retroFitService.cancelVehicleAMC(vehicleAmcId, reason)
            .enqueue(object : NetworkCallBack<SoldAMCDetails> {
                override fun success(response: Response<SoldAMCDetails>?) {
                    callback.onSuccess(response?.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    override fun getVendorList(callback: DataSource.OnResponseCallback<List<Vendor>>) {
        retroFitService.getVendorList().enqueue(object : NetworkCallBack<List<Vendor>> {
            override fun success(response: Response<List<Vendor>>) {
                callback.onSuccess(response.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun getPdcChecklist(
        jobCardId: String,
        callback: DataSource.OnResponseCallback<List<PdcBase>>
    ) {
        retroFitService.getPdcChecklist(jobCardId).enqueue(object : NetworkCallBack<List<PdcBase>> {
            override fun success(response: Response<List<PdcBase>>?) {
                callback.onSuccess(response?.body()!!)
            }

            override fun unauthenticated(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun clientError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun serverError(response: Response<*>) {
                if (response.errorBody() != null) {
                    try {
                        callback.onError(
                            errorWrapperHelper.transformToErrorWrapper(
                                response.errorBody()!!.string()
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun networkError(e: IOException) {
                try {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                } catch (exp: Exception) {
                    exp.printStackTrace()
                }
            }

            override fun unexpectedError(t: Throwable) {
                try {
                    callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        })
    }

    override fun saveCustomerGroup(options: List<Option>) {
        TODO("Not yet implemented")
    }

    override fun getCustomerGroup(callback: DataSource.OnResponseCallback<List<Option>>) {
        TODO("Not yet implemented")
    }

    override fun deleteCustomerGroup(callback: DataSource.OnResponseCallback<Boolean>) {
        TODO("Not yet implemented")
    }

    override fun postPdcChecklist(
        jobCardId: String,
        pdcEntity: PdcEntity,
        callback: DataSource.OnResponseCallback<NetworkPostResponse>
    ) {
        retroFitService.postPdcChecklist(jobCardId, pdcEntity)
            .enqueue(object : NetworkCallBack<NetworkPostResponse> {
                override fun success(response: Response<NetworkPostResponse>?) {
                    callback.onSuccess(response?.body()!!)
                }

                override fun unauthenticated(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun clientError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun serverError(response: Response<*>) {
                    if (response.errorBody() != null) {
                        try {
                            callback.onError(
                                errorWrapperHelper.transformToErrorWrapper(
                                    response.errorBody()!!.string()
                                )
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun networkError(e: IOException) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(e))
                    } catch (exp: Exception) {
                        exp.printStackTrace()
                    }
                }

                override fun unexpectedError(t: Throwable) {
                    try {
                        callback.onError(errorWrapperHelper.transformToErrorWrapper(t))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            })
    }

    companion object {

        private const val TAG = "NetworkDataSource"
    }
}
