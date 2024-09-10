package com.carworkz.dearo.amc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.carworkz.dearo.R
import com.carworkz.dearo.databinding.BottomsheetAmcSellBinding
import com.carworkz.dearo.domain.entities.AMCPackage
import com.carworkz.dearo.domain.entities.Customer
import com.carworkz.dearo.domain.entities.Vehicle
import com.carworkz.dearo.utils.Utility
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AmcSellBottomSheet : BottomSheetDialogFragment() {
    private lateinit var amcPackage: AMCPackage
    private lateinit var vehicle: Vehicle
    private lateinit var customer: Customer
    private var listener: PurchaseListener? = null

    private var _binding: BottomsheetAmcSellBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            amcPackage = it.getParcelable(ARG_DATA)!!
            vehicle = it.getParcelable(VEHICLE)!!
            customer = it.getParcelable(CUSTOMER)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomsheetAmcSellBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            close.setOnClickListener { dismiss() }
            confirmPurchase.setOnClickListener {
                listener?.onPurchaseClicked(amcPackage)
                dismiss()
            }
            customerName.text = customer.name
            mobile.text = customer.mobile
            registrationNumber.text = vehicle.registrationNumber
            makeModel.text = "${vehicle.make.name} - ${vehicle.model.name} - ${vehicle.variant.fuelType}"
            amcPackageName.text = amcPackage.name
            packageDescription.text = amcPackage.description
            packagePrice.text = Utility.convertToCurrency(amcPackage.price)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_DATA = "arg-data"
        const val VEHICLE = "amc-vehicle"
        const val CUSTOMER = "amc-customer"

        fun newInstance(amcPackage: AMCPackage, vehicle: Vehicle, customer: Customer): AmcSellBottomSheet {
            return AmcSellBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_DATA, amcPackage)
                    putParcelable(VEHICLE, vehicle)
                    putParcelable(CUSTOMER, customer)
                }
            }
        }
    }

    fun setPurchaseListener(listener: PurchaseListener) {
        if (this.listener == null) {
            this.listener = listener
        }
    }

    interface PurchaseListener {
        fun onPurchaseClicked(amcPackage: AMCPackage)
    }
}
