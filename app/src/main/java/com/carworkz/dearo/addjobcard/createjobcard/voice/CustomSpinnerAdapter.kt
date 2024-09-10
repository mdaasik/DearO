package com.carworkz.dearo.addjobcard.createjobcard.voice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.carworkz.dearo.domain.entities.SpinnerEntity

class CustomSpinnerAdapter(ctx: Context, @LayoutRes private val layoutResource: Int, private val spinnerEntity: List<SpinnerEntity>) : ArrayAdapter<SpinnerEntity>(ctx, layoutResource, spinnerEntity) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: TextView = convertView as TextView?
                ?: LayoutInflater.from(context).inflate(layoutResource, parent, false) as TextView
        view.text = spinnerEntity[position].name()
        return view
    }
}