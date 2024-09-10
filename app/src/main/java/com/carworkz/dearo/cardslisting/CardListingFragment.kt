package com.carworkz.dearo.cardslisting

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.LoggingFacade
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.completejobcard.CompleteJobCardActivity
import com.carworkz.dearo.addjobcard.createjobcard.accidental.missinginfoaccidental.AccidentalMissingInfoActivity
import com.carworkz.dearo.addjobcard.createjobcard.customercarsearch.CustomerCarSearchActivity
import com.carworkz.dearo.amc.amcsolddetails.AmcSoldDetailsActivity
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.appointments.createappointment.CreateAppointmentActivity
import com.carworkz.dearo.appointments.createappointment.updateInfo.UpdateInfoActivity
import com.carworkz.dearo.appointments.reschedule.RescheduleActivity
import com.carworkz.dearo.appointments.status.AppointmentStatusChangeActivity
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.base.DialogFactory
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.cardslisting.adapters.*
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.FragmentCardListingBinding
import com.carworkz.dearo.databinding.FragmentDashboardBinding
import com.carworkz.dearo.databinding.LayoutEstimateMissingInfoBinding
import com.carworkz.dearo.domain.entities.*
import com.carworkz.dearo.events.*
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.otc.OtcProformaActivity
import com.carworkz.dearo.outwarding.OutwardingProcessActivity
import com.carworkz.dearo.outwarding.OutwardingProcessActivity.Companion.SCREEN_TYPE_PROFORMA
import com.carworkz.dearo.partpayment.PartPaymentActivity
import com.carworkz.dearo.pdf.Source
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.serviceremainder.NewServiceReminderActivity
import com.carworkz.dearo.utils.Constants
import com.carworkz.dearo.utils.Utility
/*import kotlinx.android.synthetic.main.dialog_vertical_btn.view.*
import kotlinx.android.synthetic.main.layout_estimate_missing_info.view.**/
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates

