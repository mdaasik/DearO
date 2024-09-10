package com.carworkz.dearo

//import kotlinx.android.synthetic.main.activity_main.*
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.cardslisting.DisplayCardListingFragment
import com.carworkz.dearo.contactus.ContactUsActivity
import com.carworkz.dearo.cronjobs.JobHelper
import com.carworkz.dearo.dashboard.DashboardFragment
import com.carworkz.dearo.data.local.SharedPrefHelper
import com.carworkz.dearo.databinding.ActivityMainBinding
import com.carworkz.dearo.databinding.BaseDrawerLayoutBinding
import com.carworkz.dearo.events.ToggleMenuItemsEvent
import com.carworkz.dearo.extensions.startActivity
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider
import com.carworkz.dearo.login.WebPageActivity
import com.carworkz.dearo.login.addmobilenumber.LoginActivity
import com.carworkz.dearo.mycustomers.mycustomerlisting.CustomerListingFragment
import com.carworkz.dearo.screencontainer.NavigationDrawerScreenContainer
import com.carworkz.dearo.search.SearchActivity
import com.carworkz.dearo.splashscreen.ConfigContract
import com.carworkz.dearo.splashscreen.ConfigPresenter
import com.carworkz.dearo.splashscreen.ConfigPresenterModule
import com.carworkz.dearo.utils.Constants
import com.carworkz.dearo.utils.Constants.ApiConstants
import com.carworkz.dearo.utils.Utility
import com.carworkz.dearo.vehiclequery.VehicleQueryActivity
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.installations.FirebaseInstallations
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

private const val UPDATE_REQUEST_CODE_IMMEDIATE = 122
private const val UPDATE_REQUEST_CODE_FLEXIBLE = 123

