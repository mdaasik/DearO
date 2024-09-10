package com.carworkz.dearo.appointments.createappointment.servicePackages

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.ActionButtonClickEvent
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.appointments.createappointment.CreateAppointmentActivity
import com.carworkz.dearo.appointments.createappointment.ICreateAppointmentInteraction
import com.carworkz.dearo.base.BaseActivity
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.domain.entities.Appointment
import com.carworkz.dearo.domain.entities.AppointmentPost
import com.carworkz.dearo.domain.entities.Category
import com.carworkz.dearo.domain.entities.ServicePackage
import com.carworkz.dearo.extensions.*
import com.carworkz.dearo.utils.Utility
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import javax.inject.Inject

class ServicePackageFragment : BaseFragment(),
        ServicePackageContract.View,
        EventsManager.EventSubscriber {

    @Inject
    lateinit var presenter: ServicePackagePresenter

    @Inject
    lateinit var screenTracker: ScreenTracker

    var interaction: ICreateAppointmentInteraction? = null
    private lateinit var serviceRecyclerView: RecyclerView
    private lateinit var serviceAdapter: ServicePackageAdapter
    private lateinit var filterSpinner: Spinner
    private lateinit var categoryAdapter: ArrayAdapter<String>
    private lateinit var appointmentId: String
    private var categoryList = ArrayList<String>()
    private var packageList = ArrayList<ServicePackage>()
    private lateinit var appointment: Appointment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ((requireActivity().application) as DearOApplication)
                .repositoryComponent
                .COMPONENT(ServicePackagePresenterModule(this))
                .inject(this)
        screenTracker.sendScreenEvent(activity, ScreenTracker.SCREEN_ADD_APPMNT_SERVICE_PKG, javaClass.name)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_service_package, container, false)
        serviceRecyclerView = view?.findViewById(R.id.rv_service_packages) as RecyclerView
        serviceRecyclerView.layoutManager = GridLayoutManager(container?.context, 2)
        serviceRecyclerView.addItemDecoration(DividerItemDecoration(serviceRecyclerView.context, LinearLayout.HORIZONTAL))
        serviceRecyclerView.addItemDecoration(DividerItemDecoration(serviceRecyclerView.context, LinearLayout.VERTICAL))
        filterSpinner = view.find(R.id.filterSpinner)
        appointment = (context as CreateAppointmentActivity).appointment.deepCopy()
        displayFilter(appointment.suggestedPackages?.filter?.category)
        displayPackages(appointment.suggestedPackages?.list)
        return view
    }

    override fun onResume() {
        super.onResume()
        EventsManager.register(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ICreateAppointmentInteraction) {
            interaction = context
        } else {
            throw IllegalStateException("Activity must implement ICreateJobCardInteraction interface ")
        }
        if ((context as CreateAppointmentActivity).appointment.id?.isNotEmpty() == true) {
            this.appointmentId = (context).appointment.id ?: ""
            Timber.d(appointmentId)
        } else {
            throw IllegalStateException("Appointment Id not found")
        }
    }

    override fun onStop() {
        super.onStop()
        EventsManager.unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        interaction = null
        presenter.detachView()
    }

    override fun showProgressIndicator() {
        (activity as BaseActivity).showProgressBar()
    }

    override fun dismissProgressIndicator() {
        (activity as BaseActivity).dismissProgressBar()
    }

    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
    }

    override fun moveToNextScreen(appointment: Appointment) {
        Utility.hideSoftKeyboard(activity)
        (context as CreateAppointmentActivity).appointment.timeSlots = appointment.timeSlots
        (context as CreateAppointmentActivity).appointment.packageIds = appointment.packageIds
        Timber.d((context as CreateAppointmentActivity).appointment.vehicle.toString())
        interaction?.onSuccess()
    }

    override fun displayPackages(packages: List<ServicePackage>?) {
        packageList.clear()
        packages?.forEach { packageList.add(it) }
        serviceAdapter = ServicePackageAdapter(packageList)
        appointment.packageIds?.forEach { index ->
            packageList.filter { it.packageId == index }.forEach { serviceAdapter.selectedList.add(it) }
        }
        serviceRecyclerView.adapter = serviceAdapter
    }

    override fun displayFilteredPackages(filterType: String) {
        Timber.d("Filter Applied")
        if (filterType == ALL) {
            serviceAdapter.applyFilter(packageList)
        } else {
            serviceAdapter.applyFilter(packageList.filter { it.category == filterType })
        }
    }

    override fun displayFilter(filter: Category?) {
        categoryList.clear()
        categoryList.add(0, ALL)
        filter?.value?.forEach { categoryList.add(it) }
        categoryAdapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categoryList)
        filterSpinner.adapter = categoryAdapter
        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                presenter.filterPackages(categoryList[position])
            }
        }
    }

    override fun displayPackageError(error: String) {
        toast(error)
    }

    @Subscribe
    fun onNextBtnEvent(btn: ActionButtonClickEvent) {
        Timber.v("ActionButton Click Event" + btn.hashCode())
        Timber.d(serviceAdapter.getSelection().map { it.packageId }.toString())
        val appointmentPost = AppointmentPost()
        appointmentPost.packageIds = serviceAdapter.getSelection().map { it.packageId!! }
        if (SharedPrefHelper.isAppointmentServicePackageMandatory()) {
            if (serviceAdapter.getSelection().isNotEmpty()) {
            presenter.savePackages(appointmentId, appointmentPost)
            } else {
                toast("Please Select a Package")
            }
        } else {
            presenter.savePackages(appointmentId, appointmentPost)
        }
    }

    companion object {
        private const val ALL = "All"

        fun newInstance(): ServicePackageFragment {
            val fragment = ServicePackageFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
