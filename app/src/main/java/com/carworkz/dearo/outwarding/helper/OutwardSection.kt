package com.carworkz.dearo.outwarding.helper

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OutwardSection(var title: String, var total: Double?) : OutwardStep, Parcelable