class MainActivity : ScreenContainerActivity(), ToolBarImgInteractionProvider,
    EventsManager.EventSubscriber, HomeInteraction, ConfigContract.View {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private var pDialog: ProgressBar? = null
    private lateinit var userNameView: TextView
    private lateinit var profileImageView: ImageView
    private lateinit var workShopNameView: TextView
    private lateinit var versionView: TextView
    var search: ImageView? = null
    private lateinit var currentFragment: Fragment
    private lateinit var screenContainer: NavigationDrawerScreenContainer
    private var isFrom = SearchActivity.ARG_JOB_CARD_SEARCH

    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

    private var back_pressed: Long = 0

    @Inject
    lateinit var presenter: ConfigPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        initComponent()
        super.onCreate(savedInstanceState)
        pDialog = ProgressBar(this)
       // mainContainer = findViewById(R.id.main_container)
        //setup Navigation
        setupNavigation()

        if (!Utility.isJobRunning(this, JobHelper.JOB_ID_FILE_UPLOAD)) {
            JobHelper.scheduleJob(this, JobHelper.JOB_FILE_UPLOAD)
        }

        Utility.isJobSchedulerRunning(this)
        if (SharedPrefHelper.getForceUpdate() && SharedPrefHelper.getMinimumVersion() > BuildConfig.VERSION_CODE) {
            displayForceUpdateDialog()
            return
        }

        //Set default screen
//		inflateFragment(-1)
//        startDefaultFragment()
        decideDefaultFragmentAndInflate()

        if (SharedPrefHelper.isFcmSynced().not()) {
            FirebaseInstallations.getInstance().getToken(false)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        SharedPrefHelper.setDeviceId(UUID.randomUUID().toString())
                        SharedPrefHelper.setFcmToken(it.result.token)
                        if (!Utility.isJobRunning(this, JobHelper.JOB_ID_DEVICE_REGISTRAR)) {
                            JobHelper.scheduleJob(this, JobHelper.JOB_DEVICE_REGISTRAR)
                        }
                    } else {
                        Timber.d("fetching firebaseId failed ${it.exception}")
                    }
                }
        }

        //IN-APP UPDATE
        appUpdateManager.registerListener {
            if (it.installStatus() == InstallStatus.DOWNLOADED) {
                showUpdateDownloadedSnackbar()
            }
        }

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.updatePriority() >= 4 /* high priority */
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Request an immediate update.
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    UPDATE_REQUEST_CODE_IMMEDIATE
                )
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    this,
                    UPDATE_REQUEST_CODE_FLEXIBLE
                )
            }
        }.addOnFailureListener {
            Log.e("FlexibleUpdateActivity", "Failed to check for update: $it")
        }
    }

    /**
     * @author Mehul Kadam
     * createdOn 26/10/21
     */
    private fun setupNavigation() {
        navigationView = findViewById(R.id.navigation_view)
        userNameView = navigationView.getHeaderView(0).findViewById<TextView>(R.id.userName)!!
        profileImageView = navigationView.getHeaderView(0).findViewById(R.id.iv_profile_image)
        if (SharedPrefHelper.isBrandingEnabled()) {
            Timber.d("outside gide")
            profileImageView.postDelayed({
                Timber.d("inside gide")
                Glide
                    .with(this)
                    .load(
                        if (SharedPrefHelper.getBrandingClusterLogo()
                                .isNullOrEmpty()
                        ) SharedPrefHelper.getBrandingLogo()
                        else SharedPrefHelper.getBrandingClusterLogo()
                    )
                    .apply(
                        RequestOptions()
                            .error(R.drawable.ic_dearo_logo_white)
                    )
                    .into(profileImageView)
            }, 500)
        }

        workShopNameView = navigationView.getHeaderView(0).findViewById(R.id.workShopName)
        versionView = navigationView.getHeaderView(0).findViewById(R.id.tv_version)
        // TODO: check for debug and add role
        userNameView.text = SharedPrefHelper.getUserName()
        workShopNameView.text = SharedPrefHelper.getWorkShopName()
        versionView.text = BuildConfig.VERSION_NAME
        drawerLayout = findViewById(R.id.main_drawer_layout)


        //###### MENU VISIBILITY AS PER WORKSHOP CONFIG
        //Appointments
        navigationView.menu.findItem(R.id.nav_appointments).isEnabled =
            SharedPrefHelper.isAppointmentActive()
        navigationView.menu.findItem(R.id.nav_appointments).isVisible =
            SharedPrefHelper.isAppointmentActive()
        //PART FINDER
        navigationView.menu.findItem(R.id.nav_part_finder).isEnabled =
            SharedPrefHelper.isPartFinderEnabled()
        navigationView.menu.findItem(R.id.nav_part_finder).isVisible =
            SharedPrefHelper.isPartFinderEnabled()

        //OTC
        navigationView.menu.findItem(R.id.nav_otc).isEnabled = SharedPrefHelper.isOtcEnabled()
        navigationView.menu.findItem(R.id.nav_otc).isVisible = SharedPrefHelper.isOtcEnabled()

        //AMC
        navigationView.menu.findItem(R.id.nav_amc).isEnabled = SharedPrefHelper.isAmcEnabled()
        navigationView.menu.findItem(R.id.nav_amc).isVisible = SharedPrefHelper.isAmcEnabled()


        //###### MENU VISIBILITY AS PER ROUTES
        val routes = SharedPrefHelper.getRoutes()

        //#PRIMARY MENU
        //Home
        navigationView.menu.findItem(R.id.nav_home).isEnabled =
            routes.contains(Constants.AppMenuConstant.MENU_HOME)
        navigationView.menu.findItem(R.id.nav_home).isVisible =
            routes.contains(Constants.AppMenuConstant.MENU_HOME)

        //Job Cards
        navigationView.menu.findItem(R.id.nav_job_card).isEnabled =
            routes.contains(Constants.AppMenuConstant.MENU_JOB_CARDS)
        navigationView.menu.findItem(R.id.nav_job_card).isVisible =
            routes.contains(Constants.AppMenuConstant.MENU_JOB_CARDS)

        //Repair Bills
        navigationView.menu.findItem(R.id.nav_repair_bills).isEnabled =
            routes.contains(Constants.AppMenuConstant.MENU_REPAIR_BILLS)
        navigationView.menu.findItem(R.id.nav_repair_bills).isVisible =
            routes.contains(Constants.AppMenuConstant.MENU_REPAIR_BILLS)

        //Appointments
        navigationView.menu.findItem(R.id.nav_appointments).isEnabled =
            routes.contains(Constants.AppMenuConstant.MENU_APPOINTMENTS)
        navigationView.menu.findItem(R.id.nav_appointments).isVisible =
            routes.contains(Constants.AppMenuConstant.MENU_APPOINTMENTS)

        //AMC
        navigationView.menu.findItem(R.id.nav_amc).isEnabled =
            routes.contains(Constants.AppMenuConstant.MENU_AMC)
        navigationView.menu.findItem(R.id.nav_amc).isVisible =
            routes.contains(Constants.AppMenuConstant.MENU_AMC)

        //Counter Sales
        navigationView.menu.findItem(R.id.nav_otc).isEnabled =
            routes.contains(Constants.AppMenuConstant.MENU_COUNTER_SALE)
        navigationView.menu.findItem(R.id.nav_otc).isVisible =
            routes.contains(Constants.AppMenuConstant.MENU_COUNTER_SALE)

        //My Customers
        navigationView.menu.findItem(R.id.nav_my_customers).isEnabled =
            routes.contains(Constants.AppMenuConstant.MENU_MY_CUSTOMERS)
        navigationView.menu.findItem(R.id.nav_my_customers).isVisible =
            routes.contains(Constants.AppMenuConstant.MENU_MY_CUSTOMERS)

        //Part Finder(Beta)
        navigationView.menu.findItem(R.id.nav_part_finder).isEnabled =
            routes.contains(Constants.AppMenuConstant.MENU_PART_FINDER_BETA)
        navigationView.menu.findItem(R.id.nav_part_finder).isVisible =
            routes.contains(Constants.AppMenuConstant.MENU_PART_FINDER_BETA)

        //Spares
        navigationView.menu.findItem(R.id.nav_spares).isEnabled =
            routes.contains(Constants.AppMenuConstant.MENU_SPARES)
        navigationView.menu.findItem(R.id.nav_spares).isVisible =
            routes.contains(Constants.AppMenuConstant.MENU_SPARES)

        val actionbarDrawerToggle =
            object : ActionBarDrawerToggle(
                this,
                drawerLayout,
                screenContainer.toolbar,
                R.string.drawer_open,
                R.string.drawer_close
            ) {
            }
        drawerLayout.addDrawerListener(actionbarDrawerToggle)
        actionbarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener { item ->
            drawerLayout.closeDrawers()
            Log.d("NavigationDebug", "Item clicked: " + item.getItemId());
            inflateFragment(item.itemId)
            true
        }
    }

    override fun onResume() {
        super.onResume()
        EventsManager.register(this)

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                // If the update is downloaded but not installed,
                // notify the user to complete the update.
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED)
                    showUpdateDownloadedSnackbar()
            } else {
                // for AppUpdateType.IMMEDIATE only
                // already executing updater
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        UPDATE_REQUEST_CODE_IMMEDIATE
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_REQUEST_CODE_IMMEDIATE && resultCode == Activity.RESULT_CANCELED) {
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        EventsManager.unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.clearHsn()
        presenter.clearCustomerGroup()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            when (isFrom) {
                ARG_HOME -> finishThisScreen()
                ARG_JOB_CARD_SEARCH -> {
                    //here we have to check if default screen is Job card listing
                    if (SharedPrefHelper.getRoutes()[0].equals(Constants.AppMenuConstant.MENU_JOB_CARDS)) {
                        finishThisScreen()
                    } else {
                        decideDefaultFragmentAndInflate()
                    }
                }

                else -> {
                    decideDefaultFragmentAndInflate()
                }
            }
        }
    }

    private fun finishThisScreen() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            this.finishAffinity()
        } else {
            toast("Press once again to exit")
            back_pressed = System.currentTimeMillis()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("NavigationDebug", "Item clicked: " + item.getItemId());
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }

    override fun getProgressView(): View? {
        return null
    }

    override fun onActionBtnClick() {
        if (isFrom != SearchActivity.ARG_APPOINTMENT_SEARCH && isFrom != SearchActivity.ARG_OTC_SEARCH) // Search for these screens isn't Implemented
        {
            var intent = Intent(this, SearchActivity::class.java)
            intent.putExtra(SearchActivity.ARG_TYPE, isFrom)
            startActivityForResult(intent, PAGINATION_REQUEST_CODE)
        } else
            toast("Coming Soon")
    }

    override fun getActionBarImage(): Int {
        return when (isFrom) {
            ARG_HOME, ARG_APPOINTMENT_SEARCH -> 0
            else -> R.drawable.ic_search_black_24dp
        }
    }

    override fun getNavigationImage(): Int {
        return 0
    }

    override fun onSecondaryActionBtnClick() {
    }

    override fun getSecondaryActionBarImage(): Int {
        return 0
    }

    override fun getToolBarTitle(): String {
        return when (isFrom) {
            ARG_HOME -> getString(R.string.navigation_menu_home)
            ARG_JOB_CARD_SEARCH -> getString(R.string.navigation_menu_job_card)
            ARG_INVOICE_SEARCH -> getString(R.string.navigation_menu_repair_bills)
            ARG_APPOINTMENT_SEARCH -> getString(R.string.navigation_menu_appointment)
            ARG_OTC_SEARCH -> getString(R.string.navigation_menu_otc_sales)
            ARG_CUSTOMER_SEARCH -> getString(R.string.navigation_menu_my_customers)
            ARG_AMC_SEARCH -> getString(R.string.navigation_menu_amc)
            else -> getString(R.string.navigation_menu_job_card)
        }
    }

    override fun createScreenContainer(): ScreenContainer {
        screenContainer = NavigationDrawerScreenContainer(this)
        return screenContainer
    }

    override fun getViewBinding(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        attachToParent: Boolean
    ): ViewBinding {
        // Inflate the base drawer layout
       // bindingBase = BaseDrawerLayoutBinding.inflate(inflater!!)
        // Inflate the main activity binding, attaching it to the base drawer layout
        // Do not attach the root if it's already part of another parent
        binding = ActivityMainBinding.inflate(layoutInflater)
        return binding
    }


    override fun switchToJobCard(jobCardState: String) {
        startJobCardListing(jobCardState)
    }


    override fun moveToNextScreen() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    override fun showProgressIndicator() = Unit

    override fun dismissProgressIndicator() = Unit

    override fun showGenericError(errorMsg: String) = Unit

    @Subscribe
    fun toggleMenuItems(toggle: ToggleMenuItemsEvent) {
        // TODO: 26/10/21 check this menu as per role
        Timber.v("Toggle Navigation Menu Items" + toggle.hashCode())
        navigationView.menu.findItem(R.id.nav_appointments).isEnabled =
            SharedPrefHelper.isAppointmentActive()
        navigationView.menu.findItem(R.id.nav_part_finder).isEnabled =
            SharedPrefHelper.isPartFinderEnabled()
        navigationView.menu.findItem(R.id.nav_otc).isEnabled = SharedPrefHelper.isOtcEnabled()
        navigationView.menu.findItem(R.id.nav_amc).isEnabled = SharedPrefHelper.isAmcEnabled()
    }

    private fun decideDefaultFragmentAndInflate() {
        val routes = SharedPrefHelper.getRoutes()

        //if routes are null or empty logout user
        if (routes.isEmpty()) {
            presenter.logout()
        } else {
            when (routes[0]) {
                Constants.AppMenuConstant.MENU_HOME -> {
                    inflateFragment(R.id.nav_home)
                }

                Constants.AppMenuConstant.MENU_JOB_CARDS -> {
                    startJobCardListing(null)
                }

                Constants.AppMenuConstant.MENU_REPAIR_BILLS -> {
                    inflateFragment(R.id.nav_repair_bills)
                }

                Constants.AppMenuConstant.MENU_APPOINTMENTS -> {
                    inflateFragment(R.id.nav_appointments)
                }

                Constants.AppMenuConstant.MENU_AMC -> {
                    startAMCCardListing()
                }

                Constants.AppMenuConstant.MENU_COUNTER_SALE -> {
                    inflateFragment(R.id.nav_otc)
                }

                Constants.AppMenuConstant.MENU_MY_CUSTOMERS -> {
                    inflateFragment(R.id.nav_my_customers)
                }

                Constants.AppMenuConstant.MENU_PART_FINDER_BETA -> {
                    inflateFragment(R.id.nav_part_finder)
                }

                else -> {
                    inflateFragment(R.id.nav_home)
                }
            }
        }
    }

    private fun inflateFragment(menuId: Int) {
        when (menuId) {
            R.id.nav_home -> {
                isFrom = ARG_HOME
                currentFragment = DashboardFragment.newInstance()
                screenContainer.refreshToolBar()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, currentFragment).commit()
            }

            R.id.nav_job_card -> {
                startJobCardListing(null)
            }

            R.id.nav_amc -> {
                startAMCCardListing()
            }

            R.id.nav_repair_bills -> {
                currentFragment = DisplayCardListingFragment.newInstance(
                    DisplayCardListingFragment.CARD_TYPE_INVOICE,
                    null
                )
                isFrom = ARG_INVOICE_SEARCH
                screenContainer.refreshToolBar()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, currentFragment).commit()
            }

            R.id.nav_appointments -> {
                currentFragment = DisplayCardListingFragment.newInstance(
                    DisplayCardListingFragment.CARD_TYPE_APPOINTMENT,
                    null
                )
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, currentFragment).commit()
                isFrom = ARG_APPOINTMENT_SEARCH
                screenContainer.refreshToolBar()
            }

            R.id.nav_otc -> {
                currentFragment = DisplayCardListingFragment.newInstance(
                    DisplayCardListingFragment.CARD_TYPE_OTC,
                    null
                )
                isFrom = ARG_OTC_SEARCH
                screenContainer.refreshToolBar()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, currentFragment).commit()
            }

            R.id.nav_spares -> {
                try {
                    intent = Intent(Intent.ACTION_VIEW, Uri.parse(ApiConstants.BASE_URL_WEB))
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    LoggingFacade.log(e)
                }
            }

            R.id.nav_part_finder -> {
                startActivity<VehicleQueryActivity>()
            }

            R.id.nav_tc -> {
                val intent = Intent(applicationContext, WebPageActivity::class.java)
                intent.putExtra(WebPageActivity.TYPE, WebPageActivity.TERMS_AND_CONDITION)
                startActivity(intent)
            }

            R.id.pp -> {
                val intent = Intent(applicationContext, WebPageActivity::class.java)
                intent.putExtra(WebPageActivity.TYPE, WebPageActivity.PRIVACY_POLICY)
                startActivity(intent)
            }

            R.id.nav_my_customers -> {
                currentFragment = CustomerListingFragment.newInstance()
                isFrom = ARG_CUSTOMER_SEARCH
                screenContainer.refreshToolBar()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, currentFragment).commit()
            }

            R.id.nav_contact_us -> {
                startActivity(ContactUsActivity.getIntent(this))
            }

            R.id.nav_logout -> {
                presenter.logout()
            }

            else -> {
                screenContainer.toolbar.title = getString(R.string.jobCard_title)
                startJobCardListing(null)
            }
        }
    }

    private fun startJobCardListing(jobCardType: String?) {
        currentFragment = DisplayCardListingFragment.newInstance(
            DisplayCardListingFragment.CARD_TYPE_JOBCARD,
            jobCardType,
            null
        )
        isFrom = ARG_JOB_CARD_SEARCH
        screenContainer.refreshToolBar()
        supportFragmentManager.beginTransaction().replace(R.id.main_container, currentFragment)
            .commit()
    }

    private fun startAMCCardListing() {
        currentFragment =
            DisplayCardListingFragment.newInstance(DisplayCardListingFragment.CARD_TYPE_AMC, null)
        isFrom = ARG_AMC_SEARCH
        screenContainer.refreshToolBar()
        supportFragmentManager.beginTransaction().replace(R.id.main_container, currentFragment)
            .commit()
    }

    private fun displayForceUpdateDialog() {
        val alert = AlertDialog.Builder(this)
        alert.setMessage(SharedPrefHelper.getForceUpdateText())
        alert.setTitle("Force Update")
        alert.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
            val appPackageName =
                "com.carworkz.dearo" // getPackageName() from Context or Activity object
            try {
                if (SharedPrefHelper.getForceUpdateLink().isEmpty()) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$appPackageName")
                        )
                    )
                } else {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(SharedPrefHelper.getForceUpdateLink())
                        )
                    )
                }
            } catch (exception: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
        }

        alert.setNegativeButton("No") { _: DialogInterface, _: Int ->
            finish()
        }

        alert.show()
    }

    private fun showUpdateDownloadedSnackbar() {
        Snackbar.make(binding.mainContainer, "Update downloaded!", Snackbar.LENGTH_INDEFINITE)
            .setAction("Install") { appUpdateManager.completeUpdate() }
            .show()
    }

    private fun initComponent() {
        (application as DearOApplication)
            .repositoryComponent
            .COMPONENT(ConfigPresenterModule(this))
            .inject(this)
    }


    companion object {
        const val PAGINATION_REQUEST_CODE = 100
        const val ARG_JOB_CARD_SEARCH = "ARG_JOB_CARD_SEARCH"
        const val ARG_HOME = "ARG_HOME"
        const val ARG_INVOICE_SEARCH = "ARG_INVOICE_SEARCH"
        const val ARG_APPOINTMENT_SEARCH = "ARG_APPOINTMENT_SEARCH"
        const val ARG_OTC_SEARCH = "ARG_OTC_SEARCH"
        const val ARG_CUSTOMER_SEARCH = "ARG_CUSTOMER_SEARCH"
        const val ARG_AMC_SEARCH = "ARG_AMC_SEARCH"
    }
}
