
 package com.carworkz.dearo.cardslisting.adapters
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.addcustomer.AddCustomerActivity
import com.carworkz.dearo.addjobcard.addeditvehicle.VehicleDetailsActivity
import com.carworkz.dearo.cardslisting.AMCInteractionProvider
import com.carworkz.dearo.cardslisting.CardListingBaseAdapter
import com.carworkz.dearo.cardslisting.CardListingBaseViewHolder
import com.carworkz.dearo.domain.entities.AMC
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.morecta.MoreCtaListDialogFragment
import com.carworkz.dearo.utils.Utility
import java.util.*

class AmcCardListingAdapter(
    private val context: Context,
    private val amcList: MutableList<AMC>,
    private val interaction: AMCInteractionProvider
) : CardListingBaseAdapter<AmcCardListingAdapter.AmcCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmcCardViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.row_new_amc_card_listing, parent, false)
        return AmcCardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return amcList.size
    }

    fun updateList(updateList: List<AMC>) {
        val oldSize = itemCount
        amcList.addAll(updateList)
        notifyItemRangeInserted(oldSize, itemCount)
    }

    override fun onBindViewHolder(holder: AmcCardViewHolder, position: Int) {
        val amc = amcList[position]

        val chipText = " <b>${amc.amcCode}</b>"
        holder.chipView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(chipText, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(chipText)
        }

        (holder.chipView.background as? GradientDrawable)?.setColor(
            ContextCompat.getColor(
                context,
                android.R.color.holo_orange_dark
            )
        )
        holder.chipView.textColor = Color.WHITE
        holder.parentView.tag = amc
        holder.secondaryCtaParent.tag = amc
        holder.primaryCtaParent.tag = amc
        holder.callView.tag = amc.customer?.mobile

        holder.idView.text = StringBuilder().apply {
            append("\u2022 ")
            append(amc.amcNumber)
        }

        holder.titleView.text = amc.vehicle?.registrationNumber

        holder.makeModelView.text = StringBuilder().apply {
            // Used StringBuilder to avoid lint warning
            append(amc.vehicle?.make?.name)
            append("-")
            append(amc.vehicle?.model?.name)
            append("-")
            append(amc.vehicle?.fuelType)
        }

        holder.nameView.text = amc.customer?.name?.capitalize()

        holder.timeRemainingView.visibility = View.VISIBLE
        holder.timeRemainingView.setCompoundDrawables(null, null, null, null)

        //if AMC is cancelled -> "Cancelled On"
        //if AMC is Expired -> "Expired On"
        //else -> "Valid Till"
        var timeText = ""
        var primaryCtaText = ""

        val isExpired = Utility.dateToCalender(
            amc.expiryDate,
            Utility.TIMESTAMP
        ).time < Calendar.getInstance().time

        timeText = if (isExpired) "Expired On" else "Valid Till"
        primaryCtaText =
            if (isExpired) "Renew AMC".capitalize(Locale.ROOT) else "Create Job Card".capitalize(
                Locale.ROOT
            )

        if (amc.status == AMC.AMC_CANCELLED) {
            timeText = "Cancelled On"
            primaryCtaText = "Renew AMC".capitalize(Locale.ROOT)
        }
        //now if it is cancelled AMC then set date on 'timeRemaining' from 'cancelledOn' node else 'expiryDate'
        val timeRemaining = "$timeText: <b>${
            Utility.formatDate(
                if (amc.status == AMC.AMC_CANCELLED) amc.cancelledDate else amc.expiryDate,
                Utility.TIMESTAMP,
                Utility.DATE_FORMAT_4,
                Utility.TIMEZONE_UTC
            )
        }</b>"

        //set time remaining OR expired on OR cancelled on
        holder.timeRemainingView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(timeRemaining, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(timeRemaining)
        }
        //set primary CTA text
        holder.primaryCtaView.text = primaryCtaText

        holder.moneyEstimateView.visibility = View.VISIBLE
        holder.moneyEstimateView.text = Utility.convertToCurrency(amc.amount)

        holder.primaryCtaParent.visibility = View.VISIBLE
        holder.secondaryCtaParent.visibility = View.VISIBLE



        holder.secondaryCtaView.text = "view invoice".capitalize()

        holder.primaryCtaImageView.image = context.getDrawable(R.drawable.ic_create_jobcard_18dp)
        holder.secondaryCtaImageView.image = context.getDrawable(R.drawable.ic_invoice_preview)
    }

    inner class AmcCardViewHolder(view: View) : CardListingBaseViewHolder(view) {

        private val context = view.context

        init {
            parentView.setOnClickListener {
                interaction.startAmcPreview(amcList[adapterPosition])
            }
            moreActionView.visibility = View.VISIBLE

            moreActionView.setOnClickListener {
                //if this amc is active then only show cta to cancel AMC
//                if(amcList[adapterPosition].)
                val amc = amcList[adapterPosition]
                val isExpiredOrCancelled = (Utility.dateToCalender(
                    amc.expiryDate,
                    Utility.TIMESTAMP
                ).time < Calendar.getInstance().time) or (amc.status == AMC.AMC_CANCELLED)
                if (!isExpiredOrCancelled) {
                    val bundle = Bundle()
                    bundle.putString(MoreCtaListDialogFragment.ARG_VEHICLE_AMC_ID, amc.id)
                    bundle.putString(
                        MoreCtaListDialogFragment.ARG_TYPE,
                        AddCustomerActivity.ARG_VIEW
                    )
                    val modalBottom = MoreCtaListDialogFragment.newInstance(
                        MoreCtaListDialogFragment.ARG_IS_AMC,
                        bundle,
                        amc.vehicle.vehicleType
                    )
                    val fragmentManager = (context as FragmentActivity).supportFragmentManager
                    modalBottom.show(fragmentManager, "some")
                }
//                interaction.cancelAMC(amcList[adapterPosition].amcId)
            }

            primaryCtaView.setOnClickListener {
                val amc = amcList[adapterPosition]
                val isExpired = Utility.dateToCalender(
                    amc.expiryDate,
                    Utility.TIMESTAMP
                ).time < Calendar.getInstance().time
                if (isExpired || amc.status == AMC.AMC_CANCELLED) {
                    //todo:Renew AMC
                    context.toast("Coming soon!")
                } else {
                    interaction.initiateJobCard(amc.vehicle.registrationNumber, amc.customer.mobile)
                }
            }
            secondaryCtaView.setOnClickListener {
                interaction.startAMCInvoicePreview(amcList[adapterPosition])
            }
        }
    }

    override fun removeItemAndRefresh(id: String) {
    }
}