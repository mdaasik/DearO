package com.carworkz.dearo.appointments.createappointment.servicePackages

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.R
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityPackageDetailBinding
import com.carworkz.dearo.domain.entities.ServicePackage
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.interactionprovider.ToolBarInteractionProvider
import com.carworkz.dearo.screencontainer.SingleTextActionScreenContainer
import com.carworkz.dearo.utils.Utility

class PackageDetailActivity : ScreenContainerActivity(), ToolBarInteractionProvider {
    private lateinit var binding: ActivityPackageDetailBinding
    private lateinit var packageContainer: LinearLayout
    private lateinit var serviceIncludeLayout: LinearLayout
    private lateinit var termsLayout: LinearLayout
    private lateinit var servicePackage: ServicePackage

    override fun createScreenContainer() = SingleTextActionScreenContainer(this)
    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityPackageDetailBinding.inflate(layoutInflater)
        return binding
    }

    override fun getToolBarTitle() = "Package Details"

    override fun getActionBtnTitle() = "OK"

    override fun onActionBtnClick() {
        finish()
    }


    override fun getProgressView(): View = ProgressBar(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        servicePackage = intent.extras!!.getParcelable(PACKAGE)!!
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        packageContainer = find(R.id.ll_package_container)
        val view = layoutInflater.inflate(R.layout.row_package_detail, packageContainer, false)
        val serviceNameView = view.find<TextView>(R.id.tv_package_name)
        val serviceTypeView = view.find<TextView>(R.id.tv_package_type)
        val serviceRateView = view.find<TextView>(R.id.tv_package_rate)
        val oilTypeView = view.find<TextView>(R.id.tv_oil_type)
        serviceNameView.text = servicePackage.name
        serviceTypeView.text = servicePackage.description
        var total = 0f
        if (servicePackage.offerPrice != null) {
            total += servicePackage.offerPrice?.amount ?: 0f
        } else {
            servicePackage.rates?.forEach { total += it.offerPrice.amount }
        }

        serviceRateView.text = Utility.convertToCurrency(total.toDouble()) // "Rs.$total"
        if (servicePackage.rates != null && servicePackage.rates!![0].engineOilType.isNotEmpty()) {
            oilTypeView.visibility = View.VISIBLE
            oilTypeView.text = servicePackage.rates!![0].engineOilType
        } else {
            oilTypeView.visibility = View.GONE
        }
        packageContainer.addView(view)

        serviceIncludeLayout = find(R.id.ll_service)
        termsLayout = find(R.id.ll_tandc)

        servicePackage.services?.indices?.forEach { pos ->
            TextView(this).let {
                @SuppressLint("SetTextI18n")
                it.text = "\u2022 ${servicePackage.services?.get(pos)?.text} "
                it.textColor = ContextCompat.getColor(this, R.color.black)
                serviceIncludeLayout.addView(it)
            }
        }

        servicePackage.terms?.indices?.forEach { pos ->
            TextView(this).let {
                @SuppressLint("SetTextI18n")
                it.text = " ${pos + 1}. ${servicePackage.terms?.get(pos)}"
                it.textColor = ContextCompat.getColor(this, R.color.black)
                termsLayout.addView(it)
            }
        }
    }

    companion object {
        const val PACKAGE = "package"
    }
}
