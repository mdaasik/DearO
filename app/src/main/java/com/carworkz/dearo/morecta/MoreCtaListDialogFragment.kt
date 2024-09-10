package com.carworkz.dearo.morecta

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.addcustomer.AddCustomerActivity
import com.carworkz.dearo.addjobcard.addeditvehicle.VehicleDetailsActivity
import com.carworkz.dearo.addjobcard.createjobcard.estimate.servicePackage.AddServicePackageActivity
import com.carworkz.dearo.appointments.reschedule.RescheduleActivity
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.cardslisting.adapters.InvoiceListingAdapter
import com.carworkz.dearo.carpm.CarPmAuthActivity
import com.carworkz.dearo.carpm.CarPmAuthActivity.Companion.ARG_JOB_ID
import com.carworkz.dearo.carpm.CarPmAuthActivity.Companion.ARG_MAKE_NAME
import com.carworkz.dearo.carpm.CarPmAuthActivity.Companion.ARG_REG_NO
import com.carworkz.dearo.carpm.ClearCodeActivity
import com.carworkz.dearo.customerfeedback.NewCustomerFeedBackActivity
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.domain.entities.Appointment
import com.carworkz.dearo.domain.entities.Feedback
import com.carworkz.dearo.domain.entities.Invoice
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.domain.entities.Role
import com.carworkz.dearo.domain.entities.Vehicle
import com.carworkz.dearo.events.AppointmentReassignEvent
import com.carworkz.dearo.events.ApproveEvent
import com.carworkz.dearo.events.CancelEvent
import com.carworkz.dearo.events.ChangeCardStatusEvent
import com.carworkz.dearo.extensions.image
import com.carworkz.dearo.invoices.invoiceremarks.InvoiceRemarksActivity
import com.carworkz.dearo.mrn.MrnActivity
import com.carworkz.dearo.outwarding.OutwardingProcessActivity
import com.carworkz.dearo.pdf.Source
import com.carworkz.dearo.pdf.mediator.PdfMediator
import com.carworkz.dearo.predeliverycheck.PdcActivity
import com.carworkz.dearo.search.SearchActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject

class MoreCtaListDialogFragment : BottomSheetDialogFragment() {

    private lateinit var progressbar: ProgressBar

    @Inject
    lateinit var pdfMediator: PdfMediator

    private var vehicleType: String? = null
    private var vehicleAmcId: String? = null

    private var ctaList: MutableList<MoreCtaObj> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressbar = ProgressBar(activity)
        vehicleType = requireArguments().getString(ARG_VEHICLE_TYPE)
        vehicleAmcId = requireArguments().getBundle(ARG_DATA)?.getString(ARG_VEHICLE_AMC_ID, null)

