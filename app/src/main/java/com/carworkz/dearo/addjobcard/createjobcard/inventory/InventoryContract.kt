package com.carworkz.dearo.addjobcard.createjobcard.inventory

import com.carworkz.dearo.base.BasePresenter
import com.carworkz.dearo.base.BaseView
import com.carworkz.dearo.domain.entities.Inventory

/**
 * Created by Farhan on 18/8/17.
 */
interface InventoryContract {

    interface View : BaseView<Presenter> {

        fun displayInventoryList(inventories: List<Inventory>)

        fun displayInventoryItems(serviceType: String, fuelReading: Int, kmsReading: Int, userComment: String)

        fun onInventorySaveSuccess()

        fun showServiceTypeError(message: String)

        fun showKmsReadingError(message: String)

        fun showInventoryListError(message: String)

        fun showRemarksError(message: String)

        fun showFuelReadingError(message: String)
    }

    interface Presenter : BasePresenter {

        fun getInventoryList(vehicleType: String?)

        fun getSelectedInventoryList(jobCardId: String)

        fun saveJobCardInventory(jobCardId: String, serviceType: String, fuelReading: Int, kmsReading: Int, inventory: List<Inventory>, remarks: String?)
    }
}