class CardListingFragment : BaseFragment(),
    CardListingContract.View,
    OnPageVisibleCallback,
    CardListingInteractionProvider,
    EventsManager.EventSubscriber, AMCInteractionProvider {
    private lateinit var binding: FragmentCardListingBinding
    private lateinit var appointment: Appointment

    @Inject
    lateinit var presenter: CardListingPresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    @Inject
    lateinit var pdfMediator: PdfMediator

    private lateinit var listingType: String
   /* private lateinit var recyclerView: RecyclerView
    private lateinit var binding.tvNoCardsFound: TextView*/
    private var adapter: CardListingBaseAdapter<*>? = null
    /*private lateinit var binding.swipeCardListing: SwipeRefreshLayout*/
    private var isLast = false
    private var isLoading: Boolean by Delegates.observable(false) { property, old, new ->
        try {
            binding.swipeCardListing.isRefreshing = new
        } catch (e: UninitializedPropertyAccessException) {
            LoggingFacade.log(e)
            // LoggingFacade.log("Card type $listingType", "Swipe to refresh not init")
        }
    }
    private val limit = 9

    private var calendar = Calendar.getInstance()
    private var alertDialog: AlertDialog? = null
    private var vehicleAmcId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            listingType = it.getString(ARG_TYPE)!!
            vehicleAmcId = it.getString(ARG_VEHICLE_AMC_ID, null)
        }

        (requireActivity().application as DearOApplication)
            .repositoryComponent
            .COMPONENT(CardListingPresenterModule(this))
            .inject(this)
        when (listingType) {
            LIST_TYPE_JOB_CARD_INITIATED -> screenTracker.sendScreenEvent(
                activity,
                ScreenTracker.SCREEN_JC_LISTING_INTIATED,
                this.javaClass.name
            )
            LIST_TYPE_JOB_CARD_IN_PROGRESS -> screenTracker.sendScreenEvent(
                activity,
                ScreenTracker.SCREEN_JC_LISTING_IN_PROGRESS,
                this.javaClass.name
            )
            LIST_TYPE_JOB_CARD_COMPLETED -> screenTracker.sendScreenEvent(
                activity,
                ScreenTracker.SCREEN_JC_LISTING_COMPLETED,
                this.javaClass.name
            )
            LIST_TYPE_JOB_CARD_CLOSED -> screenTracker.sendScreenEvent(
                activity,
                ScreenTracker.SCREEN_JC_LISTING_CLOSED,
                this.javaClass.name
            )
            LIST_TYPE_INVOICE_PROFORMA -> screenTracker.sendScreenEvent(
                activity,
                ScreenTracker.SCREEN_INVOICE_LISTING_PROFORMA,
                this.javaClass.name
            )
            LIST_TYPE_INVOICE_INVOICED -> screenTracker.sendScreenEvent(
                activity,
                ScreenTracker.SCREEN_INVOICE_LISTING_INVOICED,
                this.javaClass.name
            )
            LIST_TYPE_INVOICE_PAID -> screenTracker.sendScreenEvent(
                activity,
                ScreenTracker.SCREEN_INVOICE_LISTING_PAID,
                this.javaClass.name
            )
            LIST_TYPE_INVOICE_CANCELLED -> screenTracker.sendScreenEvent(
                activity,
                ScreenTracker.SCREEN_INVOICE_LISTING_CANCELLED,
                this.javaClass.name
            )
            LIST_TYPE_OTC_PROFORMA -> screenTracker.sendScreenEvent(
                activity,
                ScreenTracker.SCREEN_OTC_LISTING_PROFORMA,
                this.javaClass.name
            )
            LIST_TYPE_OTC_INVOICED -> screenTracker.sendScreenEvent(
                activity,
                ScreenTracker.SCREEN_OTC_LISTING_INVOICED,
                this.javaClass.name
            )
            // LIST_TYPE_OTC_CANCELLED -> screenTracker.sendScreenEvent(activity, ScreenTracker.SCREEN_OTC_LISTING_CANCELLED, this.javaClass.name)
            LIST_TYPE_APPOINTMENT_PAST -> screenTracker.sendScreenEvent(
                activity,
                ScreenTracker.SCREEN_APPOINTMENT_LISTING_PAST,
                this.javaClass.name
            )
            LIST_TYPE_APPOINTMENT_TODAY -> screenTracker.sendScreenEvent(
                activity,
                ScreenTracker.SCREEN_APPOINTMENT_LISTING_TODAY,
                this.javaClass.name
            )
            LIST_TYPE_APPOINTMENT_UPCOMING -> screenTracker.sendScreenEvent(
                activity,
                ScreenTracker.SCREEN_APPOINTMENT_LISTING_UPCOMING,
                this.javaClass.name
            )
            LIST_TYPE_APPOINTMENT_CANCELLED -> screenTracker.sendScreenEvent(
                activity,
                ScreenTracker.SCREEN_APPOINTMENT_LISTING_CANCELLED,
                this.javaClass.name
            )
            LIST_TYPE_AMC_ACTIVE -> screenTracker.sendScreenEvent(
                activity,
                ScreenTracker.SCREEN_AMC_LISTING_ACTIVE,
                this.javaClass.name
            )
            LIST_TYPE_AMC_EXPIRED -> screenTracker.sendScreenEvent(
                activity,
                ScreenTracker.SCREEN_AMC_LISTING_EXPIRED,
                this.javaClass.name
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // this.container = container
        binding = FragmentCardListingBinding.inflate(inflater, container, false)
        return binding.root
       /* return inflater.inflate(R.layout.fragment_card_listing, container, false)*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
     //   recyclerView = view.findViewById(R.id.rv_card_listing) as RecyclerView
        binding.rvCardListing.layoutManager?.isItemPrefetchEnabled = true
        progressBar = view.findViewById(R.id.pb_card_listing) as ProgressBar
     //   binding.tvNoCardsFound = view.findViewById(R.id.tv_no_cards_found) as TextView
       // binding.swipeCardListing = view.findViewById(R.id.swipe_card_listing) as SwipeRefreshLayout
        binding.swipeCardListing.setOnRefreshListener {
            refreshCurrentPage()
        }
        binding.rvCardListing.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager
                if (!listingType.contains("invoice")) {
                    val displayFrg = parentFragment as DisplayCardListingFragment
                    if (dy > 0 && displayFrg.isFabVisible()) {
                        displayFrg.hideFab()
                    } else if (dy <= 0 && !displayFrg.isFabVisible()) {
                        displayFrg.showFab()
                    }
                }
                val visibleItemCount = layoutManager?.childCount ?: -1
                val totalItemCount = layoutManager?.itemCount ?: -1
                val pastVisibleItems =
                    (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                if (pastVisibleItems + visibleItemCount >= totalItemCount && !isLoading && !isLast) {
                    checkInternetAndGetCards()
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NEW_ESTIMATOR_INVOICE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            EventsManager.post(
                CardStatusChangeEvent(
                    CardStatusChangeEvent.CARD_TYPE_INVOICE,
                    CardStatusChangeEvent.CARD_STATUS_INVOICED
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun showProgressIndicator() {
        isLoading = true
    }

    override fun dismissProgressIndicator() {
        isLoading = false
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
    }

    override fun onAppointmentAcceptSuccess(appointmentId: String) {
        adapter?.run {
            removeItemAndRefresh(appointmentId)
            postCardsCountChangeEvent(itemCount)
        }
    }

    override fun onAppointmentDeclineSuccess(appointmentId: String) {
        adapter?.run {
            removeItemAndRefresh(appointmentId)
            postCardsCountChangeEvent(itemCount)
            EventsManager.post(
                CardStatusChangeEvent(
                    CardStatusChangeEvent.CARD_TYPE_APPOINTMENT,
                    CardStatusChangeEvent.CARD_STATUS_APPOINTMENT_CANCELLED
                )
            )
        }
    }

    override fun onCreateJobCardSuccess(upsertDetails: UpsertDetails) {
        val startInitJobCard = Intent(activity, CustomerCarSearchActivity::class.java)
        startInitJobCard.putExtra(
            CustomerCarSearchActivity.REGISTRATION_NUMBER,
            upsertDetails.vehicle.registrationNumber
        )
        startInitJobCard.putExtra(
            CustomerCarSearchActivity.CUSTOMER_MOBILE_NUMBER,
            upsertDetails.customer.mobile
        )
        startInitJobCard.putExtra(CustomerCarSearchActivity.APPOINTMENT_ID, appointment.id)
        startActivity(startInitJobCard)
    }


    override fun onCreateJobCardError(
        isVariantRequired: Boolean,
        isNameRequired: Boolean,
        isPincodeRequired: Boolean
    ) {
        val startUpdateInfoIntent = Intent(activity, UpdateInfoActivity::class.java)
        startUpdateInfoIntent.putExtra(UpdateInfoActivity.APPOINTMENT_ID, appointment.id)
        startUpdateInfoIntent.putExtra(UpdateInfoActivity.DISPLAY_ID, appointment.appointmentId)
        startUpdateInfoIntent.putExtra(UpdateInfoActivity.DISPLAY_TIME, appointment.date)
        startUpdateInfoIntent.putExtra(UpdateInfoActivity.CUSTOMER, appointment.customer)
        startUpdateInfoIntent.putExtra(UpdateInfoActivity.VEHICLE, appointment.vehicle)
        startUpdateInfoIntent.putExtra(UpdateInfoActivity.PINCODE_REQUIRED, isPincodeRequired)
        startUpdateInfoIntent.putExtra(UpdateInfoActivity.VARIANT_REQUIRED, isVariantRequired)
        startUpdateInfoIntent.putExtra(UpdateInfoActivity.NAME_REQUIRED, isNameRequired)
        startActivity(startUpdateInfoIntent)
    }

    override fun launchWhatsapp(contactNumber: String, message: String) {
        Utility.sendWhatsApp(context, contactNumber, message)
    }

    override fun setUpdatedJC(jobCard: JobCard) {
        if (checkIfNetworkAvailable()) {
            //we need to check if mrnEstimate is enable and part issue is completed
            if ((SharedPrefHelper.isMrnEnabled()) and (jobCard.mrn?.isPartsIssued != true)) {
                showGenericError("Parts incharge not issued the requested parts")
                return
            }
            if(SharedPrefHelper.isPreDeliveryCheckEnabled() and SharedPrefHelper.getPreDeliveryCheckMode().equals("STRICT",true) and jobCard.isPdcCompleted.not())
            {
                showGenericError("Pre-Delivery check is not completed")
                return
            }

            if (jobCard.type != JobCard.TYPE_ACCIDENTAL) {

                startActivity(
                    CompleteJobCardActivity.getIntent(
                        requireContext(),
                        jobCard.id,
                        Utility.getDate(jobCard.estimate.deliveryDateTime).time.toString(),
                        jobCard.estimate.minCost,
                        jobCard.estimate.maxCost,
                        jobCard
                    )
                )
            } else {
                if (jobCard.accidental?.insurance?.claim == true) {
                    if (jobCard.accidental?.insurance?.claimNumber != null &&
                        jobCard.accidental?.insurance?.policyNumber != null &&
                        jobCard.accidental?.insurance?.expiryDate != null &&
                        jobCard.accidental?.company?.name != null &&
                        jobCard.accidental?.company?.address?.city != null &&
                        jobCard.accidental?.company?.address?.state != null &&
                        jobCard.accidental?.company?.address?.street != null &&
                        jobCard.accidental?.company?.address?.street != null &&
                        Utility.isPinCodeValid(jobCard.accidental?.company?.address?.pincode?.toString()) &&
                        jobCard.accidental?.company?.gstNumber != null

                    ) {
                        startActivity(
                            CompleteJobCardActivity.getIntent(
                                requireContext(),
                                jobCard.id,
                                Utility.getDate(jobCard.estimate.deliveryDateTime).time.toString(),
                                jobCard.estimate.minCost,
                                jobCard.estimate.maxCost,
                                jobCard
                            )
                        )
                    } else {
                        val errorStringBuilder = StringBuilder()
                        if (jobCard.accidental.insurance?.claimNumber == null) {
                            errorStringBuilder.appendln("\u2022 Claim Number")
                        }
                        if (jobCard.accidental.insurance?.policyNumber == null) {
                            errorStringBuilder.appendln("\u2022 Policy Number")
                        }
                        if (jobCard.accidental.insurance?.expiryDate == null) {
                            errorStringBuilder.appendln("\u2022 Policy Expiry date")
                        }
                        if (jobCard.accidental.company?.name == null) {
                            errorStringBuilder.appendln("\u2022 Insurance Company Name")
                        }

                        if (jobCard.accidental.company?.address?.city == null) {
                            errorStringBuilder.appendln("\u2022 Insurance Company City")
                        }

                        if (jobCard.accidental.company?.address?.state == null) {
                            errorStringBuilder.appendln("\u2022 Insurance Company State")
                        }

                        if (jobCard.accidental.company?.address?.street == null) {
                            errorStringBuilder.appendln("\u2022 Insurance Company Street")
                        }

                        if (jobCard.accidental.company?.address?.pincode == null) {
                            errorStringBuilder.appendln("\u2022 Insurance Company PinCode")
                        }

                        if (jobCard.accidental.company?.address?.pincode != null && Utility.isPinCodeValid(
                                jobCard.accidental?.company?.address?.pincode?.toString()
                            ).not()
                        ) {
                            errorStringBuilder.appendln("\u2022 PinCode not valid")
                        }

                        if (jobCard.accidental.company?.gstNumber == null) {
                            errorStringBuilder.appendln("\u2022 GST Number")
                        }

                        showAccidentalError(errorStringBuilder.toString(), jobCard)
                    }
                } else {

                    var dialog: AlertDialog? = null
                    val dialogView = View.inflate(context, R.layout.dialog_vertical_btn, null)
                    val splitInvoiceDialogBuilder = AlertDialog.Builder(requireContext())
                        .setView(dialogView)
                        .setTitle("Create Split Invoice?")
                        .setMessage("This will let you split the invoice between Customer and Insurance Company")
                    val positiveBtn: Button = dialogView.findViewById(R.id.positiveBtn)
                    val negativeBtn: Button = dialogView.findViewById(R.id.negativeBtn)
                    positiveBtn.setOnClickListener {
                        startAccidentalMissingActivity(jobCard)
                        dialog?.dismiss()
                    }

                    negativeBtn.setOnClickListener {
                        dialog?.dismiss()
                        startActivity(
                            CompleteJobCardActivity.getIntent(
                                requireContext(),
                                jobCard.id,
                                Utility.getDate(jobCard.estimate.deliveryDateTime).time.toString(),
                                jobCard.estimate.minCost,
                                jobCard.estimate.maxCost,
                                jobCard
                            )
                        )
                    }

                    dialog = splitInvoiceDialogBuilder.show()
                }
            }
        }

    }

    override fun acceptAppointment(appointment : Appointment) {

//        presenter.acceptAppointment(appointmentId)
//        adapter?.removeItemAndRefresh(appointmentId)
       startActivity( CreateAppointmentActivity.getIntent(requireContext(),appointment.vehicle.registrationNumber, appointment.customer.mobile, appointment.id))
    }

    override fun updateLeadStatus(appointment: Appointment) {
        startActivity(AppointmentStatusChangeActivity.getIntent(requireContext(),appointment.id!!))
    }

    override fun rescheduleAppointment(appointmentId: String) {
        startActivity<RescheduleActivity>(
            RescheduleActivity.ARG_ID to appointmentId,
            RescheduleActivity.ARG_TYPE to RescheduleActivity.ARG_IS_RESCHEDULE
        )
    }

    override fun callCreateJobCard(appointment: Appointment) {
        this.appointment = appointment
        presenter.createJobCard(appointment.id!!)
    }

    override fun getCardType(): String {
        return when (listingType) {
            LIST_TYPE_AMC_ACTIVE -> AMCDetails.AMC_ACTIVE
            LIST_TYPE_AMC_EXPIRED -> AMCDetails.AMC_EXPIRED
            LIST_TYPE_JOB_CARD_INITIATED -> JobCard.STATUS_INITIATED
            LIST_TYPE_JOB_CARD_IN_PROGRESS -> JobCard.STATUS_IN_PROGRESS
            LIST_TYPE_JOB_CARD_COMPLETED -> JobCard.STATUS_COMPLETED
            LIST_TYPE_JOB_CARD_CLOSED -> JobCard.STATUS_CLOSED
            LIST_TYPE_INVOICE_PROFORMA -> Invoice.STATUS_PROFORMA
            LIST_TYPE_INVOICE_INVOICED -> Invoice.STATUS_INVOICED
            LIST_TYPE_INVOICE_PAID -> Invoice.STATUS_PAID
            LIST_TYPE_INVOICE_CANCELLED -> Invoice.STATUS_CANCEL
            LIST_TYPE_APPOINTMENT_PAST -> Appointment.STATUS_PAST
            LIST_TYPE_APPOINTMENT_TODAY -> Appointment.STATUS_TODAY
            LIST_TYPE_APPOINTMENT_UPCOMING -> Appointment.STATUS_UPCOMING
            LIST_TYPE_APPOINTMENT_REQUESTED -> Appointment.STATUS_REQUESTED
            LIST_TYPE_APPOINTMENT_CANCELLED -> Appointment.STATUS_CANCELLED
            LIST_TYPE_OTC_PROFORMA -> OverTheCounter.STATUS_PROFORMA
            LIST_TYPE_OTC_INVOICED -> OverTheCounter.STATUS_INVOICED
            else -> ""
        }
    }

    override fun displayJobCards(cards: List<JobCard>, totalItemCount: Int) {
        binding.swipeCardListing.isRefreshing = false
        binding.tvNoCardsFound.visibility = View.GONE
        binding.rvCardListing.visibility = View.VISIBLE
        if (adapter == null) {
            adapter = JobCardListingAdapter(requireContext(), cards.toMutableList(), this)
            binding.rvCardListing.adapter = adapter
        } else {
            (adapter as JobCardListingAdapter).updateList(cards)
        }
        postCardsCountChangeEvent(totalItemCount)
        Timber.d("${adapter!!.itemCount}")
        if (adapter!!.itemCount >= totalItemCount) {
            isLast = true
        }
    }

    override fun displayAmcCards(cards: List<AMC>, totalItemCount: Int) {
        binding.swipeCardListing.isRefreshing = false
        binding.tvNoCardsFound.visibility = View.GONE
        binding.rvCardListing.visibility = View.VISIBLE
        if (listingType.equals(LIST_TYPE_AMC_ACTIVE)) {
        } else {

        }
        if (adapter == null) {
            adapter = AmcCardListingAdapter(requireContext(), cards.toMutableList(), this)
            binding.rvCardListing.adapter = adapter
        } else {
            (adapter as AmcCardListingAdapter).updateList(cards)
        }
        postCardsCountChangeEvent(totalItemCount)
        Timber.d("${adapter!!.itemCount}")
        if (adapter!!.itemCount >= totalItemCount) {
            isLast = true
        }
    }

    override fun displayAppointmentCards(cards: List<Appointment>, totalItemCount: Int) {
        binding.swipeCardListing.isRefreshing = false
        binding.tvNoCardsFound.visibility = View.GONE
        binding.rvCardListing.visibility = View.VISIBLE
        if (adapter == null) {
            adapter = AppointmentListingAdapter(requireContext(), cards.toMutableList(), this)
            binding.rvCardListing.adapter = adapter
        } else {
            (adapter as AppointmentListingAdapter).updateList(cards)
        }
        postCardsCountChangeEvent(totalItemCount)
        Timber.d("${adapter!!.itemCount}")
        if (adapter!!.itemCount >= totalItemCount) {
            isLast = true
        }
    }

    override fun displayInvoiceCards(cards: List<Invoice>, totalItemCount: Int) {
        binding.swipeCardListing.isRefreshing = false
        binding.tvNoCardsFound.visibility = View.GONE
        binding.rvCardListing.visibility = View.VISIBLE
        if (adapter == null) {
            adapter = InvoiceListingAdapter(cards.toMutableList(), this)
            binding.rvCardListing.adapter = adapter
        } else {
            (adapter as InvoiceListingAdapter).updateList(cards)
        }
        postCardsCountChangeEvent(totalItemCount)
        Timber.d("${adapter!!.itemCount}")
        if (adapter!!.itemCount >= totalItemCount) {
            isLast = true
        }
    }

    override fun displayOtcCards(obj: List<Invoice>, totalItemsCount: Int) {
        binding.swipeCardListing.isRefreshing = false
        binding.tvNoCardsFound.visibility = View.GONE
        binding.rvCardListing.visibility = View.VISIBLE
        if (adapter == null) {
            adapter = OtcListingAdapter(obj.toMutableList(), this)
            binding.rvCardListing.adapter = adapter
        } else {
            (adapter as OtcListingAdapter).updateList(obj)
        }
        postCardsCountChangeEvent(totalItemsCount)
        Timber.d("${adapter!!.itemCount}")
        if (adapter!!.itemCount >= totalItemsCount) {
            isLast = true
        }
    }

    private fun postCardsCountChangeEvent(count: Int) {
        var cardType: String? = null
        when (listingType) {
            LIST_TYPE_JOB_CARD_INITIATED -> cardType = CardCountUpdateEvent.CARD_STATUS_JOB_CARD_INITAITED
            LIST_TYPE_JOB_CARD_IN_PROGRESS -> cardType = CardCountUpdateEvent.CARD_STATUS_JOB_CARD_IN_PROGRESS
            LIST_TYPE_JOB_CARD_COMPLETED -> cardType = CardCountUpdateEvent.CARD_STATUS_JOB_CARD_COMPLETED
            LIST_TYPE_JOB_CARD_CLOSED -> cardType = CardCountUpdateEvent.CARD_STATUS_JOB_CARD_CLOSED

            LIST_TYPE_INVOICE_PROFORMA -> cardType = CardCountUpdateEvent.CARD_STATUS_PROFORMA
            LIST_TYPE_INVOICE_INVOICED -> cardType = CardCountUpdateEvent.CARD_STATUS_INVOICED
            LIST_TYPE_INVOICE_PAID -> cardType = CardCountUpdateEvent.CARD_STATUS_PAID
            LIST_TYPE_INVOICE_CANCELLED -> cardType =
                CardCountUpdateEvent.CARD_STATUS_INVOICE_CANCELLED
            LIST_TYPE_OTC_PROFORMA -> cardType = CardCountUpdateEvent.CARD_STATUS_OTC_PROFORMA
            LIST_TYPE_OTC_INVOICED -> cardType = CardCountUpdateEvent.CARD_STATUS_OTC_INVOICED
            // LIST_TYPE_OTC_CANCELLED -> cardType = CardCountUpdateEvent.CARD_STATUS_OTC_CANCELLED
            LIST_TYPE_APPOINTMENT_PAST -> cardType =
                CardCountUpdateEvent.CARD_STATUS_APPOINTMENT_PAST
            LIST_TYPE_APPOINTMENT_TODAY -> cardType =
                CardCountUpdateEvent.CARD_STATUS_APPOINTMENT_TODAY
            LIST_TYPE_APPOINTMENT_UPCOMING -> cardType =
                CardCountUpdateEvent.CARD_STATUS_APPOINTMENT_UPCOMING
            LIST_TYPE_APPOINTMENT_REQUESTED -> cardType =
                CardCountUpdateEvent.CARD_STATUS_APPOINTMENT_REQUESTED
            LIST_TYPE_APPOINTMENT_CANCELLED -> cardType =
                CardCountUpdateEvent.CARD_STATUS_APPOINTMENT_CANCELLED
            LIST_TYPE_AMC_ACTIVE -> cardType = CardCountUpdateEvent.CARD_STATUS_AMC_ACTIVE
            LIST_TYPE_AMC_EXPIRED -> cardType = CardCountUpdateEvent.CARD_STATUS_AMC_EXPIRED
        }
        EventsManager.post(
            CardCountUpdateEvent(
                cardType,
                count
            )
        )
    }

    override fun completeJobCardDone(jobCardId: String) {
        adapter?.run {
            removeItemAndRefresh(jobCardId)
            postCardsCountChangeEvent(itemCount)
            EventsManager.post(CardStatusChangeEvent(CardStatusChangeEvent.CARD_TYPE_JOB_CARD, CardStatusChangeEvent.CARD_STATUS_JOB_CARD_COMPLETED))
        }
    }

    override fun cancelJobCardDone(jobCardId: String) {
        adapter?.removeItemAndRefresh(jobCardId)
    }

    override fun onJobCardStatusChangeSuccess() {
    }

    override fun noCardsFound() {
        binding.rvCardListing.adapter = null
        binding.swipeCardListing.isRefreshing = false
        binding.rvCardListing.visibility = View.GONE
        binding.tvNoCardsFound.visibility = View.VISIBLE
        postCardsCountChangeEvent(0)
    }

    override fun showError(message: String) {
        binding.tvNoCardsFound.visibility = View.VISIBLE
        binding.tvNoCardsFound.text = message
    }

    override fun isPageVisible() {
        if (activity != null && !requireActivity().isFinishing) {
            Timber.d("$listingType is now Visible")
            if (!isLast && !isLoading) {
                checkInternetAndGetCards()
            }
            EventsManager.register(this)
        }
    }

    override fun onPageDetach() {
        Timber.d("OnPageDetach")
        adapter = null
        isLast = false
        isLoading = false
        EventsManager.unregister(this)
    }

    override fun callCancelJobCard(jobCardId: String) {
    }

    override fun callCompleteJobCard(jobCard: JobCard) {
        presenter.getEstimateAndCompleteJC(jobCardId = jobCard.id)
    }

    override fun callCloseJobCard(jobCardId: String) {
        startActivityForResult(NewServiceReminderActivity.getIntent(requireContext(), jobCardId, null, NewServiceReminderActivity.ACTION_CLOSE), REQUEST_CODE)
    }

    override fun callUpdatePayment(invoiceId: String, displayInvoiceId: String, jobCardId: String) {
        startActivity(PartPaymentActivity.getIntent(requireContext(), invoiceId, displayInvoiceId, jobCardId))
    }

    override fun updateCardListing(jobCardId: String) {
        if (adapter != null) {
            adapter!!.removeItemAndRefresh(jobCardId)
            postCardsCountChangeEvent(adapter!!.itemCount)
            when (listingType) {
                LIST_TYPE_JOB_CARD_COMPLETED -> EventsManager.post(CardStatusChangeEvent(CardStatusChangeEvent.CARD_TYPE_JOB_CARD, CardStatusChangeEvent.CARD_STATUS_JOB_CARD_CLOSED))
                LIST_TYPE_INVOICE_INVOICED -> EventsManager.post(CardStatusChangeEvent(CardStatusChangeEvent.CARD_TYPE_INVOICE, CardStatusChangeEvent.CARD_STATUS_PAID))
            }
        }
    }

    override fun onMissingUpdateSaveSuccess(jobCard: JobCard) {
        if (alertDialog != null && alertDialog?.isShowing == true) {
            alertDialog?.dismiss()
        }
        if (jobCard.status == JobCard.STATUS_IN_PROGRESS) {
            callCompleteJobCard(jobCard)
        }
    }
    override fun startEditProformaActivity(
        invoice: Invoice,
        jobCardId: String?,
        displayJcId: String?,
        invoiceId: String?,
        displayInvoiceId: String?,
        splitInvoice: Boolean,
        vehicleType: String?,
        jcType: String,
        requestCode: Int
    ) {
        startActivityForResult(
            OutwardingProcessActivity.getProformaIntent(
                requireContext(),
                SCREEN_TYPE_PROFORMA,
                displayJcId,
                displayInvoiceId,
                jobCardId,
                invoiceId,
                splitInvoice,
                vehicleType,
                invoice,
                jcType
            ), requestCode
        )
    }

    override fun startOtcProformaActivity(
        invoiceId: String,
        displayInvoiceId: String,
        vehicleType: String?
    ) {
        startActivity(
            OtcProformaActivity.getIntent(
                requireContext(),
                invoiceId,
                displayInvoiceId,
                vehicleType
            )
        )
    }

    override fun displayReassignServiceAdvisor(
        appointmentId: String,
        selectedServiceAdvisor: String?,
        serviceAdvisors: List<WorkshopAdviser>
    ) {
        var serviceAdvisorDialog: AlertDialog? = null
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        if (serviceAdvisors.isNotEmpty()) {
            val alertLayout = View.inflate(context, R.layout.layout_reassign_advisor_pop_up, null)
            alertDialogBuilder.setView(alertLayout)
            val advisorView: RecyclerView = alertLayout.findViewById(R.id.popUpReassignView)
            val adapter =
                ServiceAdvisorAdapter(requireContext(), selectedServiceAdvisor, serviceAdvisors)
            advisorView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            advisorView.adapter = adapter
            alertDialogBuilder.setTitle("Service Advisor")
            alertDialogBuilder.setNegativeButton("Cancel") { _, _ -> serviceAdvisorDialog?.dismiss() }
            alertDialogBuilder.setPositiveButton("Assign", null)
            serviceAdvisorDialog = alertDialogBuilder.create()
            serviceAdvisorDialog.setOnShowListener {
                val positiveButton =
                    (serviceAdvisorDialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton.setOnClickListener {
                    if (adapter.selectedServiceAdvisor == null) {
                        toast("Please select a service advisor to reassign")
                        return@setOnClickListener
                    }
                    presenter.reassignAdvisor(
                        appointmentId,
                        adapter.selectedServiceAdvisor!!,
                        object : CardListingPresenter.OnReassignCallback {
                            override fun onSuccess() {
                                serviceAdvisorDialog?.dismiss()
                                refreshCurrentPage()
                            }

                            override fun onFailure(errorMessage: String) {
                                showGenericError(errorMessage)
                            }
                        })
                }
            }
        } else {
            alertDialogBuilder.setTitle("Service Advisor")
            alertDialogBuilder.setMessage("No Service Advisors to reassign")
            alertDialogBuilder.setNeutralButton("OK") { _, _ ->
                serviceAdvisorDialog?.dismiss()
            }
            serviceAdvisorDialog = alertDialogBuilder.create()
        }
        serviceAdvisorDialog.show()
    }

    override fun startInvoicePreview(invoice: Invoice, jobCardId: String, source: Source) {
        // pdfMediator.startInvoicePreviewPdf(context, invoiceId, jobCardId, displayId, isSplit, state)
        pdfMediator.startInvoicePreviewPdf(requireContext(), invoice, jobCardId, source)
    }

    override fun startOtcInvoicePreview(invoice: Invoice) {
        pdfMediator.startOtcInvoicePreview(requireContext(), invoice)
    }


    override fun startJobCardDetailsPreview(jobCardId: String, displayId: String, source: Source) {
        pdfMediator.startJobCardDetailsPdf(requireContext(), jobCardId, displayId, source)
    }

    override fun getAmcId(): String? {
        return vehicleAmcId
    }

    override fun getJobCardById(id: String) {

    }

    override fun initiateJobCard(registrationNumber: String, mobile: String) {
        startActivity(
            CustomerCarSearchActivity.getIntent(
                requireContext(),
                registrationNumber,
                mobile,
                null
            )
        )
    }

    override fun startAmcPreview(amc: AMC) {
        startActivity(AmcSoldDetailsActivity.getIntent(requireContext(), amc))
    }

    override fun startAMCInvoicePreview(amc: AMC) {
        pdfMediator.startAmcInvoicePdf(requireContext(), amc.invoice?.pdf)
    }

    override fun startProformaPdf(invoice: Invoice, jobCardId: String, source: Source) {
        pdfMediator.startProformaPdf(requireContext(), invoice, jobCardId, source)
    }

    override fun showAccidentalErrors(
        errorList: ArrayList<String>,
        companyList: List<InsuranceCompany>?,
        jobCard: JobCard
    ) {
        if (alertDialog == null || (alertDialog != null && alertDialog?.isShowing == false)) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Insurance Details")
            var selectedCompany: String? = null

            // Inflate the layout using View Binding
            val missingInfoBind = LayoutEstimateMissingInfoBinding.inflate(LayoutInflater.from(context))
            builder.setView(missingInfoBind.root)

            if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_EXIPRY_DATE)) {
                missingInfoBind.insuranceExpiryView.visibility = View.VISIBLE
                missingInfoBind.insuranceExpiryView.setOnClickListener {
                    val insuranceDatePicker = DatePickerDialog(
                        requireContext(),
                        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                            missingInfoBind.insuranceExpiryView.setText(
                                StringBuilder()
                                    .append(dayOfMonth)
                                    .append("-")
                                    .append(month + 1)
                                    .append("-")
                                    .append(year)
                            )
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    insuranceDatePicker.datePicker.minDate = System.currentTimeMillis()
                    insuranceDatePicker.show()
                }
            }

            if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_COMPANY_NAME)) {
                missingInfoBind.insuranceCompanyParent.visibility = View.VISIBLE
                val list = companyList?.toMutableList()
                list?.add(0, InsuranceCompany("Select Insurance Company", DUMMY_SLUG))
                missingInfoBind.insuranceCompanyView.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    list?.map { it.name }?.toTypedArray() ?: emptyArray()
                )
                missingInfoBind.insuranceCompanyView.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {}

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (position != 0) {
                                selectedCompany = list!![position].name
                            }
                        }
                    }
            }

            if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_POLICY_NUMBER)) {
                missingInfoBind.policyNumberView.visibility = View.VISIBLE
            }

            if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_CLAIM_NUMBER)) {
                missingInfoBind.claimNumberView.visibility = View.VISIBLE
            }

            if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_PINCODE)) {
                missingInfoBind.pincodeView.visibility = View.VISIBLE
            }

            builder.setPositiveButton("OK", null)
            builder.setNegativeButton("Cancel") { _, _ ->
                alertDialog?.dismiss()
            }

            alertDialog = builder.create()
            alertDialog?.setOnShowListener {
                val positiveButton = alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton?.setOnClickListener {
                    val isValid = (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_EXIPRY_DATE).not() || missingInfoBind.insuranceExpiryView.text!!.isNotEmpty()) &&
                            (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_CLAIM_NUMBER).not() || missingInfoBind.claimNumberView.text!!.isNotEmpty()) &&
                            (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_POLICY_NUMBER).not() || missingInfoBind.policyNumberView.text!!.isNotEmpty()) &&
                            (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_COMPANY_NAME).not() || selectedCompany != null) &&
                            (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_PINCODE).not() || (missingInfoBind.pincodeView.text!!.isNotEmpty() && Utility.isPinCodeValid(missingInfoBind.pincodeView.text.toString())))

                    if (isValid) {
                        MissingAccidentalDetails().apply {
                            expiryDate = if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_EXIPRY_DATE) && missingInfoBind.insuranceExpiryView.text.toString().isNotEmpty()) {
                                Utility.formatDate(missingInfoBind.insuranceExpiryView.text.toString(), Utility.DATE_FORMAT_7, Utility.DATE_FORMAT_5, Utility.TIMEZONE_UTC)
                            } else {
                                Utility.formatDate(jobCard.accidental.insurance!!.expiryDate, Utility.TIMESTAMP, Utility.DATE_FORMAT_5, Utility.TIMEZONE_UTC)
                            }
                            claimNumber = if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_CLAIM_NUMBER)) missingInfoBind.claimNumberView.text.toString() else jobCard.accidental.insurance!!.claimNumber
                            policyNumber = if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_POLICY_NUMBER)) missingInfoBind.policyNumberView.text.toString() else jobCard.accidental.insurance!!.policyNumber
                            companyName = if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_COMPANY_NAME)) selectedCompany else jobCard.accidental.company!!.name
                            pincode = if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_PINCODE)) missingInfoBind.pincodeView.text.toString().toInt() else jobCard.accidental.company!!.address!!.pincode
                            presenter.updateMissingInfo(jobCard.id, this)
                        }
                    } else {
                        if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_EXIPRY_DATE) && missingInfoBind.insuranceExpiryView.text!!.isEmpty()) {
                            missingInfoBind.insuranceExpiryView.error = "Please Select Expiry Date"
                        }
                        if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_CLAIM_NUMBER) && missingInfoBind.claimNumberView.text!!.isEmpty()) {
                            missingInfoBind.claimNumberView.error = "Claim Number Required"
                        }
                        if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_POLICY_NUMBER) && missingInfoBind.policyNumberView.text!!.isEmpty()) {
                            missingInfoBind.policyNumberView.error = "Policy Number Required"
                        }
                        if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_COMPANY_NAME) && selectedCompany == null) {
                            toast("Please Select Insurance Company")
                        }
                        if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_PINCODE)) {
                            if (missingInfoBind.pincodeView.text!!.isEmpty()) {
                                missingInfoBind.pincodeView.error = "Pincode Required"
                            } else if (missingInfoBind.pincodeView.text!!.trim().length < 6) {
                                missingInfoBind.pincodeView.error = "Invalid Pincode"
                            }
                        }
                    }
                }
            }
            alertDialog?.show()
        }
    }



    /* override fun showAccidentalErrors(
         errorList: ArrayList<String>,
         companyList: List<InsuranceCompany>?,
         jobCard: JobCard
     ) {
         if (alertDialog == null || (alertDialog != null && alertDialog?.isShowing == false)) {
             val builder = android.app.AlertDialog.Builder(context)
             builder.setTitle("Insurance Details")
             var selectedCompany: String? = null
             val view = View.inflate(context, R.layout.layout_estimate_missing_info, null)
             builder.setView(view)
             if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_EXIPRY_DATE)) {
                 view.insuranceExpiryView.visibility = View.VISIBLE
                 view.insuranceExpiryView.setOnClickListener {
                     val insuranceDatePicker = DatePickerDialog(
                         requireContext(),
                         DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                             view.insuranceExpiryView.setText(
                                 StringBuilder()
                                     .append(dayOfMonth)
                                     .append("-")
                                     .append(month + 1)
                                     .append("-")
                                     .append(year)
                             )
                         },
                         calendar.get(Calendar.YEAR),
                         calendar.get(Calendar.MONTH),
                         calendar.get(Calendar.DAY_OF_MONTH)
                     )
                     insuranceDatePicker.datePicker.minDate = System.currentTimeMillis()
                     insuranceDatePicker.show()
                 }
             }
             if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_COMPANY_NAME)) {
                 view.insuranceCompanyParent.visibility = View.VISIBLE
                 val list = companyList?.toMutableList()
                 list?.add(0, InsuranceCompany("Select Insurance Company", DUMMY_SLUG))
                 view.insuranceCompanyView.adapter = ArrayAdapter(
                     requireContext(),
                     android.R.layout.simple_spinner_dropdown_item,
                     arrayOf(list?.map { it.name })
                 )
                 view.insuranceCompanyView.onItemSelectedListener =
                     object : AdapterView.OnItemSelectedListener {
                         override fun onNothingSelected(parent: AdapterView<*>?) {
                         }

                         override fun onItemSelected(
                             parent: AdapterView<*>?,
                             view: View?,
                             position: Int,
                             id: Long
                         ) {
                             if (position != 0) {
                                 selectedCompany = list!![position].name
                             }
                         }
                     }
             }
             if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_POLICY_NUMBER)) {
                 view.policyNumberView.visibility = View.VISIBLE
             }
             if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_CLAIM_NUMBER)) {
                 view.claimNumberView.visibility = View.VISIBLE
             }
             if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_PINCODE)) {
                 view.pincodeView.visibility = View.VISIBLE
             }
             builder.setPositiveButton("OK", null)
             builder.setNegativeButton("Cancel") { _, _ ->
                 alertDialog?.dismiss()
             }
             alertDialog = builder.create()
             alertDialog?.setOnShowListener {
                 val positiveButton =
                     (alertDialog as android.app.AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                 positiveButton.setOnClickListener {
                     if ((errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_EXIPRY_DATE)
                             .not() || (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_EXIPRY_DATE) && view.insuranceExpiryView.text!!.isNotEmpty())) &&
                         (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_CLAIM_NUMBER)
                             .not() || (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_CLAIM_NUMBER) && view.claimNumberView.text!!.isNotEmpty())) &&
                         (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_POLICY_NUMBER)
                             .not() || (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_POLICY_NUMBER) && view.policyNumberView.text!!.isNotEmpty())) &&
                         (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_COMPANY_NAME)
                             .not() || (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_COMPANY_NAME) && selectedCompany != null)) &&
                         (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_PINCODE)
                             .not() || (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_PINCODE)) && view.pincodeView.text!!.isNotEmpty() && Utility.isPinCodeValid(
                             view.pincodeView.text.toString()
                         ))
                     ) {
                         MissingAccidentalDetails().apply {
                             this.expiryDate =
                                 if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_EXIPRY_DATE) && view.insuranceExpiryView.text.toString()
                                         .isNotEmpty()
                                 ) Utility.formatDate(
                                     view.insuranceExpiryView.text.toString(),
                                     Utility.DATE_FORMAT_7,
                                     Utility.DATE_FORMAT_5,
                                     Utility.TIMEZONE_UTC
                                 ) else Utility.formatDate(
                                     jobCard.accidental.insurance!!.expiryDate,
                                     Utility.TIMESTAMP,
                                     Utility.DATE_FORMAT_5,
                                     Utility.TIMEZONE_UTC
                                 )
                             this.claimNumber =
                                 if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_CLAIM_NUMBER)) view.claimNumberView.text.toString() else jobCard.accidental.insurance!!.claimNumber
                             this.policyNumber =
                                 if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_POLICY_NUMBER)) view.policyNumberView.text.toString() else jobCard.accidental.insurance!!.policyNumber
                             this.companyName =
                                 if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_COMPANY_NAME)) selectedCompany else jobCard.accidental.company!!.name
                             this.pincode =
                                 if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_PINCODE)) view.pincodeView.text.toString()
                                     .toInt() else jobCard.accidental.company!!.address!!.pincode
                             presenter.updateMissingInfo(jobCard.id, this)
                         }
                     } else {
                         if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_EXIPRY_DATE) && view.insuranceExpiryView.text!!.isEmpty()) {
                             view.insuranceExpiryView.error = "Please Select. Expiry Date"
                         }
                         if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_CLAIM_NUMBER) && view.claimNumberView.text!!.isEmpty()) {
                             view.claimNumberView.error = "Claim Number Required"
                         }
                         if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_POLICY_NUMBER) && view.policyNumberView.text!!.isEmpty()) {
                             view.policyNumberView.error = "Policy Number Required"
                         }
                         if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_COMPANY_NAME) && selectedCompany == null) {
                             toast("Please Select Insurance Company")
                         }
                         if (errorList.contains(Constants.ApiConstants.KEY_JOB_CARD_ESTIMATE_PINCODE)) {
                             if (view.pincodeView.text!!.isEmpty()) {
                                 view.pincodeView.error = "Pincode Required"
                             } else if (view.pincodeView.text!!.trim().length < 6) {
                                 view.pincodeView.error = "Invalid Pincode"
                             }
                         }
                     }
                 }
             }
             alertDialog?.show()
         }
     }*/

    @Subscribe
    fun onJobCardCloseEvent(event: ChangeCardStatusEvent) {
        if (event.cardStatus == JobCard.STATUS_CLOSED)
            callCloseJobCard(event.cardId)
    }

    @Subscribe
    fun cancelEvent(cancelEvent: CancelEvent) {
        when (cancelEvent.type) {
            CancelEvent.AMC->{
                //show dialog
                alert(getString(R.string.cancel_amc_warning), getString(R.string.are_u_sure)) {
                    positiveButton("AGREE") {
                        val intent= Intent(context,RescheduleActivity::class.java)
                        intent.putExtra(RescheduleActivity.ARG_ID,cancelEvent.id)
                        intent.putExtra(RescheduleActivity.ARG_TYPE,RescheduleActivity.ARG_IS_CANCEL_AMC)
                        startActivity(intent)
                    }
                    negativeButton("DISAGREE") { it.dismiss() }
                }.show()
            }
            CancelEvent.INVOICE -> {
                alert(getString(R.string.cancel_invoice_warning), getString(R.string.are_u_sure)) {
                    positiveButton("AGREE") {
                        this@CardListingFragment.startActivity<RescheduleActivity>(
                            ARG_ID to cancelEvent.id,
                            RescheduleActivity.ARG_TYPE to RescheduleActivity.ARG_IS_CANCEL_INVOICE
                        )
                    }
                    negativeButton("DISAGREE") { it.dismiss() }
                }.show()
            }
            CancelEvent.APPOINTMENT -> {
                presenter.cancelAppointment(cancelEvent.id)
                adapter?.removeItemAndRefresh(cancelEvent.id)
                Timber.d("Event Appointment Cancelled + $listingType")
            }
            CancelEvent.JOBCARD -> {
                Timber.d("Event Jobcard")
                alert(
                    getString(R.string.card_listing_alert_cancel_jc_message),
                    getString(R.string.card_listing_alert_cancel_jc_title)
                ) {
                    positiveButton(getString(R.string.card_listing_alert_positive_button)) {
                        presenter.cancelJobCard(cancelEvent.id)
                        Timber.d("Event Jobcard Cancelled $listingType id is ${cancelEvent.id}")
                    }
                    negativeButton(getString(R.string.card_listing_alert_negative_button)) {
                        Timber.d("Event Jobcard Cancelled dismissing")
                    }
                }.show()
            }
        }
    }

    @Subscribe
    fun reassignAppointment(event: AppointmentReassignEvent) {
        presenter.initReassignServiceAdvisor(event.appointmentId, event.selectedServiceAdvisorId)
    }

    private fun checkInternetAndGetCards() {
        if (checkIfNetworkAvailable()) {
            when (listingType) {
                LIST_TYPE_AMC_ACTIVE -> presenter.getAmcCrds(
                    adapter?.itemCount
                        ?: 0, limit, AMCDetails.AMC_ACTIVE,"")

                LIST_TYPE_AMC_EXPIRED -> presenter.getAmcCrds(
                    adapter?.itemCount
                        ?: 0, limit, AMCDetails.AMC_EXPIRED
                ,"")

                LIST_TYPE_JOB_CARD_INITIATED -> presenter.getJobCards(
                    adapter?.itemCount
                        ?: 0, limit, JobCard.STATUS_INITIATED
                )

                LIST_TYPE_JOB_CARD_IN_PROGRESS -> presenter.getJobCards(
                    adapter?.itemCount
                        ?: 0, limit, JobCard.STATUS_IN_PROGRESS
                )
                LIST_TYPE_JOB_CARD_COMPLETED -> presenter.getJobCards(
                    adapter?.itemCount
                        ?: 0, limit, JobCard.STATUS_COMPLETED
                )
                LIST_TYPE_JOB_CARD_CLOSED -> presenter.getJobCards(
                    adapter?.itemCount
                        ?: 0, limit, JobCard.STATUS_CLOSED, JobCard.STATUS_CANCELLED
                )
                LIST_TYPE_INVOICE_PROFORMA -> presenter.getInvoiceCards(
                    Invoice.STATUS_PROFORMA, adapter?.itemCount
                        ?: 0, limit, ARG_TYPE_JC
                )
                LIST_TYPE_INVOICE_INVOICED -> presenter.getInvoiceCards(
                    Invoice.STATUS_INVOICED, adapter?.itemCount
                        ?: 0, limit, ARG_TYPE_JC
                )
                LIST_TYPE_INVOICE_PAID -> presenter.getInvoiceCards(
                    Invoice.STATUS_PAID, adapter?.itemCount
                        ?: 0, limit, ARG_TYPE_JC
                )
                LIST_TYPE_INVOICE_CANCELLED -> presenter.getInvoiceCards(
                    Invoice.STATUS_CANCEL, adapter?.itemCount
                        ?: 0, limit, ARG_TYPE_JC
                )
                LIST_TYPE_APPOINTMENT_PAST -> presenter.getAppointmentCards(
                    Appointment.STATUS_PAST, adapter?.itemCount
                        ?: 0, limit
                )
                LIST_TYPE_APPOINTMENT_TODAY -> presenter.getAppointmentCards(
                    Appointment.STATUS_TODAY, adapter?.itemCount
                        ?: 0, limit
                )
                LIST_TYPE_APPOINTMENT_UPCOMING -> presenter.getAppointmentCards(
                    Appointment.STATUS_UPCOMING, adapter?.itemCount
                        ?: 0, limit
                )
                LIST_TYPE_APPOINTMENT_REQUESTED -> presenter.getAppointmentCards(
                    Appointment.LIST_IN_PROGRESS, adapter?.itemCount
                        ?: 0, limit
                )
                LIST_TYPE_APPOINTMENT_CANCELLED -> presenter.getAppointmentCards(
                    Appointment.STATUS_CANCELLED, adapter?.itemCount
                        ?: 0, limit
                )
                LIST_TYPE_OTC_PROFORMA -> presenter.getInvoiceCards(
                    Invoice.STATUS_PROFORMA, adapter?.itemCount
                        ?: 0, limit, ARG_TYPE_OTC
                )
                LIST_TYPE_OTC_INVOICED -> presenter.getInvoiceCards(
                    Invoice.STATUS_INVOICED, adapter?.itemCount
                        ?: 0, limit, ARG_TYPE_OTC
                )
//
            }
        }
    }

    private fun refreshCurrentPage() {
        if (isLoading.not()) {
            adapter = null
            isLast = false
            checkInternetAndGetCards()
        }
    }

    override fun showAccidentalError(error: String?, jobCard: JobCard) {
        val dialog = DialogFactory.createGenericErrorDialog(
            context,
            "Following details are missing",
            error
        ) { _, _ ->
            startAccidentalMissingActivity(jobCard)
        }
        dialog.show()
    }

    private fun startAccidentalMissingActivity(jobCard: JobCard) {
        startActivity(
            AccidentalMissingInfoActivity.getIntent(
                requireContext(),
                jobCard.id,
                jobCard.status
            )
        )
    }

    companion object {

        const val ARG_TYPE = "type"
        const val ARG_VEHICLE_AMC_ID = "vehicle-amc-id"
        const val ARG_TYPE_OTC = "OTC"
        const val ARG_TYPE_JC = "JOBCARD"
        const val LIST_TYPE_JOB_CARD_INITIATED = "jc_initiated"
        const val LIST_TYPE_JOB_CARD_IN_PROGRESS = "jc_in_progress"
        const val LIST_TYPE_JOB_CARD_COMPLETED = "jc_completed"
        const val LIST_TYPE_JOB_CARD_CLOSED = "jc_closed"

        const val LIST_TYPE_INVOICE_PROFORMA = "invoice_proforma"
        const val LIST_TYPE_INVOICE_INVOICED = "invoice_invoiced"
        const val LIST_TYPE_INVOICE_PAID = "invoice_paid"
        const val LIST_TYPE_INVOICE_CANCELLED = "invoice_cancelled"

        const val ARG_ID = "ID"

        const val LIST_TYPE_APPOINTMENT_PAST = "appointment_past"
        const val LIST_TYPE_APPOINTMENT_TODAY = "appointment_today"
        const val LIST_TYPE_APPOINTMENT_UPCOMING = "appointment_upcoming"
        const val LIST_TYPE_APPOINTMENT_REQUESTED = "appointment_requested"
        const val LIST_TYPE_APPOINTMENT_CANCELLED = "appointment_cancelled"

        const val REQUEST_CODE = 1000
        const val NEW_ESTIMATOR_JC_REQUEST_CODE = 1001
        const val NEW_ESTIMATOR_INVOICE_REQUEST_CODE = 1002
        const val LIST_TYPE_OTC_PROFORMA: String = "otc_proforma"
        const val LIST_TYPE_OTC_INVOICED: String = "otc_invoiced"

        const val LIST_TYPE_AMC_ACTIVE: String = "amc_list_active"
        const val LIST_TYPE_AMC_EXPIRED: String = "amc_list_expired"
        // val LIST_TYPE_OTC_CANCELLED: String = "otc_cancelled"

        @JvmStatic
        fun newInstance(type: String, amcId: String?) =
            CardListingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TYPE, type)
                    putString(ARG_VEHICLE_AMC_ID, amcId)
                }
            }
    }
}