        (requireActivity().application as DearOApplication).repositoryComponent.COMPONENT(
            MoreCtaPresenterModule()
        ).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_morecta_list_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view as RecyclerView?
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = MoreCtaAdapter(
            requireArguments().getBoolean(ARG_IS_QUICK_JC, false),
            requireArguments().getString(ARG_CARD_TYPE)!!,
            requireArguments().getBundle(ARG_DATA)!!
        )
    }

    private inner class ViewHolder(
        view: View, isQuickJc: Boolean, cardType: String, data: Bundle
    ) : RecyclerView.ViewHolder(view) {

        val ctaTitleView: TextView = itemView.findViewById(R.id.tv_cta)
        val ctaIconView: ImageView = itemView.findViewById(R.id.iv_cta)
        val ctaParent: LinearLayout = itemView.findViewById(R.id.cta_more_parent)

        init {
            ctaParent.setOnClickListener {
                when (cardType) {
                    ARG_CUSTOMER_APPROVAL -> {
                        when (adapterPosition) {
                            0 -> {
                                EventsManager.post(
                                    ApproveEvent(
                                        ApproveEvent.ESTIMATE_CUSTOMER_APPROVAL,
                                        data.getString(ARG_JOB_CARD_ID).toString()
                                    )
                                )
                            }
                        }
                    }

                    ARG_IS_AMC -> {
                        when (adapterPosition) {
                            //show notice dialog
                            0 -> {
                                EventsManager.post(
                                    CancelEvent(
                                        CancelEvent.AMC,
                                        data.getString(ARG_VEHICLE_AMC_ID).toString()
                                    )
                                )
                            }
                        }
                    }

                    JobCard.STATUS_INITIATED -> {
                        when (adapterPosition) {
                            0 -> requireContext().startActivity(
                                AddCustomerActivity.getIntent(
                                    requireContext(),
                                    AddCustomerActivity.ARG_VIEW,
                                    data.getString(AddCustomerActivity.ARG_CUSTOMER_ID),
                                    null,
                                    null,
                                    null
                                )
                            )

                            1 -> requireContext().startActivity(
                                VehicleDetailsActivity.getViewVehicleIntent(
                                    requireContext(),
                                    data.get(VehicleDetailsActivity.VEHICLE) as Vehicle
                                )
                            )
                        }
                    }

                    JobCard.STATUS_IN_PROGRESS -> {
                        when (ctaList[adapterPosition].title) {
                            CONSTANT_KEYS.CARPM_SCAN_CODES -> {
                                requireContext().startActivity(
                                    CarPmAuthActivity.getIntent(
                                        requireContext(),
                                        data.getString(ARG_JOB_ID),
                                        data.getString(ARG_REG_NO),
                                        data.getString(ARG_MAKE_NAME),
                                        data.getString(CarPmAuthActivity.ARG_SCAN_ID),
                                        data.getBoolean(CarPmAuthActivity.ARG_IS_CARPM_SCANNED)
                                    )
                                )
                            }

                            CONSTANT_KEYS.CARPM_SCANNED_REPORTS -> {
                                requireContext().startActivity(
                                    CarPmAuthActivity.getIntent(
                                        requireContext(),
                                        data.getString(ARG_JOB_ID),
                                        data.getString(ARG_REG_NO),
                                        data.getString(ARG_MAKE_NAME),
                                        data.getString(CarPmAuthActivity.ARG_SCAN_ID),
                                        data.getBoolean(CarPmAuthActivity.ARG_IS_CARPM_SCANNED)
                                    )
                                )
                            }


                            CONSTANT_KEYS.CUSTOMER_DETAILS -> {
                                requireContext().startActivity(
                                    AddCustomerActivity.getIntent(
                                        requireContext(),
                                        AddCustomerActivity.ARG_VIEW,
                                        data.getString(AddCustomerActivity.ARG_CUSTOMER_ID),
                                        null,
                                        null,
                                        null
                                    )
                                )
                            }

                            CONSTANT_KEYS.VEHICLE_DETAILS -> {
                                requireContext().startActivity(
                                    VehicleDetailsActivity.getViewVehicleIntent(
                                        requireContext(),
                                        data.get(VehicleDetailsActivity.VEHICLE) as Vehicle
                                    )
                                )
                            }

                            CONSTANT_KEYS.VIEW_ISSUE_PARTS -> {
                                requireContext().startActivity(
                                    MrnActivity.getIntent(
                                        requireContext(), data.getString(ARG_JOB_CARD_ID).toString()
                                    )
                                )
                            }

                            CONSTANT_KEYS.PREVIEW_JOB_CARD -> {
                                pdfMediator.startJobCardDetailsPdf(
                                    requireContext(),
                                    data.getString(ARG_JOB_CARD_ID).toString(),
                                    data.getString(ARG_DISPLAY_ID).toString(),
                                    Source.IN_PROGRESS
                                )
                            }

                            CONSTANT_KEYS.UPDATE_ESTIMATE -> {
                                requireContext().startActivity(
                                    OutwardingProcessActivity.getEstimatorIntent(
                                        requireContext(),
                                        data.getString(ARG_DISPLAY_ID).toString().substring(0, 6),
                                        data.getString(ARG_JOB_CARD_ID).toString(),
                                        false,
                                        vehicleType
                                    )
                                )
                            }

                            CONSTANT_KEYS.VIEW_CUSTOMER -> {
                                requireContext().startActivity(
                                    AddCustomerActivity.getIntent(
                                        requireContext(),
                                        AddCustomerActivity.ARG_VIEW,
                                        data.getString(AddCustomerActivity.ARG_CUSTOMER_ID),
                                        null,
                                        null,
                                        null
                                    )
                                )
                            }

                            CONSTANT_KEYS.VIEW_VEHICLE -> requireContext().startActivity(
                                VehicleDetailsActivity.getViewVehicleIntent(
                                    requireContext(),
                                    data.get(VehicleDetailsActivity.VEHICLE) as Vehicle
                                )
                            )

                            CONSTANT_KEYS.CANCEL_JOB_CARD -> {
                                EventsManager.post(
                                    CancelEvent(
                                        CancelEvent.JOBCARD,
                                        data.getString(ARG_JOB_CARD_ID).toString()
                                    )
                                )
                            }

                            CONSTANT_KEYS.PRE_DELIVERY_CHECK -> {
                                //start activity to capture PDC
                                requireContext().startActivity(
                                    PdcActivity.getIntent(
                                        requireContext(),
                                        data.getString(ARG_JOB_CARD_ID).toString(),
                                        data.getString(ARG_DISPLAY_ID).toString()
                                    )
                                )
                            }
                        }
                    }

                    JobCard.STATUS_COMPLETED -> {
                        if (adapterPosition == -1) {
                            return@setOnClickListener
                        }
                        when (ctaList[adapterPosition].title) {
                            CONSTANT_KEYS.CARPM_CLEAR_CODES -> {
                                requireContext().startActivity(
                                    ClearCodeActivity.getIntent(
                                        requireContext(),
                                        data.getString(ClearCodeActivity.ARG_JOB_ID)
                                    )
                                )
                            }

                            CONSTANT_KEYS.CUSTOMER_DETAILS -> {
                                requireContext().startActivity(
                                    AddCustomerActivity.getIntent(
                                        requireContext(),
                                        AddCustomerActivity.ARG_VIEW,
                                        data.getString(AddCustomerActivity.ARG_CUSTOMER_ID),
                                        null,
                                        null,
                                        null
                                    )
                                )
                            }

                            CONSTANT_KEYS.VEHICLE_DETAILS -> {
                                requireContext().startActivity(
                                    VehicleDetailsActivity.getViewVehicleIntent(
                                        requireContext(),
                                        data.get(VehicleDetailsActivity.VEHICLE) as Vehicle
                                    )
                                )
                            }

                            CONSTANT_KEYS.INVOICE_REMARKS -> {
                                requireContext().startActivity(
                                    InvoiceRemarksActivity.getIntent(
                                        requireContext(), data.getString(ARG_JOB_CARD_ID).toString()
                                    )
                                )
                            }

                            CONSTANT_KEYS.MARK_VEHICLE_DELIVERED -> {
                                EventsManager.post(
                                    ChangeCardStatusEvent(
                                        data.getString(
                                            ARG_JOB_CARD_ID
                                        ), JobCard.STATUS_CLOSED
                                    )
                                )
                            }

                            CONSTANT_KEYS.VIEW_ESTIMATE -> {
                                requireContext().startActivity(
                                    OutwardingProcessActivity.getEstimatorIntent(
                                        requireContext(),
                                        data.getString(ARG_DISPLAY_ID),
                                        data.getString(ARG_JOB_CARD_ID).toString(),
                                        true,
                                        vehicleType
                                    )
                                )
                            }

                            CONSTANT_KEYS.PREVIEW_JOB_CARD -> {
                                pdfMediator.startJobCardDetailsPdf(
                                    requireContext(),
                                    data.getString(ARG_JOB_CARD_ID).toString(),
                                    data.getString(ARG_DISPLAY_ID).toString(),
                                    Source.COMPLETED
                                )
                            }

                            CONSTANT_KEYS.VIEW_ISSUE_PARTS -> {
                                requireContext().startActivity(
                                    MrnActivity.getIntent(
                                        requireContext(), data.getString(ARG_JOB_CARD_ID).toString()
                                    )
                                )
                            }

                            CONSTANT_KEYS.PRINT_PDC -> {
                                pdfMediator.startJobCardPdcPdf(
                                    requireContext(),
                                    data.getString(ARG_JOB_CARD_ID).toString(),
                                    data.getString(ARG_DISPLAY_ID).toString()
                                )
                            }

                            CONSTANT_KEYS.GATE_PASS -> {
                                pdfMediator.startGatePassPdf(
                                    requireContext(),
                                    data.getString(ARG_JOB_CARD_ID).toString(),
                                    data.getString(ARG_DISPLAY_ID).toString()
                                )
                            }

                            CONSTANT_KEYS.VIEW_CUSTOMER -> {
                                requireContext().startActivity(
                                    AddCustomerActivity.getIntent(
                                        requireContext(),
                                        AddCustomerActivity.ARG_VIEW,
                                        data.getString(AddCustomerActivity.ARG_CUSTOMER_ID),
                                        null,
                                        null,
                                        null
                                    )
                                )
                            }

                            CONSTANT_KEYS.VIEW_VEHICLE -> {
                                requireContext().startActivity(
                                    VehicleDetailsActivity.getViewVehicleIntent(
                                        requireContext(),
                                        data.get(VehicleDetailsActivity.VEHICLE) as Vehicle
                                    )
                                )
                            }
                        }
                    }

                    JobCard.STATUS_CLOSED -> {
                        when (adapterPosition) {
                            0 -> requireContext().startActivity(
                                AddCustomerActivity.getIntent(
                                    requireContext(),
                                    AddCustomerActivity.ARG_VIEW,
                                    data.getString(AddCustomerActivity.ARG_CUSTOMER_ID),
                                    null,
                                    null,
                                    null
                                )
                            )

                            1 -> requireContext().startActivity(
                                VehicleDetailsActivity.getViewVehicleIntent(
                                    requireContext(),
                                    data.get(VehicleDetailsActivity.VEHICLE) as Vehicle
                                )
                            )

                            2 -> {
                                if (isQuickJc) {
                                    requireContext().startActivity(
                                        OutwardingProcessActivity.getEstimatorIntent(
                                            requireContext(),
                                            data.getString(ARG_DISPLAY_ID),
                                            data.getString(ARG_JOB_CARD_ID).toString(),
                                            true,
                                            vehicleType
                                        )
                                    )
                                } else {
                                    pdfMediator.startJobCardDetailsPdf(
                                        requireContext(),
                                        data.getString(ARG_JOB_CARD_ID).toString(),
                                        data.getString(ARG_DISPLAY_ID).toString(),
                                        Source.CLOSED
                                    )
                                }
                            }

                            3 -> requireContext().startActivity(
                                OutwardingProcessActivity.getEstimatorIntent(
                                    requireContext(),
                                    data.getString(ARG_DISPLAY_ID),
                                    data.getString(ARG_JOB_CARD_ID).toString(),
                                    true,
                                    vehicleType
                                )
                            )
                        }
                    }

                    JobCard.STATUS_CANCELLED -> {
                        when (adapterPosition) {
                            0 -> requireContext().startActivity(
                                AddCustomerActivity.getIntent(
                                    requireContext(),
                                    AddCustomerActivity.ARG_VIEW,
                                    data.getString(AddCustomerActivity.ARG_CUSTOMER_ID),
                                    null,
                                    null,
                                    null
                                )
                            )

                            1 -> requireContext().startActivity(
                                VehicleDetailsActivity.getViewVehicleIntent(
                                    requireContext(),
                                    data.get(VehicleDetailsActivity.VEHICLE) as Vehicle
                                )
                            )
                        }
                    }

                    Invoice.STATUS_PROFORMA -> {
                        if (data.getString(ARG_TYPE) == InvoiceListingAdapter.type) {
                            when (adapterPosition) {
                                0 -> pdfMediator.startProformaEstimatePdf(
                                    requireContext(),
                                    data.getString(ARG_INVOICE_ID).toString(),
                                    "Estimate"
                                )

                                1 -> requireContext().startActivity(
                                    InvoiceRemarksActivity.getIntent(
                                        requireContext(), data.getString(ARG_JOB_CARD_ID).toString()
                                    )
                                )

                                2 -> requireContext().startActivity(
                                    AddCustomerActivity.getIntent(
                                        requireContext(),
                                        AddCustomerActivity.ARG_VIEW,
                                        data.getString(AddCustomerActivity.ARG_CUSTOMER_ID),
                                        null,
                                        null,
                                        null
                                    )
                                )

                                3 -> requireContext().startActivity(
                                    VehicleDetailsActivity.getViewVehicleIntent(
                                        requireContext(),
                                        data.get(VehicleDetailsActivity.VEHICLE) as Vehicle
                                    )
                                )
                            }
                        } else {
                            when (adapterPosition) {
                                0 -> requireContext().startActivity(
                                    AddCustomerActivity.getIntent(
                                        requireContext(),
                                        AddCustomerActivity.ARG_VIEW,
                                        data.getString(AddCustomerActivity.ARG_CUSTOMER_ID),
                                        null,
                                        null,
                                        null
                                    )
                                )
                            }
                        }
                    }

                    Invoice.STATUS_INVOICED, Invoice.STATUS_PAID_PARTIAL -> {
                        if (data.getString(ARG_TYPE) == InvoiceListingAdapter.type) {
                            when (ctaList[adapterPosition].title) {
                                "View Customer" -> requireContext().startActivity(
                                    AddCustomerActivity.getIntent(
                                        requireContext(),
                                        AddCustomerActivity.ARG_VIEW,
                                        data.getString(AddCustomerActivity.ARG_CUSTOMER_ID),
                                        null,
                                        null,
                                        null
                                    )
                                )

                                "View Vehicle" -> requireContext().startActivity(
                                    VehicleDetailsActivity.getViewVehicleIntent(
                                        requireContext(),
                                        data.get(VehicleDetailsActivity.VEHICLE) as Vehicle
                                    )
                                )

                                "Cancel Invoice" -> {
                                    val cancel = CancelEvent(
                                        CancelEvent.INVOICE,
                                        data.getString(ARG_INVOICE_ID).toString()
                                    )
                                    EventsManager.post(cancel)
                                }

                                "View Feedback" -> requireContext().startActivity(
                                    NewCustomerFeedBackActivity.getIntent(
                                        requireContext(),
                                        data.getParcelable(ARG_FEEDBACK),
                                        data.getString(ARG_JOB_CARD_ID).toString()
                                    )
                                )
                            }
                        } else {
                            when (adapterPosition) {
                                0 -> requireContext().startActivity(
                                    AddCustomerActivity.getIntent(
                                        requireContext(),
                                        AddCustomerActivity.ARG_VIEW,
                                        data.getString(AddCustomerActivity.ARG_CUSTOMER_ID),
                                        null,
                                        null,
                                        null
                                    )
                                )
                            }
                        }
                    }

                    Invoice.STATUS_PAID -> {
                        when (adapterPosition) {
                            0 -> pdfMediator.startGatePassPdf(
                                requireContext(),
                                data.getString(ARG_JOB_CARD_ID).toString(),
                                data.getString(ARG_DISPLAY_ID).toString()
                            )

                            1 -> requireContext().startActivity(
                                AddCustomerActivity.getIntent(
                                    requireContext(),
                                    AddCustomerActivity.ARG_VIEW,
                                    data.getString(AddCustomerActivity.ARG_CUSTOMER_ID),
                                    null,
                                    null,
                                    null
                                )
                            )

                            2 -> requireContext().startActivity(
                                VehicleDetailsActivity.getViewVehicleIntent(
                                    requireContext(),
                                    data.get(VehicleDetailsActivity.VEHICLE) as Vehicle
                                )
                            )

                            3 -> requireContext().startActivity(
                                NewCustomerFeedBackActivity.getIntent(
                                    requireContext(),
                                    data.getParcelable(ARG_FEEDBACK),
                                    data.getString(ARG_JOB_CARD_ID).toString()
                                )
                            )
                        }
                    }

                    Invoice.STATUS_CANCEL -> {
                        when (adapterPosition) {
                            0 -> requireContext().startActivity(
                                AddCustomerActivity.getIntent(
                                    requireContext(),
                                    AddCustomerActivity.ARG_VIEW,
                                    data.getString(AddCustomerActivity.ARG_CUSTOMER_ID),
                                    null,
                                    null,
                                    null
                                )
                            )

                            1 -> requireContext().startActivity(
                                VehicleDetailsActivity.getViewVehicleIntent(
                                    requireContext(),
                                    data.get(VehicleDetailsActivity.VEHICLE) as Vehicle
                                )
                            )
                        }
                    }

                    Appointment.STATUS_CONFIRMED -> {
                        // STATUS_CONFIRMED represents STATUS_TODAY
                        when (adapterPosition) {
                            0 -> {
                                if (SharedPrefHelper.isServiceAdvisorEnabled()) {
                                    val reassignEvent = AppointmentReassignEvent(
                                        data.getString(ARG_ID),
                                        data.getString(ARG_SERVICE_ADVISOR_ID)
                                    )
                                    EventsManager.post(reassignEvent)
                                } else {
                                    val cancel = CancelEvent(
                                        CancelEvent.APPOINTMENT, data.getString(ARG_ID).toString()
                                    )
                                    EventsManager.post(cancel)
                                }
                            }

                            1 -> {
                                val cancel = CancelEvent(
                                    CancelEvent.APPOINTMENT, data.getString(ARG_ID).toString()
                                )
                                EventsManager.post(cancel)
                            }
                        }

                        // Timber.e("Cancel Appointment")
                    }

                    Appointment.STATUS_UPCOMING -> {
                        when (adapterPosition) {
                            0 -> {
                                if (SharedPrefHelper.isServiceAdvisorEnabled()) {
                                    val reassignEvent = AppointmentReassignEvent(
                                        data.getString(ARG_ID),
                                        data.getString(ARG_SERVICE_ADVISOR_ID)
                                    )
                                    EventsManager.post(reassignEvent)
                                } else {
                                    val cancel = CancelEvent(
                                        CancelEvent.APPOINTMENT, data.getString(ARG_ID).toString()
                                    )
                                    EventsManager.post(cancel)
                                }
                            }

                            1 -> {
                                val cancel = CancelEvent(
                                    CancelEvent.APPOINTMENT, data.getString(ARG_ID).toString()
                                )
                                EventsManager.post(cancel)
                            }
                        }
                        // Timber.e("Cancel Appointment")
                    }

                    Appointment.STATUS_PAST -> {
                        when (adapterPosition) {
                            1 -> {
                                val cancel = CancelEvent(
                                    CancelEvent.APPOINTMENT, data.getString(ARG_ID).toString()
                                )
                                EventsManager.post(cancel)
                            }
                        }
                        // Timber.e("Cancel Appointment")
                    }

                    Appointment.STATUS_REQUESTED -> {
                        val intent = Intent(context, RescheduleActivity::class.java)
                        intent.putExtra(
                            RescheduleActivity.ARG_ID, data.getString(ARG_ID).toString()
                        )
                        intent.putExtra(
                            RescheduleActivity.ARG_TYPE,
                            RescheduleActivity.ARG_IS_REJECT_APPOINTMENT
                        )
                        startActivity(intent)
                    }

                    else -> {
                        // called from estimater or profroma
                        when (adapterPosition) {
                            0 -> {
                                requireActivity().startActivityForResult(
                                    SearchActivity.getIntent(
                                        requireContext(),
                                        SearchActivity.ARG_LABOUR_SEARCH,
                                        data.getString(ARG_JOB_CARD_ID).toString(),
                                        vehicleType,
                                        vehicleAmcId
                                    ), REQUEST_CODE_LABOUR
                                )
                            }

                            1 -> {
                                requireActivity().startActivityForResult(
                                    SearchActivity.getIntent(
                                        requireContext(),
                                        SearchActivity.ARG_PART_SEARCH,
                                        data.getString(ARG_JOB_CARD_ID).toString(),
                                        vehicleType,
                                        vehicleAmcId
                                    ), REQUEST_CODE_PART
                                )
                            }

                            2 -> {
                                if (SharedPrefHelper.isPackagesEnabled()) {
                                    if (data.getBoolean(ARG_IS_FROM_PROFROMA)) {
                                        requireActivity().startActivityForResult(
                                            AddServicePackageActivity.getIntent(
                                                activity as OutwardingProcessActivity,
                                                data.getString(ARG_JOB_CARD_ID).toString(),
                                                data.getString(ARG_INVOICE_ID).toString(),
                                                true,
                                                data.getStringArrayList(ARG_EXISTING_ID)!!
                                            ), OutwardingProcessActivity.REQUEST_CODE_PACKAGES
                                        )
                                    } else {
                                        requireActivity().startActivityForResult(
                                            AddServicePackageActivity.getIntent(
                                                activity as OutwardingProcessActivity,
                                                data.getString(ARG_JOB_CARD_ID).toString(),
                                                "",
                                                false,
                                                data.getStringArrayList(ARG_EXISTING_ID)!!
                                            ), OutwardingProcessActivity.REQUEST_CODE_PACKAGES
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                dismiss()
            }
        }
    }

    private inner class MoreCtaAdapter(
        private val isQuickJc: Boolean, val cardType: String, private val data: Bundle
    ) : RecyclerView.Adapter<ViewHolder>() {

        init {
            when (cardType) {
                ARG_CUSTOMER_APPROVAL -> {
                    val isViewOnly = data.getByte(OutwardingProcessActivity.ARG_IS_VIEW_ONLY)
                    if (isViewOnly != 0.toByte()) {
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_assignment_turned_in_black_24dp,
                                "View Customer Approval"
                            )
                        )
                    } else {
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_assignment_turned_in_black_24dp,
                                "Update Customer Approval"
                            )
                        )
                    }
                }

                ARG_IS_AMC -> {

                    ctaList.add(MoreCtaObj(R.drawable.ic_job_cancel, "Cancel AMC"))
                }

                JobCard.STATUS_INITIATED -> {
                    if (isQuickJc) {
                        ctaList.add(MoreCtaObj(R.drawable.ic_person_black_24dp, "Customer Details"))
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_directions_car_black_24dp, "Vehicle Details"
                            )
                        )
                    } else {
                        ctaList.add(MoreCtaObj(R.drawable.ic_person_black_24dp, "View Customer"))
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_directions_car_black_24dp, "View Vehicle"
                            )
                        )
                    }
                    // Timber.d("arg init" + ctaList.size)
                }

                JobCard.STATUS_IN_PROGRESS -> {

                    if (isQuickJc) {
                        ctaList.add(MoreCtaObj(R.drawable.ic_person_black_24dp, "Customer Details"))
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_directions_car_black_24dp, "Vehicle Details"
                            )
                        )
                        if (SharedPrefHelper.isMrnEnabled()) {
                            ctaList.add(
                                MoreCtaObj(
                                    R.drawable.ic_assignment_turned_in_black_24dp,
                                    "View Issued Parts"
                                )
                            )
                        }
                    } else {
                       // ctaList.add(MoreCtaObj(R.drawable.ic_carpm_logo, "Scan with CarPM"))
                      //  ctaList.add(MoreCtaObj(R.drawable.ic_carpm_logo, "View CarPM Reports"))

                        if (data.getBoolean(CarPmAuthActivity.ARG_IS_CARPM_SCANNED)) {
                            ctaList.add(MoreCtaObj(R.drawable.ic_carpm_logo, "View CarPM Reports"))
                        } else {
                            ctaList.add(MoreCtaObj(R.drawable.ic_carpm_logo, "Scan with CarPM"))
                        }

                        ctaList.add(MoreCtaObj(R.drawable.ic_jobcard_pdf, "Preview Job Card"))
                        ctaList.add(MoreCtaObj(R.drawable.ic_estimate, "Update Estimate"))
                        ctaList.add(MoreCtaObj(R.drawable.ic_person_black_24dp, "View Customer"))
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_directions_car_black_24dp, "View Vehicle"
                            )
                        )
                        if (SharedPrefHelper.getApproval().not() && SharedPrefHelper.getUserRole()
                                .equals(Role.USER_ROLE_ADMIN, true)
                        ) {
                            ctaList.add(MoreCtaObj(R.drawable.ic_job_cancel, "Cancel Job Card"))
                        }
                        if (SharedPrefHelper.isMrnEnabled()) {
                            ctaList.add(
                                MoreCtaObj(
                                    R.drawable.ic_assignment_turned_in_black_24dp,
                                    "View Issued Parts"
                                )
                            )
                        }
                    }
                    //add CTA for PDC
                    //if PDC is completed then do not add
                    val isPdcCompleted = (arguments!!.getBundle(ARG_DATA)
                        ?.getByte(ARG_IS_PDC_COMPLETED)) == 1.toByte()

                    if (SharedPrefHelper.isPreDeliveryCheckEnabled()) {
                        ctaList.add(MoreCtaObj(R.drawable.ic_pdc_check_24, "Pre-Delivery Check"))
                    }
                    // Timber.d("arg in progress" + ctaList.size)
                }

                JobCard.STATUS_COMPLETED -> {
                    if (isQuickJc) {
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_person_black_24dp, CONSTANT_KEYS.CUSTOMER_DETAILS
                            )
                        )
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_directions_car_black_24dp,
                                CONSTANT_KEYS.VEHICLE_DETAILS
                            )
                        )
                        if (data.getString(ARG_INVOICE_STATUS) == Invoice.STATUS_PROFORMA) {
                            ctaList.add(
                                MoreCtaObj(
                                    R.drawable.ic_cloud_upload_grey_24dp,
                                    CONSTANT_KEYS.INVOICE_REMARKS
                                )
                            )
                        }
                        if (data.getString(ARG_INVOICE_STATUS) != Invoice.STATUS_INVOICED || data.getString(
                                ARG_INVOICE_STATUS
                            ) != Invoice.STATUS_PAID_PARTIAL && SharedPrefHelper.isJobCardClosureAllowed() && data.getParcelable<JobCard>(
                                ARG_JOB_CARD
                            ) != null
                        ) {
                            ctaList.add(
                                MoreCtaObj(
                                    R.drawable.ic_done_all_black_24dp,
                                    CONSTANT_KEYS.MARK_VEHICLE_DELIVERED
                                )
                            )
                        }
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_cloud_upload_grey_24dp, CONSTANT_KEYS.VIEW_ESTIMATE
                            )
                        )
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_jobcard_pdf, CONSTANT_KEYS.PREVIEW_JOB_CARD
                            )
                        )
                        if (SharedPrefHelper.isMrnEnabled()) {
                            ctaList.add(
                                MoreCtaObj(
                                    R.drawable.ic_assignment_turned_in_black_24dp,
                                    CONSTANT_KEYS.VIEW_ISSUE_PARTS
                                )
                            )
                        }
                        //add CTA for PDC
                        //if PDC is completed then do not add
                        val isPdcCompleted = (arguments!!.getBundle(ARG_DATA)
                            ?.getByte(ARG_IS_PDC_COMPLETED)) == 1.toByte()
                        if (SharedPrefHelper.isPreDeliveryCheckEnabled() and isPdcCompleted) {
                            ctaList.add(
                                MoreCtaObj(
                                    R.drawable.ic_pdc_check_24, CONSTANT_KEYS.PRINT_PDC
                                )
                            )
                        }
                    } else {
                        if (data.getBoolean(CarPmAuthActivity.ARG_IS_CARPM_SCANNED)) {
                            ctaList.add(MoreCtaObj(R.drawable.ic_carpm_logo, "Clear Error Codes"))
                        }
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_person_black_24dp, CONSTANT_KEYS.VIEW_CUSTOMER
                            )
                        )
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_directions_car_black_24dp, CONSTANT_KEYS.VIEW_VEHICLE
                            )
                        )
                        if (data.getString(ARG_INVOICE_STATUS) == Invoice.STATUS_PROFORMA) {
                            ctaList.add(
                                MoreCtaObj(
                                    R.drawable.ic_cloud_upload_grey_24dp,
                                    CONSTANT_KEYS.INVOICE_REMARKS
                                )
                            )
                        }
                        if (SharedPrefHelper.isJobCardClosureAllowed() && data.getParcelable<JobCard>(
                                ARG_JOB_CARD
                            ) != null
                        ) {
                            ctaList.add(
                                MoreCtaObj(
                                    R.drawable.ic_done_all_black_24dp,
                                    CONSTANT_KEYS.MARK_VEHICLE_DELIVERED
                                )
                            )
                        }
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_jobcard_pdf, CONSTANT_KEYS.PREVIEW_JOB_CARD
                            )
                        )
                        if (SharedPrefHelper.isMrnEnabled()) {
                            ctaList.add(
                                MoreCtaObj(
                                    R.drawable.ic_assignment_turned_in_black_24dp,
                                    CONSTANT_KEYS.VIEW_ISSUE_PARTS
                                )
                            )
                        }

                        //add CTA for PDC
                        //if PDC is completed then do not add
                        val isPdcCompleted = (arguments!!.getBundle(ARG_DATA)
                            ?.getByte(ARG_IS_PDC_COMPLETED)) == 1.toByte()
                        if (SharedPrefHelper.isPreDeliveryCheckEnabled() and isPdcCompleted) {
                            ctaList.add(
                                MoreCtaObj(
                                    R.drawable.ic_pdc_check_24, CONSTANT_KEYS.PRINT_PDC
                                )
                            )
                        }
                    }

                    // Timber.d("arg completed" + ctaList.size)
                }

                JobCard.STATUS_CLOSED -> {
                    if (isQuickJc) {
                        ctaList.add(MoreCtaObj(R.drawable.ic_person_black_24dp, "Customer Details"))
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_directions_car_black_24dp, "Vehicle Details"
                            )
                        )
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_cloud_upload_grey_24dp, "View Estimate"
                            )
                        )
                    } else {
                        ctaList.add(MoreCtaObj(R.drawable.ic_person_black_24dp, "View Customer"))
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_directions_car_black_24dp, "View Vehicle"
                            )
                        )
                    }
                    ctaList.add(MoreCtaObj(R.drawable.ic_jobcard_pdf, "Preview Job Card"))

                    // Timber.d("arg closed" + ctaList.size)
                }

                JobCard.STATUS_CANCELLED -> {
                    if (isQuickJc) {
                        ctaList.add(MoreCtaObj(R.drawable.ic_person_black_24dp, "Customer Details"))
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_directions_car_black_24dp, "Vehicle Details"
                            )
                        )
                    } else {
                        ctaList.add(MoreCtaObj(R.drawable.ic_person_black_24dp, "View Customer"))
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_directions_car_black_24dp, "View Vehicle"
                            )
                        )
                    }
                }

                Invoice.STATUS_PROFORMA -> {
                    if (data.getString(ARG_TYPE) == InvoiceListingAdapter.type) {
                        ctaList.add(MoreCtaObj(R.drawable.ic_gatepass, "Print Estimate"))
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_cloud_upload_grey_24dp, "Invoice Remarks"
                            )
                        )
                        ctaList.add(MoreCtaObj(R.drawable.ic_person_black_24dp, "View Customer"))
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_directions_car_black_24dp, "View Vehicle"
                            )
                        )
                    } else {
                        ctaList.add(MoreCtaObj(R.drawable.ic_person_black_24dp, "View Customer"))
                    }
                }

                Invoice.STATUS_INVOICED, Invoice.STATUS_PAID_PARTIAL -> {
                    if (data.getString(ARG_TYPE) == InvoiceListingAdapter.type) {
                        ctaList.add(MoreCtaObj(R.drawable.ic_person_black_24dp, "View Customer"))
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_directions_car_black_24dp, "View Vehicle"
                            )
                        )
                        if (cardType != Invoice.STATUS_PAID_PARTIAL && SharedPrefHelper.getApproval()
                                .not()
                        ) {
                            ctaList.add(MoreCtaObj(R.drawable.ic_job_cancel, "Cancel Invoice"))
                        }
