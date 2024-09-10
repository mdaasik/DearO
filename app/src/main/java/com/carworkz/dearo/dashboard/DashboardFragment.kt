package com.carworkz.dearo.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.carworkz.dearo.DearOApplication
import com.carworkz.dearo.HomeInteraction
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.ActionButtonClickEvent
import com.carworkz.dearo.analytics.ScreenTracker
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.cardslisting.CardListingFragment
import com.carworkz.dearo.databinding.FragmentDashboardBinding
import com.carworkz.dearo.domain.entities.WorkshopResource
import com.carworkz.dearo.extensions.toast
import com.carworkz.dearo.utils.Utility
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber
import java.io.IOException
import java.util.UUID
import javax.inject.Inject


class DashboardFragment : BaseFragment(), DashboardContract.View, EventsManager.EventSubscriber {
    private lateinit var binding: FragmentDashboardBinding

    @Inject
    lateinit var presenter: DashboardPresenter

    private lateinit var bluetoothAdapter: BluetoothAdapter

    @Inject
    lateinit var screenTracker: ScreenTracker

    private lateinit var interaction: HomeInteraction

    private lateinit var dialogList: Dialog
    private lateinit var dialogShowObdii: Dialog
    private lateinit var bluetoothDeviceList: MutableList<BluetoothDevice>
    private lateinit var listAdapter: ArrayAdapter<String>
    private lateinit var listView: ListView

    private val REQUEST_ENABLE_BT = 1
    private val REQUEST_PERMISSIONS = 2

