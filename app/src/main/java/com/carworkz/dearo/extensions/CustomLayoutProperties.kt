package com.carworkz.dearo.extensions

import android.view.ViewGroup
import com.carworkz.dearo.extensions.AnkoInternals.NO_GETTER
import com.carworkz.dearo.extensions.AnkoInternals.noGetter

val matchParent: Int = ViewGroup.LayoutParams.MATCH_PARENT
val wrapContent: Int = ViewGroup.LayoutParams.WRAP_CONTENT

var ViewGroup.MarginLayoutParams.verticalMargin: Int
    @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR) get() = noGetter()
    set(v) {
        topMargin = v
        bottomMargin = v
    }

var ViewGroup.MarginLayoutParams.horizontalMargin: Int
    @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR) get() = noGetter()
    set(v) {
        leftMargin = v; rightMargin = v
    }

var ViewGroup.MarginLayoutParams.margin: Int
    @Deprecated(NO_GETTER, level = DeprecationLevel.ERROR) get() = noGetter()
    set(v) {
        leftMargin = v
        rightMargin = v
        topMargin = v
        bottomMargin = v
    }