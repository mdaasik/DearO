package com.carworkz.dearo.carpm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.R
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityClearCodeBinding
import com.carworkz.dearo.domain.entities.ClearCodeCarpmRequest
import com.carworkz.dearo.interactionprovider.ToolBarImgInteractionProvider
import com.carworkz.dearo.networkAPI.ApiClient
import com.carworkz.dearo.networkAPI.ApiRequest
import com.carworkz.dearo.screencontainer.ActionImgScreenContainer
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClearCodeActivity : ScreenContainerActivity(), ToolBarImgInteractionProvider {
    private lateinit var binding: ActivityClearCodeBinding
    private lateinit var jobCardId: String
    var restApi: ApiRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        jobCardId = intent?.extras?.getString(ARG_JOB_ID)!!
        restApi = ApiClient.getAPIClient()?.create(ApiRequest::class.java)

        binding.btnSave.setOnClickListener {
            if (!binding.edRemarks.text.isEmpty() && !binding.edRemarks.text.isBlank()) {
                clearCodes(binding.edRemarks.text.toString())
            } else {
                Toast.makeText(applicationContext, "Kindly Enter Remarks", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun getProgressView(): View {
        TODO("Not yet implemented")
    }

    override fun createScreenContainer(): ScreenContainer = ActionImgScreenContainer(this)


    override fun getViewBinding(
        inflater: LayoutInflater?, container: ViewGroup?, attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityClearCodeBinding.inflate(layoutInflater)
        return binding
    }

    private fun clearCodes(remarks: String) {
        binding.pbMain.visibility = View.VISIBLE
        Log.d("Cheeeeeekkkkk", "----" + jobCardId + "----" + remarks)
        val request = ClearCodeCarpmRequest(remarks, jobCardId)
        val call = restApi!!.clearScannedData(request)
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(
                call: Call<JsonElement>, response: Response<JsonElement>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        binding.pbMain.visibility = View.GONE
                        Toast.makeText(
                            applicationContext, "Codes cleared successfully!", Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        binding.pbMain.visibility = View.GONE
                        Toast.makeText(
                            applicationContext, "Failed to clear codes", Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                } else {
                    binding.pbMain.visibility = View.GONE
                    Toast.makeText(applicationContext, "Failed to clear codes", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                binding.pbMain.visibility = View.GONE
                Toast.makeText(applicationContext, "Failed to clear codes", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        })
    }

    companion object {
        const val ARG_JOB_ID = "ARG_JOB_CARD_ID"
        fun getIntent(
            context: Context,
            Id: String?,
        ): Intent {
            return Intent(context, ClearCodeActivity::class.java).putExtra(ARG_JOB_ID, Id)
        }
    }

    override fun getNavigationImage(): Int = R.drawable.ic_arrow_back_white_24dp

    override fun onActionBtnClick() = Unit

    override fun onSecondaryActionBtnClick() {
    }

    override fun getActionBarImage(): Int = 0

    override fun getSecondaryActionBarImage(): Int = 0
    override fun getToolBarTitle(): String = "Clear Codes"
}