    private var bluetoothSocket: BluetoothSocket? = null
    private val handler = Handler()
    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root/* return inflater.inflate(R.layout.fragment_dashboard, container, false)*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ((requireActivity().application) as DearOApplication).repositoryComponent.COMPONENT(
            DashboardPresenterModule(this)
        ).inject(this)
        presenter.getDashBoardDetails()

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothDeviceList = mutableListOf()

        binding.tagReadCodes.setOnClickListener {
           // checkBluetoothConnection()
        }

        binding.tagClearCodes.setOnClickListener {
           // checkBluetoothConnection()
        }


        binding.tagPoweredBy.setOnClickListener { showDialogOBDII() }


        binding.tagDiagonise.setOnClickListener { showDiagonise() }
        binding.tagReports.setOnClickListener { showReports() }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeInteraction) {
            interaction = context
        } else {
            IllegalStateException("Activity must implement interaction")
        }
    }

    override fun onResume() {
        super.onResume()
        EventsManager.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        EventsManager.unregister(this)
    }

    override fun moveToNextScreen() {
    }



    private fun checkPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.BLUETOOTH_ADMIN
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.BLUETOOTH_ADMIN
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                ), REQUEST_PERMISSIONS
            )
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), REQUEST_PERMISSIONS
            )
        }
    }

    // Call this method to check and request permissions as needed
    private fun ensurePermissions() {
        if (!checkPermissions()) {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Handle permission results here
    }


    @SuppressLint("MissingPermission")
    private fun showDialogOBDII() {
        dialogShowObdii = Dialog(requireContext())
        dialogShowObdii.setContentView(R.layout.dialog_banner_scanner)
        dialogShowObdii.setCancelable(false)
        dialogShowObdii.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogShowObdii.show()

        val imgClose: ImageView = dialogShowObdii.findViewById(R.id.img_close)
        val tvHaveScanner: TextView = dialogShowObdii.findViewById(R.id.txt_have_scanner)
        val tvCheckOffers: TextView = dialogShowObdii.findViewById(R.id.txt_scanner_offers)


        imgClose.setOnClickListener {
            dialogShowObdii.dismiss()
        }

        tvHaveScanner.setOnClickListener {
            if (bluetoothAdapter.isEnabled) {
                if (checkPermissions()) {
                    // showDialog()
                } else {
                    requestPermissions()
                }
            } else {
              /*  val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, REQUEST_ENABLE_BT)*/
            }
        }

        tvCheckOffers.setOnClickListener {
          /*  sendMessageToWhatsApp(
                "7402085201", "Check scanner offers in MYTVS to buy a ODBII Bluetooth Device"
            )*/
        }

    }

   /* private fun sendMessageToWhatsApp(phoneNumber: String, message: String) {
        try {
            val uri = Uri.parse("https://wa.me/$phoneNumber?text=${Uri.encode(message)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.whatsapp")
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            toast("WhatsApp not installed.")
        }
    }*/


    @SuppressLint("MissingPermission")
    private fun showDialog() {
        dialogList = Dialog(requireContext())
        dialogList.setContentView(R.layout.dialog_odb_scanner)
        dialogList.setCancelable(false)
        dialogList.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogList.show()

        val imgClose: ImageView = dialogList.findViewById(R.id.img_close)
        listView = dialogList.findViewById(R.id.rvBluetoothList)

        listAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_list_item_1, mutableListOf<String>()
        )
        listView.adapter = listAdapter

        imgClose.setOnClickListener {
            dialogList.dismiss()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val device = bluetoothDeviceList[position]
            Toast.makeText(requireContext(), "Selected: ${device.name}", Toast.LENGTH_SHORT).show()

            connectToDevice(device)
            dialogList.dismiss()
        }

        //discoverBluetoothDevices()
        showPairedDevices()

        /* val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
         requireContext().registerReceiver(receiver, filter)
         bluetoothAdapter.startDiscovery()*/
    }

    @SuppressLint("MissingPermission")
    private fun showPairedDevices() {
        bluetoothDeviceList.clear()
        listAdapter.clear()

        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        pairedDevices?.forEach { device ->
            bluetoothDeviceList.add(device)
            listAdapter.add(device.name ?: "Unknown Device")
        }
        listAdapter.notifyDataSetChanged()
    }

    /* @SuppressLint("MissingPermission")
     private fun showPairedDevices() {
         bluetoothDeviceList.clear()
         listAdapter.clear()

         val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
         pairedDevices?.forEach { device ->
             if (device.name?.contains("OBD", ignoreCase = true) == true || device.name?.contains("OBDII", ignoreCase = true) == true) {
                 bluetoothDeviceList.add(device)
                 listAdapter.add(device.name ?: "Unknown Device")
             }
         }
         listAdapter.notifyDataSetChanged()
     }*/


    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice) {
        val uuid: UUID =
            device.uuids?.firstOrNull()?.uuid ?: MY_UUID // Use known UUID if UUID array is null

        // Cancel any ongoing discovery to speed up the connection process
        bluetoothAdapter.cancelDiscovery()

        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
            bluetoothSocket?.connect()
            handler.post {
                Toast.makeText(requireContext(), "Connected to ${device.name}", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: IOException) {
            Timber.e("Primary connection failed: ${e.message}")

            // Attempt fallback connection method
            try {
                val m =
                    device.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
                bluetoothSocket = m.invoke(device, 1) as BluetoothSocket
                bluetoothSocket?.connect()
                handler.post {
                    Toast.makeText(
                        requireContext(), "Connected to ${device.name}", Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (fallbackException: Exception) {
                Timber.e("Fallback connection failed: ${fallbackException.message}")

                handler.post {
                    Toast.makeText(
                        requireContext(), "Failed to connect to ${device.name}", Toast.LENGTH_SHORT
                    ).show()
                }

                try {
                    bluetoothSocket?.close()
                } catch (closeException: IOException) {
                    Timber.e("Could not close the client socket: ${closeException.message}")
                }
            }
        }
    }

    /* @SuppressLint("MissingPermission")
     private fun connectToDevice(device: BluetoothDevice) {
         val uuid: UUID = device.uuids?.firstOrNull()?.uuid ?: MY_UUID

         bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)

         bluetoothAdapter.cancelDiscovery()

            // Using a background thread to connect to the device
            Thread {
                try {
                    bluetoothSocket?.connect()
                    handler.post {
                        Toast.makeText(requireContext(), "Connected to ${device.name}", Toast.LENGTH_SHORT).show()
                    }
                } catch (connectException: IOException) {
                    Timber.e(connectException, "Failed to connect to ${device.name}")
                    handler.post {
                        Toast.makeText(requireContext(), "Failed to connect to ${device.name}", Toast.LENGTH_SHORT).show()
                    }
                    try {
                        bluetoothSocket?.close()
                    } catch (closeException: IOException) {
                        Timber.e(closeException, "Could not close the client socket")
                    }
                }
            }.start()
     }*/

    override fun onDestroyView() {
        super.onDestroyView()
    }


    override fun showGenericError(errorMsg: String) {
        displayError(errorMsg)
    }

    override fun showProgressIndicator() {
        binding.pbMain.visibility = View.VISIBLE
    }

    fun showDiagonise() {
        binding.constraintDiagnose.visibility = View.VISIBLE
        binding.constraintReports.visibility = View.GONE

        binding.viewDiagonise.visibility = View.VISIBLE
        binding.viewReports.visibility = View.GONE
    }

    fun showReports() {
        binding.constraintDiagnose.visibility = View.GONE
        binding.constraintReports.visibility = View.VISIBLE

        binding.viewDiagonise.visibility = View.GONE
        binding.viewReports.visibility = View.VISIBLE
    }

    override fun dismissProgressIndicator() {
        binding.pbMain.visibility = View.GONE
    }

    override fun showDashBoardData(obj: WorkshopResource) {
        countUpEffect(obj.delivered ?: 0, binding.deliveredCountView)
        countUpEffect(obj.ready ?: 0, binding.readyCountView)
        countUpEffect(obj.pending ?: 0, binding.pendingCountView)

        binding.vehicleInflowFTDView.text = "${obj.vehicleFlow?.ftd ?: 0}"
        binding.vehicleInflowMTDView.text = "${obj.vehicleFlow?.mtd ?: 0}"

        binding.partSalesFTDView.text =
            Utility.convertToCurrency(obj.invoice?.part?.ftd?.jobCard?.toDouble() ?: 0.0)
        binding.partSalesMTDView.text =
            Utility.convertToCurrency(obj.invoice?.part?.mtd?.jobCard?.toDouble() ?: 0.0)

        binding.labourRevenueFTDView.text =
            Utility.convertToCurrency(obj.invoice?.labour?.ftd?.toDouble() ?: 0.0)
        binding.labourRevenueMTDView.text =
            Utility.convertToCurrency(obj.invoice?.labour?.mtd?.toDouble() ?: 0.0)

        binding.servicePackageFTDView.text =
            Utility.convertToCurrency(obj.invoice?.servicePackage?.ftd?.toDouble() ?: 0.0)
        binding.servicePackageMTDView.text =
            Utility.convertToCurrency(obj.invoice?.servicePackage?.mtd?.toDouble() ?: 0.0)

        binding.counterSalesFTDView.text =
            Utility.convertToCurrency(obj.invoice?.part?.ftd?.otc?.toDouble() ?: 0.0)
        binding.counterSalesMTDView.text =
            Utility.convertToCurrency(obj.invoice?.part?.mtd?.otc?.toDouble() ?: 0.0)

        binding.amcSalesFTDView.text = Utility.convertToCurrency(obj.invoice?.amc?.ftd ?: 0.0)
        binding.amcSalesMTDView.text = Utility.convertToCurrency(obj.invoice?.amc?.mtd ?: 0.0)

        binding.totalFTDView.text =
            Utility.convertToCurrency(obj.summary?.totalFtd?.toDouble() ?: 0.0)
        binding.totalMTDView.text =
            Utility.convertToCurrency(obj.summary?.totalMtd?.toDouble() ?: 0.0)

        binding.pendingVehicleParentView.setOnClickListener {
            interaction.switchToJobCard(CardListingFragment.LIST_TYPE_JOB_CARD_IN_PROGRESS)
        }

        binding.readyVehicleParentView.setOnClickListener {
            interaction.switchToJobCard(CardListingFragment.LIST_TYPE_JOB_CARD_COMPLETED)
        }

        binding.deliveredVehicleParentView.setOnClickListener {
            interaction.switchToJobCard(CardListingFragment.LIST_TYPE_JOB_CARD_CLOSED)
        }
    }

    @Subscribe
    fun onNextBtnEvent(btn: ActionButtonClickEvent) {
        Timber.d("DashboardFragment Button Click ")
    }

    private fun countUpEffect(total: Int, view: TextView) {
        Thread(Runnable {
            val speed = when {
                total >= 400 -> 10L
                total >= 300 -> 15L
                total >= 100 -> 20L
                total >= 50 -> 25L
                else -> 40L
            }
            Timber.d("Speed $speed")
            for (x in 0..total) {
                try {
                    Thread.sleep(speed)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                view.post { view.text = x.toString() }
            }
        }).start()
    }

    companion object {
        const val TAG = "Dashboard"
        fun newInstance() = DashboardFragment().apply {
            val args = Bundle()
            arguments = args
        }
    }
}