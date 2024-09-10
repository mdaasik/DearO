package com.carworkz.dearo.carpm

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.R
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityScanCarPmBinding
import com.carworkz.dearo.domain.entities.CarpmScanReport
import com.carworkz.dearo.domain.entities.CodeDetail
import com.carworkz.dearo.domain.entities.ScannedCarpmRequest
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider
import com.carworkz.dearo.networkAPI.ApiClient
import com.carworkz.dearo.networkAPI.ApiRequest
import com.carworkz.dearo.screencontainer.ActionImgScreenContainer
import com.zymbia.garageprosdk.GProAuthentication
import com.zymbia.garageprosdk.GProAuthenticationListener
import com.zymbia.garageprosdk.GProScanListener
import com.zymbia.garageprosdk.GProScanProcess
import com.zymbia.garageprosdk.utils.GlobalStaticKeys
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CarPmAuthActivity : ScreenContainerActivity(), ToolBarImgInteractionProvider,
    GProAuthenticationListener, GProScanListener {
    private lateinit var binding: ActivityScanCarPmBinding
    val context: Context = this@CarPmAuthActivity
    val uniqueIdentifier: String = "vignesh.selva@tvs.in"
    //  val uniqueIdentifier: String = "imvky18@gmail.com"

    //  val uniqueIdentifier: String = "b.jegadeeswaran@tvs.in"
    val garageId: String = "null"
    val userId: String = "null"
    private lateinit var progressDialog: ProgressDialog

    val passToken: String =
        "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjo5MTIwMzh9.k9iDx1S9ASvmhDYcADv-VSP4ErDw_tFZprp-ddKtC8k"
    private lateinit var gProAuthentication: GProAuthentication

    private lateinit var mBtResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var mSettingsResultLauncher: ActivityResultLauncher<Intent>
    private var mKeyType: String? = null
    private lateinit var gProScanProcess: GProScanProcess

    private lateinit var makeName: String
    private lateinit var registrationNumber: String
    private lateinit var jobCardId: String

    private lateinit var scanId: String
    private var isScanned: Boolean = false

    var restApi: ApiRequest? = null
    lateinit var scanListAdapter: ScanListAdapter
    var codeList = listOf<CodeDetail>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        makeName = intent?.extras?.getString(ARG_MAKE_NAME)!!
        registrationNumber = intent?.extras?.getString(ARG_REG_NO)!!
        jobCardId = intent?.extras?.getString(ARG_JOB_ID)!!
        scanId = intent?.extras?.getString(ARG_SCAN_ID)!!
        isScanned = intent?.extras?.getBoolean(ARG_IS_CARPM_SCANNED)!!

        if (makeName.isEmpty() || makeName.equals("")) {
            makeName = "Hyundai"
        }

        restApi = ApiClient.getAPIClient()?.create(ApiRequest::class.java)

        Log.d(
            "Cheeeeeekkkkk",
            makeName + "---" + registrationNumber + "---" + jobCardId + "---" + scanId + "---" + isScanned
        )

        if (isScanned) {
            binding.txtHaveScanner.visibility = View.GONE
            binding.txtContent.visibility = View.GONE

            binding.rvScanResults.visibility = View.VISIBLE
            binding.pbMain.visibility = View.VISIBLE
            // getScanList("4693479")
            getScanList(scanId)
        } else {
            binding.txtHaveScanner.visibility = View.VISIBLE
            binding.txtContent.visibility = View.VISIBLE

            binding.rvScanResults.visibility = View.GONE
            binding.pbMain.visibility = View.VISIBLE
            mKeyType = GlobalStaticKeys.KEY_SCAN
            initializeActivityLaunchers()
            gProAuthentication =
                GProAuthentication(context, uniqueIdentifier, garageId, userId, passToken)
            gProAuthentication.init()
            gProScanProcess = GProScanProcess(context)
        }

        binding.txtHaveScanner.setOnClickListener {
            //    val makeName: String = "Hyundai"
            //    val registrationNumber: String = "TN09BE6565"
            val jobCardId: String = "1276352"

            /*  Log.d(
                  "Cheeeeeekkkkk",
                  makeName + "---" + registrationNumber + "---" + jobCardId + "---" + scanId + "---" + isScanned
              )*/
            //  gProScanProcess.initScan(makeName, registrationNumber, jobCardId.substring(0,7))
            gProScanProcess.initScan(makeName, registrationNumber, jobCardId)
        }
    }

    private fun getScanList(scanId: String) {
        // val request = ScannedCarpmRequest(scanId, "1276352")
        val request = ScannedCarpmRequest(scanId, jobCardId)
        val call = restApi!!.getScannedData(request)
        call.enqueue(object : Callback<CarpmScanReport> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<CarpmScanReport>, response: Response<CarpmScanReport>
            ) {
                if (response.isSuccessful) {
                    binding.pbMain.visibility = View.GONE
                    val responseBody = response.body()
                    if (responseBody != null) {
                        codeList = responseBody.codeDetails
                        if (!codeList.isEmpty()) {
                            scanListAdapter = ScanListAdapter(this@CarPmAuthActivity, codeList)
                            binding.rvScanResults.layoutManager =
                                LinearLayoutManager(this@CarPmAuthActivity)
                            binding.rvScanResults.adapter = scanListAdapter
                            scanListAdapter.notifyDataSetChanged()
                        } else {
                            binding.txtHaveScanner.visibility = View.GONE
                            binding.txtContent.visibility = View.VISIBLE

                            binding.rvScanResults.visibility = View.GONE

                            binding.txtContent.text = "No Data Found"
                        }
                    } else {
                        binding.txtHaveScanner.visibility = View.GONE
                        binding.txtContent.visibility = View.VISIBLE

                        binding.rvScanResults.visibility = View.GONE
                        binding.txtContent.text = "No Data Found"
                    }
                } else {
                    binding.pbMain.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<CarpmScanReport>, t: Throwable) {
                binding.pbMain.visibility = View.GONE
            }
        })
    }


    override fun getProgressView(): View {
        TODO("Not yet implemented")
    }

    override fun createScreenContainer(): ScreenContainer = ActionImgScreenContainer(this)

    override fun getViewBinding(
        inflater: LayoutInflater?, container: ViewGroup?, attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityScanCarPmBinding.inflate(layoutInflater)
        return binding
    }

    companion object {
        const val ARG_REG_NO = "ARG_REG_NO"
        const val ARG_JOB_ID = "ARG_JOB_CARD_ID"
        const val ARG_MAKE_NAME = "ARG_MAKE_NAME"
        const val ARG_SCAN_ID = "ARG_SCAN_ID"
        const val ARG_IS_CARPM_SCANNED = "ARG_SCANNED"

        fun getIntent(
            context: Context,
            Id: String?,
            regNo: String?,
            makeName: String?,
            scanID: String?,
            isScanned: Boolean?
        ): Intent {
            return Intent(context, CarPmAuthActivity::class.java).putExtra(ARG_JOB_ID, Id)
                .putExtra(ARG_REG_NO, regNo).putExtra(ARG_MAKE_NAME, makeName)
                .putExtra(ARG_SCAN_ID, scanID).putExtra(ARG_IS_CARPM_SCANNED, isScanned)
        }
    }

    override fun getNavigationImage(): Int = R.drawable.ic_arrow_back_white_24dp

    override fun onActionBtnClick() = Unit

    override fun onSecondaryActionBtnClick() {
    }

    override fun getActionBarImage(): Int = 0

    override fun getSecondaryActionBarImage(): Int = 0
    override fun getToolBarTitle(): String = "Scan Carpm Device"

    override fun onAuthenticationSuccess(p0: String?) {
        binding.pbMain.visibility = View.GONE
        Log.d("Cheeeeeekkkkk", "Authentication success: " + p0)/*   val intent = Intent(this, ScanningCarPmActivity::class.java)
        startActivity(intent)*/
        toast("Authenticate Successfully, You Can Proceed Scanning Process Now")
        mKeyType = GlobalStaticKeys.KEY_SCAN
        gProScanProcess = GProScanProcess(context)
        //  initializeActivityLaunchers()
    }

    override fun onAuthenticationFailure(p0: String?) {
        Log.d("Cheeeeeekkkkk", "Authentication Failure: " + p0)
        binding.pbMain.visibility = View.GONE
        toast("Authenticate Failed, Try Again!")
        finish()
        // gProAuthentication.init()
    }

    private fun initializeActivityLaunchers() {
        mBtResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("Cheeeeeekkkkk", "Bluetooth Enabled!")
                gProScanProcess.checkPermissions()
            } else {
                Log.d("Cheeeeeekkkkk", "Bluetooth NOT Enabled!")
                mKeyType?.let { onPermissionFailure(it, "Your bluetooth is not ENABLED!") }
            }
        }

        mSettingsResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("Cheeeeeekkkkk", "Permission given!")
                gProScanProcess.checkPermissions()
            } else {
                Log.d("Cheeeeeekkkkk", "Permission denied!")
                mKeyType?.let {
                    onPermissionFailure(
                        it,
                        "Near by devices, bluetooth and location permissions needed for searching and connecting with OBDII scanner."
                    )
                }
            }
        }
    }

    override fun onScanAuthenticationFailure(p0: String?, p1: String?) {
        Log.d("Cheeeeeekkkkk", "onScanAuthenticationFailure" + p0 + "--" + p1)
    }

    override fun onPermissionRequest(p0: String?, p1: Int) {
        Log.d("Cheeeeeekkkkk", "onPermissionRequest" + p0 + "--" + p1)
    }

    override fun onPermissionFailure(p0: String?, p1: String?) {
        Log.d("Cheeeeeekkkkk", "onPermissionFailure" + p0 + "--" + p1)
    }

    override fun onScannerConnectionCompleted(p0: String?, p1: String?) {
        Log.d("Cheeeeeekkkkk", "onScannerConnectionCompleted" + p0 + "--" + p1)
    }

    override fun onScannerConnectionFailed(p0: String?, p1: String?) {
        Log.d("Cheeeeeekkkkk", "onScannerConnectionFailed" + p0 + "--" + p1)
    }

    override fun onScanCompleted(p0: String?, p1: String?, p2: String?, p3: String?, p4: Int) {
        Log.d(
            "Cheeeeeekkkkk",
            "onScanCompleted" + p0 + "--" + p1 + "---" + p2 + "---" + p3 + "----" + p4
        )

        runOnUiThread {
            playBeepSound()
            Handler(Looper.getMainLooper()).postDelayed({
                getScanList(p4.toString())
            }, 30000)
        }
    }

    override fun onScanAbrupted(p0: String?, p1: String?, p2: String?, p3: String?) {
        Log.d("Cheeeeeekkkkk", "onScanAbrupted" + p0 + "--" + p1 + "----" + p2 + "---" + p3)
    }

    override fun onScanFailed(p0: String?, p1: String?, p2: String?, p3: String?) {
        Log.d("Cheeeeeekkkkk", "onScanFailed" + p0 + "--" + p1 + "----" + p2 + "----" + p3)
    }

    override fun onScanInProgress(p0: String?, p1: String?, p2: String?, p3: Int) {
        Log.d("Cheeeeeekkkkk", "onScanInProgress" + p0 + "--" + p1 + "----" + p2 + "----" + p3)
        runOnUiThread {

            // Initialize and show the progress dialog if it isn't already showing
            if (!::progressDialog.isInitialized) {
                progressDialog =
                    ProgressDialog(this@CarPmAuthActivity) // Replace 'YourActivity' with your activity's name
                progressDialog.show()
            }

            // Update the progress in the dialog
            progressDialog.updateProgress(p3)

            // Dismiss the dialog when progress is complete
            if (p3 >= 100) {
                progressDialog.dismiss()

                binding.txtHaveScanner.visibility = View.GONE
                binding.txtContent.visibility = View.GONE

                binding.rvScanResults.visibility = View.VISIBLE

                toast("Scan Completed Successfully! Please Wait for Result")
                binding.pbMain.visibility = View.VISIBLE
            }
        }
    }

    override fun onClearCompleted(p0: String?, p1: String?, p2: String?, p3: String?, p4: Int) {
        Log.d("Cheeeeeekkkkk", "onClearCompleted" + p0 + "--" + p1 + "----" + p3 + "---" + p4)
    }

    override fun onClearAbrupted(p0: String?, p1: String?, p2: String?, p3: String?) {
        Log.d("Cheeeeeekkkkk", "onClearAbrupted" + p0 + "--" + p1 + "----" + p3)
    }

    override fun onClearFailed(p0: String?, p1: String?, p2: String?, p3: String?) {
        Log.d("Cheeeeeekkkkk", "onClearFailed" + p0 + "--" + p1 + "----" + p3)
    }

    override fun onClearInProgress(p0: String?, p1: String?, p2: String?, p3: Int) {
        Log.d("Cheeeeeekkkkk", "onClearInProgress" + p0 + "--" + p1 + "----" + p3)
    }

    override fun onSyncSuccessful() {
        Log.d("Cheeeeeekkkkk", "onSyncSuccessful")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        gProScanProcess.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun playBeepSound() {
        // Initialize the ToneGenerator
        val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

        // Play a beep sound (ToneGenerator.TONE_CDMA_PIP is the typical beep sound)
        toneGen.startTone(ToneGenerator.TONE_CDMA_PIP, 150)

        // You can also use other tones like:
        // ToneGenerator.TONE_PROP_BEEP
        // ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD
        // ToneGenerator.TONE_SUP_CONFIRM

        // Release the ToneGenerator after use
        toneGen.release()
    }

}