package com.carworkz.dearo.addjobcard.quickjobcard.quickvehiclereading

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.SeekBar
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.quickjobcard.PmsInteractionProvider
import com.carworkz.dearo.base.BaseFragment
import com.carworkz.dearo.base.EventsManager
import com.carworkz.dearo.databinding.LayoutShortFuelReadingBinding
/*import kotlinx.android.synthetic.main.layout_short_fuel_reading.**/
import timber.log.Timber

private const val ARG_KILOMETER_READING = "arg_kilometer_reading"
private const val ARG_FUEL_READING = "arg_fuel_reading"
private const val ARG_IS_VIEW_ONLY = "arg_is_view_only"

class QuickVehicleReadingFragment : BaseFragment(), EventsManager.EventSubscriber {
    private lateinit var binding: LayoutShortFuelReadingBinding
    private var kilometerReading = 0
    private var fuelReading = 0
    private var isViewOnly: Boolean = false
    private var initial = 180f

    private lateinit var pmsInteractionProvider: PmsInteractionProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isViewOnly = it.getBoolean(ARG_IS_VIEW_ONLY)
            kilometerReading = it.getInt(ARG_KILOMETER_READING)
            fuelReading = it.getInt(ARG_FUEL_READING)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutShortFuelReadingBinding.inflate(inflater, container, false)
        return binding.root
        /*return inflater.inflate(R.layout.layout_short_fuel_reading, container, false)*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isViewOnly) {
            binding.etInventoryKm.isEnabled = false
            binding.sbInventoryFuel.isEnabled = false
        }
        val animationSet = AnimationSet(true)
        animationSet.interpolator = DecelerateInterpolator()
        animationSet.fillAfter = true
        animationSet.isFillEnabled
        binding.sbInventoryFuel.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.needle.visibility = View.VISIBLE
                Timber.d("progress ", progress / 1.8f)
                val rotationAnimation = RotateAnimation(initial, 180f + progress, 1.0f, 1.0f)
                initial = 180f + progress
                rotationAnimation.duration = 500
                rotationAnimation.fillAfter = true
                animationSet.addAnimation(rotationAnimation)
                binding.needle.startAnimation(rotationAnimation)
                fuelReading = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        binding.etInventoryKm.setText(kilometerReading.toString())
//        Handler().postDelayed({
// //            pmsInteractionProvider.onKmsChanged(kilometerReading)//for initial setup of regular service.
// //        }, 500)
        binding.etInventoryKm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                kilometerReading = if (s.toString().isNotEmpty()) s.toString().toInt() else 0
                pmsInteractionProvider.onKmsChanged(kilometerReading)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
         binding.sbInventoryFuel.progress = (fuelReading * 1.8f).toInt()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PmsInteractionProvider) {
            pmsInteractionProvider = context
        } else {
            IllegalStateException("activity must implement interaction")
        }
    }

    fun getKilometerReading(): Int = kilometerReading

    fun getFuelReading(): Int = (fuelReading / 1.8f).toInt()

    companion object {
        @JvmStatic
        fun newInstance(isViewOnly: Boolean, kilometerReading: Int, fuelReading: Int) =
                QuickVehicleReadingFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_KILOMETER_READING, kilometerReading)
                        putInt(ARG_FUEL_READING, fuelReading)
                        putBoolean(ARG_IS_VIEW_ONLY, isViewOnly)
                    }
                }
    }
}
