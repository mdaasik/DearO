package com.carworkz.dearo.contactus

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import com.carworkz.dearo.R
import com.carworkz.dearo.base.ScreenContainer
import com.carworkz.dearo.base.ScreenContainerActivity
import com.carworkz.dearo.databinding.ActivityContactUsBinding
import com.carworkz.dearo.login.LoginScreenContainer
import com.carworkz.dearo.utils.Utility

class ContactUsActivity : ScreenContainerActivity(), View.OnClickListener {
    private lateinit var binding: ActivityContactUsBinding
    private lateinit var call: Button
    private lateinit var write: Button
    private lateinit var titleView: TextView

    override fun createScreenContainer(): ScreenContainer {
        return LoginScreenContainer()
    }

    override fun getViewBinding(
        inflater: LayoutInflater?, container: ViewGroup?, attachToParent: Boolean
    ): ViewBinding {
        binding = ActivityContactUsBinding.inflate(layoutInflater)
        return binding
    }


    override fun getProgressView(): View? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        titleView = binding.tvCount
        titleView.text = getString(R.string.contact_us_title)
        call = binding.btnCall
        write = binding.btnWrite
        call.setOnClickListener(this)
        write.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_call -> {
                Utility.makeCall(this, "8080133133")
            }

            R.id.btn_write -> {
                val emailIntent = Intent(Intent.ACTION_SENDTO)
                emailIntent.data = Uri.parse("mailto: support@dearo.com")
                startActivity(Intent.createChooser(emailIntent, "Write to us"))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else {
            false
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ContactUsActivity::class.java)
        }
    }
}
