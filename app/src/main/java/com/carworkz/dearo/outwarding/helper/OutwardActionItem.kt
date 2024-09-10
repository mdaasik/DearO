package com.carworkz.dearo.outwarding.helper

import com.carworkz.dearo.domain.entities.ThirdParty
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class OutwardActionItem(val thirdParty: ThirdParty?) : OutwardStep, Parcelable
