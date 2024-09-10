package com.carworkz.dearo.cardslisting.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.FragmentActivity
import com.carworkz.dearo.R
import com.carworkz.dearo.appointments.appointmentdetails.AppointmentCardDetailsActivity
import com.carworkz.dearo.cardslisting.CardListingBaseAdapter
import com.carworkz.dearo.cardslisting.CardListingBaseViewHolder
import com.carworkz.dearo.cardslisting.CardListingInteractionProvider
import com.carworkz.dearo.domain.entities.Appointment
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.morecta.MoreCtaListDialogFragment
import com.carworkz.dearo.utils.Utility
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Kush on 06/01/18.
 */
class AppointmentListingAdapter(
    private val context: Context,
    var list: MutableList<Appointment>,
    val interactionProvider: CardListingInteractionProvider
) : CardListingBaseAdapter<AppointmentListingAdapter.AppointmentViewHolder>() {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun removeItemAndRefresh(id: String) {
        val index = list.indexOf(list.find { id == it.id })
        if (index != -1) {
            list.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun updateList(updateList: List<Appointment>) {
        val oldSize = itemCount
        list.addAll(updateList)
        notifyItemRangeInserted(oldSize, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.row_new_job_card_listing, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = list[position]
        //set status
        holder.chipView.text=appointment.status

        holder.timeRemainingView.visibility=View.VISIBLE
        holder.timeRemainingView.text = Utility.formatDate(
            appointment.date,
            Utility.TIMESTAMP,
            Utility.DATE_FORMAT_2,
            Utility.TIMEZONE_UTC
        )

        @SuppressLint("SetTextI18n")
        holder.idView.text = "\u2022 ${appointment.appointmentId}"
        holder.callView.tag = appointment.customer.mobile
        holder.titleView.text = appointment.vehicle.registrationNumber

        @SuppressLint("SetTextI18n")
        holder.makeModelView.text =
            "${appointment.vehicle.makeName} - ${appointment.vehicle.modelName} - ${appointment.vehicle.fuelType}"

        (holder.chipView.background as? GradientDrawable)?.setColor(
            ContextCompat.getColor(
                holder.chipView.context,
                R.color.light_grey
            )
        )

        holder.nameView.text = appointment.customer.name?.capitalize()
        toggleCreateJobCard(appointment, holder)
        when (interactionProvider.getCardType()) {
            Appointment.STATUS_PAST -> holder.initPastView(holder.adapterPosition)
            Appointment.STATUS_TODAY -> holder.initTodayView(holder.adapterPosition)
            Appointment.STATUS_UPCOMING -> holder.initUpcomingView(holder.adapterPosition)
            Appointment.STATUS_REQUESTED -> holder.initRequestedView(holder.adapterPosition)
            Appointment.STATUS_CANCELLED -> holder.initCancelledView()
        }
    }

    private fun toggleCreateJobCard(appointment: Appointment, holder: AppointmentViewHolder) {
        if (interactionProvider.getCardType() == Appointment.STATUS_TODAY) {
            val now = Calendar.getInstance()
            val appointmentDate = Calendar.getInstance()
            appointmentDate.time = Utility.getDate(appointment.date)
            val diff = appointmentDate.timeInMillis - now.timeInMillis
            if (diff <= TimeUnit.MINUTES.toMillis(10)) {
                holder.primaryCtaParent.isClickable = true
                holder.primaryCtaView.textColor =
                    ContextCompat.getColor(context, R.color.textColorPrimary)
            } else {
                holder.primaryCtaParent.isClickable = false
                holder.primaryCtaView.textColor =
                    ContextCompat.getColor(context, R.color.textColorSecondary)
            }
        }
    }

    inner class AppointmentViewHolder(view: View) : CardListingBaseViewHolder(view),
        View.OnClickListener {

        init {
            parentView.setOnClickListener(this)
            moreActionView.visibility = View.GONE
            moreActionView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            val appointment = list[adapterPosition]
            when (view) {
                secondaryCtaParent -> {
                    if (appointment.status == Appointment.STATUS_CONFIRMED) {
                        interactionProvider.rescheduleAppointment(appointment.id!!)
                    } else if (appointment.status == Appointment.STATUS_REQUESTED || appointment.status == Appointment.STATUS_IN_PROGRESS) {
                        interactionProvider.updateLeadStatus(appointment)
                    }
                }
                parentView -> {
                    val intent = Intent(context, AppointmentCardDetailsActivity::class.java)
                    intent.putExtra(
                        AppointmentCardDetailsActivity.APPOINTMENT,
                        list[adapterPosition]
                    )
                    parentView.context.startActivity(intent)
                }
                moreActionView -> {
                    //dont show CTA for requested [Decline Appointment]
                    if (!( appointment.status == Appointment.STATUS_REQUESTED || appointment.status == Appointment.STATUS_IN_PROGRESS))
                        showCtaView()
                }
            }
        }

        internal fun initPastView(position: Int) {
            if (list[position].jobCard == null) {
                moreActionView.visibility = View.VISIBLE
                ctaParentView.visibility = View.VISIBLE
                primaryCtaParent.visibility = View.INVISIBLE
                secondaryCtaParent.visibility = View.VISIBLE
                secondaryCtaView.text = context.getString(R.string.card_listing_btn_reschedule)
                secondaryCtaImageView.image = context.getDrawable(R.drawable.ic_history_black_24dp)
                secondaryCtaParent.setOnClickListener(this)
            } else {
                ctaParentView.visibility = View.GONE
                moreActionView.visibility = View.GONE
            }
        }

        internal fun initTodayView(position: Int) {
            if (list[position].jobCard == null) {
                moreActionView.visibility = View.VISIBLE
                ctaParentView.visibility = View.VISIBLE
                primaryCtaParent.visibility = View.VISIBLE
                primaryCtaParent.tag = Appointment.STATUS_TODAY
                secondaryCtaParent.visibility = View.VISIBLE
                primaryCtaView.text = context.getString(R.string.card_listing_btn_create_job_card)
                secondaryCtaView.text = context.getString(R.string.card_listing_btn_reschedule)
                primaryCtaImageView.image =
                    context.getDrawable(R.drawable.ic_appointment_book_black_24dp)
                secondaryCtaImageView.image = context.getDrawable(R.drawable.ic_history_black_24dp)
                secondaryCtaParent.setOnClickListener(this)
                // if (interactionProvider.getCardType() == Appointment.STATUS_TODAY) {
                val now = Calendar.getInstance()
                val appointmentDate = Calendar.getInstance()
                appointmentDate.time = Utility.getDate(list[position].date)
                val diff = appointmentDate.timeInMillis - now.timeInMillis
                if (diff <= TimeUnit.HOURS.toMillis(24)) {
                    primaryCtaParent.setOnClickListener {
                        interactionProvider.callCreateJobCard(list[adapterPosition])
                    }
                    primaryCtaView.textColor =
                        ContextCompat.getColor(context, R.color.colorPrimaryDark)
                    DrawableCompat.setTint(
                        primaryCtaImageView.drawable,
                        ContextCompat.getColor(
                            primaryCtaImageView.context,
                            R.color.colorPrimaryDark
                        )
                    )
                } else {
                    primaryCtaParent.setOnClickListener(null)
                    primaryCtaView.textColor =
                        ContextCompat.getColor(context, R.color.textColorSecondary)
                    DrawableCompat.setTint(
                        primaryCtaImageView.drawable,
                        ContextCompat.getColor(
                            primaryCtaImageView.context,
                            R.color.textColorSecondary
                        )
                    )
                }
                // }

//                if (countDownTimer != null) {
//                    Timber.d("canceling timer" + position)
//                    doAsync {
//                        Looper.prepare()
//                        countDownTimer?.cancel()
//                        Looper.loop()
//                    }
//                }
//
//
//                if (diff > TimeUnit.MINUTES.toMillis(10) && diff <= TimeUnit.MINUTES.toMillis(20)) {
//
//                    doAsync {
//                        Timber.d("setting timer to $position ${TimeUnit.MILLISECONDS.toMinutes(diff)}")
//                        Timber.d("diff to minutes ${TimeUnit.MILLISECONDS.toMinutes(diff)}")
//                        val lapseTime = diff - TimeUnit.MINUTES.toMillis(10)
//                        Timber.d("count down future time ${TimeUnit.MILLISECONDS.toMinutes(lapseTime)}")
//                        Looper.prepare()
//                        countDownTimer = EnableCreateJobCardTimer(lapseTime, TimeUnit.SECONDS.toMillis(1), position).start()
//                        Looper.loop()
//                    }
//
//                }
            } else {
                ctaParentView.visibility = View.GONE
                moreActionView.visibility = View.GONE
            }
        }

        internal fun initRequestedView(position: Int) {
            moreActionView.visibility = View.VISIBLE
            ctaParentView.visibility = View.VISIBLE
            primaryCtaParent.visibility = View.VISIBLE
            secondaryCtaParent.visibility = View.VISIBLE
            primaryCtaView.text = context.getString(R.string.card_listing_btn_accept)
            secondaryCtaView.text = context.getString(R.string.card_listing_btn_update_statust)
            primaryCtaImageView.image = context.getDrawable(R.drawable.ic_check_black_24dp)
            secondaryCtaImageView.image = context.getDrawable(R.drawable.ic_history_black_24dp)
            secondaryCtaParent.setOnClickListener(this)
            primaryCtaParent.setOnClickListener { interactionProvider.acceptAppointment(list[position]) }
        }

        internal fun initUpcomingView(position: Int) {
            if (list[position].jobCard == null) {
                moreActionView.visibility = View.VISIBLE
                ctaParentView.visibility = View.VISIBLE
                primaryCtaParent.visibility = View.INVISIBLE
                secondaryCtaParent.visibility = View.VISIBLE
                secondaryCtaView.text = context.getString(R.string.card_listing_btn_reschedule)
                secondaryCtaImageView.image = context.getDrawable(R.drawable.ic_history_black_24dp)
                secondaryCtaParent.setOnClickListener(this)
            } else {
                ctaParentView.visibility = View.GONE
                moreActionView.visibility = View.GONE
            }
        }

        internal fun initCancelledView() {
            ctaParentView.visibility = View.GONE
//            primaryCtaParent.visibility = View.GONE
//            secondaryCtaParent.visibility = View.GONE
        }

        private fun showCtaView() {
            val bundle = Bundle()
            val appointment = list[adapterPosition]
            bundle.putString(MoreCtaListDialogFragment.ARG_ID, appointment.id)
            bundle.putString(
                MoreCtaListDialogFragment.ARG_SERVICE_ADVISOR_ID,
                appointment.serviceAdvisorId
            )
            val modalBottom =
                MoreCtaListDialogFragment.newInstance(list[adapterPosition].status!!, bundle, null)

            Timber.d("status" + list[adapterPosition].status)
            val fragmentManager = (context as FragmentActivity).supportFragmentManager
            modalBottom.show(fragmentManager, "some")
        }
    }

//    inner class EnableCreateJobCardTimer(millisInFuture: Long, intervalMillis: Long, private val adapterPos: Int) : CountDownTimer(millisInFuture, intervalMillis) {
//        override fun onFinish() {
//            context.runOnUiThread {
//                Timber.d("inside finish $adapterPos")
//                notifyItemChanged(adapterPos)
//            }
//        }
//
//        override fun onTick(millisUntilFinished: Long) {
//            context.runOnUiThread {
//                // Timber.d("Time unit $adapterPos  ${TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)}")
//            }
//        }
//    }
}