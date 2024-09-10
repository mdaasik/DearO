package com.carworkz.dearo.cardslisting

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.customercarsearch.CustomerCarSearchActivity
import com.carworkz.dearo.amc.AMCSearchActivity
import com.carworkz.dearo.appointments.createappointment.CreateAppointmentActivity
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.events.CardCountUpdateEvent
import com.carworkz.dearo.events.CardStatusChangeEvent
import com.carworkz.dearo.otc.customer.OTCCustomerDetailsActivity
import com.google.android.material.tabs.TabLayout
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber

class DisplayCardListingFragment : BaseFragment(), EventsManager.EventSubscriber {

    private lateinit var cardType: String
    private var scrollToCard: String? = null

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var newActionBtn: LinearLayout
    private lateinit var actionIconView: ImageView
    private lateinit var newActionBtnTitle: TextView
    private val fragmentsList = ArrayList<Fragment>()
    private val tabTitleList = ArrayList<String>()
    private val requestCodeJobCard = 100
    private val requestCodeAppointment = 200
    private val tabTitleString = "%s(%d)"
    private var vehicleAmcId:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cardType = it.getString(ARG_CARD_TYPE)!!
            scrollToCard = it.getString(ARG_SCROLL_CARD_TYPE)
            vehicleAmcId = it.getString(ARG_VEHICLE_AMC_ID)

        }
        Timber.d("Mehul is testing display oncreate")
        EventsManager.register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_display_cards, container, false)
        tabLayout = view?.findViewById(R.id.tab_display_job_card) as TabLayout
        viewPager = view.findViewById(R.id.view_pager_display_job_cards) as ViewPager
        newActionBtn = view.findViewById(R.id.actionParentView)
        actionIconView = view.findViewById(R.id.actionIconView)
        newActionBtnTitle = view.findViewById(R.id.actionTitleView)
        when (cardType) {
            CARD_TYPE_JOBCARD -> initJobCardListing()
            CARD_TYPE_APPOINTMENT -> initAppointmentListing()
            CARD_TYPE_INVOICE -> initInvoiceListing()
            CARD_TYPE_OTC -> initOTCListing()
            CARD_TYPE_AMC -> initAMCListing()
        }
        viewPager.adapter = CardViewPagerAdapter(childFragmentManager)
        tabLayout.setupWithViewPager(viewPager)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            var prev = 0
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                (fragmentsList[prev] as OnPageVisibleCallback).onPageDetach()
                prev = position
                (fragmentsList[position] as OnPageVisibleCallback).isPageVisible()
            }
        })
        newActionBtn.setOnClickListener {
            when (cardType) {
                CARD_TYPE_JOBCARD -> startCreateJob()
                CARD_TYPE_APPOINTMENT -> startCreateAppointment()
                CARD_TYPE_OTC -> startOTCListing()
                CARD_TYPE_AMC -> startCreateAMC()
            }
        }

        viewPager.currentItem = 0
        Handler().postDelayed({ (fragmentsList[0] as CardListingFragment).isPageVisible() }, 100)
        Timber.d("Mehul is testing display onCreateView")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.postDelayed({
            scrollToCard?.let {
                when (it) {
                    CardListingFragment.LIST_TYPE_JOB_CARD_INITIATED -> {
                        viewPager.currentItem = 0
                    }

                    CardListingFragment.LIST_TYPE_JOB_CARD_IN_PROGRESS -> {
                        viewPager.currentItem = 1
                    }

                    CardListingFragment.LIST_TYPE_JOB_CARD_COMPLETED -> {
                        viewPager.currentItem = 2
                    }

                    CardListingFragment.LIST_TYPE_JOB_CARD_CLOSED -> {
                        viewPager.currentItem = 3
                    }
                }
            }
        }, 100)
        Timber.d("Mehul is testing display onViewCreated")
    }

    override fun onResume() {
        super.onResume()
        (fragmentsList[viewPager.currentItem] as CardListingFragment).isPageVisible()
        Timber.d("Mehul is testing display onResume")
    }

    override fun onPause() {
        super.onPause()
        (fragmentsList[viewPager.currentItem] as CardListingFragment).onPageDetach()
        Timber.d("Mehul is testing display onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("Mehul is testing display onDestroy")

        EventsManager.unregister(this)
    }

    @Subscribe
    fun onJobCardStatusChangeEvent(event: CardStatusChangeEvent) {
        // Reset Current Fragment Before Page Change\
        Timber.d("Mehul is testing display onJobCardStatusChangeEvent ${event.cardType}")
        when (cardType) {
            CARD_TYPE_JOBCARD -> {
                when (event.cardStatus) {
                    CardStatusChangeEvent.CARD_STATUS_JOB_CARD_IN_PROGRESS -> {
                        viewPager.setCurrentItem(IN_PROGRESS, true)
                        Timber.d("event blah ${event.cardType}")
                    }
                    CardStatusChangeEvent.CARD_STATUS_JOB_CARD_COMPLETED -> {
                        viewPager.setCurrentItem(COMPLETED, true)
                    }
                    CardStatusChangeEvent.CARD_STATUS_JOB_CARD_CLOSED -> {
                        viewPager.setCurrentItem(CLOSED, true)
                    }
                }
            }

            CARD_TYPE_INVOICE -> {
                when (event.cardStatus) {
                    CardStatusChangeEvent.CARD_STATUS_PROFORMA -> {
                        viewPager.setCurrentItem(PROFORMA, true)
                    }
                    CardStatusChangeEvent.CARD_STATUS_INVOICED -> {
                        viewPager.setCurrentItem(INVOICED, true)
                    }
                    CardStatusChangeEvent.CARD_STATUS_PAID -> {
                        viewPager.setCurrentItem(PAID, true)
                    }
                    CardStatusChangeEvent.CARD_STATUS_CANCELLED -> {
                        viewPager.setCurrentItem(CANCELLED, true)
                    }
                }
            }

            CARD_TYPE_APPOINTMENT -> {
                when (event.cardStatus) {
                    CardStatusChangeEvent.CARD_STATUS_APPOINTMENT_CANCELLED -> {
                        viewPager.setCurrentItem(CANCELLED, true)
                    }
                }
            }

            CARD_TYPE_OTC -> {
                when (event.cardStatus) {
                    CardStatusChangeEvent.CARD_STATUS_OTC_INVOICED -> {
                        viewPager.setCurrentItem(OTC_INVOICED, true)
                    }
                    CardStatusChangeEvent.CARD_STATUS_OTC_CANCELLED -> {
                        viewPager.setCurrentItem(OTC_CANCELLED, true)
                    }
                }
            }
        }
        // Reset Current Fragment After Page Change
    }

    @Subscribe
    fun updateTabTitle(event: CardCountUpdateEvent) {
        when (event.cardType) {
            CardCountUpdateEvent.CARD_STATUS_AMC_ACTIVE -> tabLayout.getTabAt(0)?.text = String.format(tabTitleString, getString(R.string.amc_active), event.cardCount)
            CardCountUpdateEvent.CARD_STATUS_AMC_EXPIRED -> tabLayout.getTabAt(1)?.text = String.format(tabTitleString, getString(R.string.amc_expired), event.cardCount)

            CardCountUpdateEvent.CARD_STATUS_JOB_CARD_INITAITED -> tabLayout.getTabAt(0)?.text = String.format(tabTitleString, getString(R.string.jc_initiated), event.cardCount)
            CardCountUpdateEvent.CARD_STATUS_JOB_CARD_IN_PROGRESS -> tabLayout.getTabAt(1)?.text = String.format(tabTitleString, getString(R.string.jc_in_progress), event.cardCount)
            CardCountUpdateEvent.CARD_STATUS_JOB_CARD_COMPLETED -> tabLayout.getTabAt(2)?.text = String.format(tabTitleString, getString(R.string.jc_completed), event.cardCount)
            CardCountUpdateEvent.CARD_STATUS_JOB_CARD_CLOSED -> tabLayout.getTabAt(3)?.text = String.format(tabTitleString, getString(R.string.jc_closed), event.cardCount)

            CardCountUpdateEvent.CARD_STATUS_PROFORMA -> tabLayout.getTabAt(0)?.text = String.format(tabTitleString, getString(R.string.invoice_proforma), event.cardCount)
            CardCountUpdateEvent.CARD_STATUS_INVOICED -> tabLayout.getTabAt(1)?.text = String.format(tabTitleString, getString(R.string.invoice_invoiced), event.cardCount)
            CardCountUpdateEvent.CARD_STATUS_PAID -> tabLayout.getTabAt(2)?.text = String.format(tabTitleString, getString(R.string.invoice_paid), event.cardCount)
            CardCountUpdateEvent.CARD_STATUS_INVOICE_CANCELLED -> tabLayout.getTabAt(3)?.text = String.format(tabTitleString, getString(R.string.invoice_cancelled), event.cardCount)

            CardCountUpdateEvent.CARD_STATUS_OTC_PROFORMA -> tabLayout.getTabAt(0)?.text = String.format(tabTitleString, getString(R.string.invoice_proforma), event.cardCount)
            CardCountUpdateEvent.CARD_STATUS_OTC_INVOICED -> tabLayout.getTabAt(1)?.text = String.format(tabTitleString, getString(R.string.invoice_invoiced), event.cardCount)
            CardCountUpdateEvent.CARD_STATUS_OTC_CANCELLED -> tabLayout.getTabAt(2)?.text = String.format(tabTitleString, getString(R.string.invoice_cancelled), event.cardCount)

            CardCountUpdateEvent.CARD_STATUS_APPOINTMENT_REQUESTED -> tabLayout.getTabAt(0)?.text = String.format(tabTitleString, getString(R.string.appmnt_requested), event.cardCount)
            CardCountUpdateEvent.CARD_STATUS_APPOINTMENT_PAST -> tabLayout.getTabAt(1)?.text = String.format(tabTitleString, getString(R.string.appmnt_past), event.cardCount)
            CardCountUpdateEvent.CARD_STATUS_APPOINTMENT_TODAY -> tabLayout.getTabAt(2)?.text = String.format(tabTitleString, getString(R.string.appmnt_today), event.cardCount)
            CardCountUpdateEvent.CARD_STATUS_APPOINTMENT_UPCOMING -> tabLayout.getTabAt(3)?.text = String.format(tabTitleString, getString(R.string.appmnt_upcoming), event.cardCount)
            CardCountUpdateEvent.CARD_STATUS_APPOINTMENT_CANCELLED -> tabLayout.getTabAt(4)?.text = String.format(tabTitleString, getString(R.string.appmnt_cancelled), event.cardCount)
        }
    }

    fun isFabVisible(): Boolean = newActionBtn.visibility == View.VISIBLE

    fun hideFab() {
        val hideAnimation = AnimationUtils.loadAnimation(activity, R.anim.anim_scale_down)
        hideAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) = Unit

            override fun onAnimationEnd(animation: Animation?) {
                newActionBtn.visibility = View.GONE
            }

            override fun onAnimationStart(animation: Animation?) = Unit
        })
        newActionBtn.startAnimation(hideAnimation)
    }

    fun showFab() {
        val showAnimation = AnimationUtils.loadAnimation(activity, R.anim.anim_scale_up)
        showAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) = Unit

            override fun onAnimationEnd(animation: Animation?) {
                newActionBtn.visibility = View.VISIBLE
            }

            override fun onAnimationStart(animation: Animation?) = Unit
        })
        newActionBtn.startAnimation(showAnimation)
    }

    private fun initJobCardListing() {
        fragmentsList.add(CardListingFragment.newInstance(CardListingFragment.LIST_TYPE_JOB_CARD_INITIATED,vehicleAmcId))
        fragmentsList.add(CardListingFragment.newInstance(CardListingFragment.LIST_TYPE_JOB_CARD_IN_PROGRESS,vehicleAmcId))
        fragmentsList.add(CardListingFragment.newInstance(CardListingFragment.LIST_TYPE_JOB_CARD_COMPLETED,vehicleAmcId))
        fragmentsList.add(CardListingFragment.newInstance(CardListingFragment.LIST_TYPE_JOB_CARD_CLOSED,vehicleAmcId))
        tabTitleList.add(getString(R.string.jc_initiated))
        tabTitleList.add(getString(R.string.jc_in_progress))
        tabTitleList.add(getString(R.string.jc_completed))
        tabTitleList.add(getString(R.string.jc_closed))
        newActionBtnTitle.text = getString(R.string.create_job_card)
        showFab()
    }

    private fun initInvoiceListing() {
        fragmentsList.add(CardListingFragment.newInstance(CardListingFragment.LIST_TYPE_INVOICE_PROFORMA,vehicleAmcId))
        fragmentsList.add(CardListingFragment.newInstance(CardListingFragment.LIST_TYPE_INVOICE_INVOICED,vehicleAmcId))
        fragmentsList.add(CardListingFragment.newInstance(CardListingFragment.LIST_TYPE_INVOICE_PAID,vehicleAmcId))
        fragmentsList.add(CardListingFragment.newInstance(CardListingFragment.LIST_TYPE_INVOICE_CANCELLED,vehicleAmcId))
        tabTitleList.add(getString(R.string.invoice_proforma))
        tabTitleList.add(getString(R.string.invoice_invoiced))
        tabTitleList.add(getString(R.string.invoice_paid))
        tabTitleList.add(getString(R.string.invoice_cancelled))
        hideFab()
    }

    private fun initAppointmentListing() {
        fragmentsList.add(CardListingFragment.newInstance(CardListingFragment.LIST_TYPE_APPOINTMENT_REQUESTED,vehicleAmcId))
        fragmentsList.add(CardListingFragment.newInstance(CardListingFragment.LIST_TYPE_APPOINTMENT_PAST,vehicleAmcId))
        fragmentsList.add(CardListingFragment.newInstance(CardListingFragment.LIST_TYPE_APPOINTMENT_TODAY,vehicleAmcId))
        fragmentsList.add(CardListingFragment.newInstance(CardListingFragment.LIST_TYPE_APPOINTMENT_UPCOMING,vehicleAmcId))
        fragmentsList.add(CardListingFragment.newInstance(CardListingFragment.LIST_TYPE_APPOINTMENT_CANCELLED,vehicleAmcId))
        tabTitleList.add(getString(R.string.appmnt_requested))
        tabTitleList.add(getString(R.string.appmnt_past))
        tabTitleList.add(getString(R.string.appmnt_today))
        tabTitleList.add(getString(R.string.appmnt_upcoming))
        tabTitleList.add(getString(R.string.appmnt_cancelled))


        if (SharedPrefHelper.getClusterId().equals("1630665385893245")){

        //   println("-----clusterid appointment-----"+SharedPrefHelper.getClusterId())

            newActionBtn.visibility = View.GONE
            newActionBtn.setBackgroundColor(Color.TRANSPARENT)
            actionIconView.visibility = View.GONE
            newActionBtnTitle.visibility = View.GONE

        }
        else{
          //  println("-----clusterid appointment else-----"+SharedPrefHelper.getClusterId())

            newActionBtnTitle.text = getString(R.string.create_appointment)
            showFab()
        }



    }

    private fun initOTCListing() {
        fragmentsList.add(CardListingFragment.newInstance(CardListingFragment.LIST_TYPE_OTC_PROFORMA,vehicleAmcId))
        fragmentsList.add(CardListingFragment.newInstance(CardListingFragment.LIST_TYPE_OTC_INVOICED,vehicleAmcId))
        //  fragmentsList.add(CardListingFragment.newInstance(CardListingFragment.LIST_TYPE_OTC_CANCELLED))
        tabTitleList.add(getString(R.string.otc_proforma))
        tabTitleList.add(getString(R.string.otc_invoiced))
        newActionBtnTitle.text = getString(R.string.create_otc)
        showFab()
        // tabTitleList.add(getString(R.string.otc_cancelled))
    }

    private fun initAMCListing() {
        fragmentsList.add(CardListingFragment.newInstance(CardListingFragment.LIST_TYPE_AMC_ACTIVE,vehicleAmcId))
        fragmentsList.add(CardListingFragment.newInstance(CardListingFragment.LIST_TYPE_AMC_EXPIRED,vehicleAmcId))
        tabTitleList.add(getString(R.string.amc_active))
        tabTitleList.add(getString(R.string.amc_expired))
        newActionBtnTitle.text = getString(R.string.create_amc)
        showFab()
    }

    private fun startCreateJob() {
        val intent = Intent(context, CustomerCarSearchActivity::class.java)
        startActivityForResult(intent, requestCodeJobCard)
    }

    private fun startCreateAMC() {
        val intent = Intent(context, AMCSearchActivity::class.java)
        startActivityForResult(intent, requestCodeJobCard)
    }

    private fun startOTCListing() {
        startActivity(OTCCustomerDetailsActivity.getIntent(requireContext()))
    }

    private fun startCreateAppointment() {
        startActivityForResult(CreateAppointmentActivity.getIntent(requireContext()), requestCodeAppointment)
    }

    companion object {
        const val IN_PROGRESS = 1
        const val COMPLETED = 2
        const val CLOSED = 3
        const val PROFORMA = 0
        const val INVOICED = 1
        const val OTC_INVOICED = 1
        const val PAID = 2
        const val CANCELLED = 3
        const val OTC_CANCELLED = 3
        const val CARD_TYPE_AMC = "amcCard"
        const val CARD_TYPE_JOBCARD = "jobcard"
        const val CARD_TYPE_INVOICE = "invoice"
        const val CARD_TYPE_APPOINTMENT = "appointment"
        const val CARD_TYPE_OTC = "ARG_TYPE_OTC"
        private const val ARG_CARD_TYPE = "arg_card_type"
        private const val ARG_SCROLL_CARD_TYPE = "arg_card_scroll_type"
        private const val ARG_VEHICLE_AMC_ID = "vehicle-amc-id"

        @JvmStatic
        fun newInstance(cardType: String,amcId:String?) =
                DisplayCardListingFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_CARD_TYPE, cardType)
                        putString(ARG_VEHICLE_AMC_ID, amcId)
                    }
                }

        @JvmStatic
        fun newInstance(cardType: String, scrollToCard: String?,amcId:String?) =
                DisplayCardListingFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_CARD_TYPE, cardType)
                        putString(ARG_SCROLL_CARD_TYPE, scrollToCard)
                        putString(ARG_VEHICLE_AMC_ID, amcId)
                    }
                }
    }

    inner class CardViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return fragmentsList[position]
        }

        override fun getCount(): Int {
            return fragmentsList.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return tabTitleList[position]
        }
    }

    // localMethod
}