//                        ctaList.add(MoreCtaObj(R.drawable.ic_feedback_black_24dp, if (data.getParcelable<Feedback>(ARG_FEEDBACK) == null) "Get Feedback" else "View Feedback"))
                        if (data.getParcelable<Feedback>(ARG_FEEDBACK) != null) {
                            ctaList.add(
                                MoreCtaObj(
                                    R.drawable.ic_feedback_black_24dp, "View Feedback"
                                )
                            )
                        }

                    } else {
                        ctaList.add(MoreCtaObj(R.drawable.ic_person_black_24dp, "View Customer"))
                    }
                }

                Invoice.STATUS_PAID -> {
                    ctaList.add(MoreCtaObj(R.drawable.ic_gatepass, "Gate Pass"))
                    ctaList.add(MoreCtaObj(R.drawable.ic_person_black_24dp, "View Customer"))
                    ctaList.add(MoreCtaObj(R.drawable.ic_directions_car_black_24dp, "View Vehicle"))
//                    ctaList.add(MoreCtaObj(R.drawable.ic_feedback_black_24dp, if (data.getParcelable<Feedback>(ARG_FEEDBACK) == null) "Get Feedback" else "View Feedback"))
                    if (data.getParcelable<Feedback>(ARG_FEEDBACK) != null) {
                        ctaList.add(MoreCtaObj(R.drawable.ic_feedback_black_24dp, "View Feedback"))
                    }

                }

                Invoice.STATUS_CANCEL -> {
                    ctaList.add(MoreCtaObj(R.drawable.ic_person_black_24dp, "View Customer"))
                    ctaList.add(MoreCtaObj(R.drawable.ic_directions_car_black_24dp, "View Vehicle"))
                }

                Appointment.STATUS_CONFIRMED -> {
                    if (SharedPrefHelper.isServiceAdvisorEnabled()) {
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_person_black_24dp, "Reassign Service Advisor"
                            )
                        )
                    }
                    ctaList.add(
                        MoreCtaObj(
                            R.drawable.ic_appointment_cancel_black_24dp, "Cancel Appointment"
                        )
                    )
                }

                Appointment.STATUS_UPCOMING -> {
                    if (SharedPrefHelper.isServiceAdvisorEnabled()) {
                        ctaList.add(
                            MoreCtaObj(
                                R.drawable.ic_person_black_24dp, "Reassign Service Advisor"
                            )
                        )
                    }
                    ctaList.add(
                        MoreCtaObj(
                            R.drawable.ic_appointment_cancel_black_24dp, "Cancel Appointment"
                        )
                    )
                }

                Appointment.STATUS_PAST -> {
                    ctaList.add(
                        MoreCtaObj(
                            R.drawable.ic_appointment_cancel_black_24dp, "Cancel Appointment"
                        )
                    )
                }

                Appointment.STATUS_REQUESTED -> {
                    ctaList.add(MoreCtaObj(R.drawable.ic_cancel_black_24dp, "Decline Appointment"))
                }

                else -> {
                    ctaList.add(MoreCtaObj(R.drawable.ic_labour_add, "Add Labour"))
                    ctaList.add(MoreCtaObj(R.drawable.ic_spare_add, "Add Part"))
                    if (SharedPrefHelper.isPackagesEnabled()) {
                        ctaList.add(MoreCtaObj(R.drawable.ic_offer_add, "Add Packages"))
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.row_more_cta, parent, false)
            return ViewHolder(view, isQuickJc, cardType, data)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val cta = ctaList[position]
            holder.ctaTitleView.text = cta.title
            holder.ctaIconView.image = AppCompatResources.getDrawable(requireContext(), cta.icon)
        }

        override fun getItemCount(): Int {
            return ctaList.size
        }
    }

    companion object {

        const val ARG_VEHICLE_AMC_ID = "vehicle-amc-id"
        const val ARG_CARD_TYPE = "card_type"
        const val ARG_TYPE = "type"
        const val ARG_DATA = "data"
        const val ARG_IS_QUICK_JC = "arg_is_quick_jc"
        const val ARG_JOB_CARD_ID = "job_card_id"
        const val ARG_IS_FROM_PROFROMA = "is_invoice"
        const val ARG_EXISTING_ID = "existing_id"
        const val ARG_JOB_CARD = "job_card"
        const val ARG_INVOICE_ID = "invoice_id"
        const val ARG_DISPLAY_ID = "DISPLAY_ID"
        const val ARG_INVOICE_STATUS = "status"

        //        const val ARG_INVOICE_REMARKS = "remarks"
        const val ARG_VEHICLE_TYPE = "arg_vehicle_type"
        const val ARG_FEEDBACK = "arg_feedback"
        const val REQUEST_CODE_PART = 1090
        const val REQUEST_CODE_LABOUR = 1091
        const val ARG_ID = "arg_id"
        const val ARG_SERVICE_ADVISOR_ID = "arg_service_advisor_id"
        const val ARG_IS_AMC = "amc"
        const val ARG_CUSTOMER_APPROVAL = "customer_approval"
        const val ARG_IS_CUSTOMER_APPROVAL = "is_customer_approval"
        const val ARG_IS_PDC_COMPLETED = "pdc_completed"

        //CarPM
        const val ARG_IS_CARPM_SCANNED = "arg_carpm_scanned"
        const val ARG_CARPM_SCAN_ID = "arg_carpm_scan_id"

        fun newInstance(
            cardType: String, bundle: Bundle, vehicleType: String?
        ): MoreCtaListDialogFragment {
            val fragment = MoreCtaListDialogFragment()
            val args = Bundle()
            args.putString(ARG_CARD_TYPE, cardType)
            args.putBundle(ARG_DATA, bundle)
            args.putString(ARG_VEHICLE_TYPE, vehicleType)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(
            isQuickJc: Boolean, cardType: String, bundle: Bundle, vehicleType: String?
        ): MoreCtaListDialogFragment {
            val fragment = MoreCtaListDialogFragment()
            val args = Bundle()
            args.putString(ARG_CARD_TYPE, cardType)
            args.putBoolean(ARG_IS_QUICK_JC, isQuickJc)
            args.putBundle(ARG_DATA, bundle)
            args.putString(ARG_VEHICLE_TYPE, vehicleType)
            fragment.arguments = args
            return fragment
        }
    }

    data class MoreCtaObj(val icon: Int, val title: String)

    internal class CONSTANT_KEYS {
        companion object {
            val CUSTOMER_DETAILS = "Customer Details"
            val VEHICLE_DETAILS = "Vehicle Details"
            val VIEW_ISSUE_PARTS = "View Issued Parts"
            val PREVIEW_JOB_CARD = "Preview Job Card"
            val UPDATE_ESTIMATE = "Update Estimate"
            val VIEW_CUSTOMER = "View Customer"
            val VIEW_VEHICLE = "View Vehicle"
            val CANCEL_JOB_CARD = "Cancel Job Card"
            val PRE_DELIVERY_CHECK = "Pre-Delivery Check"
            val INVOICE_REMARKS = "Invoice Remarks"
            val VIEW_ESTIMATE = "View Estimate"
            val PRINT_PDC = "Print PDC"
            val GATE_PASS = "Gate Pass"
            val MARK_VEHICLE_DELIVERED = "Mark as Vehicle Delivered"
            val CARPM_SCAN_CODES = "Scan with CarPM"
            val CARPM_SCANNED_REPORTS = "View CarPM Reports"
            val CARPM_CLEAR_CODES = "Clear Error Codes"

        }

    }
}
