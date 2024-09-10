package com.carworkz.dearo

import com.carworkz.dearo.outwarding.OutwardingProcessActivity

interface PriceChangeListener {
    fun onPriceUpdate(itemType: String = OutwardingProcessActivity.NO_ITEM_TYPE, position: Int)
    fun onVendorSelectionClick(itemType: String = OutwardingProcessActivity.NO_ITEM_TYPE, position: Int)
}