package com.carworkz.dearo.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ThirdParty(var isThirdParty: Boolean? = null, var name: String, var mobile: String?, var email: String?, var gstNumber: String, var address: Address) : Parcelable