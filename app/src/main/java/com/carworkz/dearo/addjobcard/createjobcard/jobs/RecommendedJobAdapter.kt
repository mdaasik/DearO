package com.carworkz.dearo.addjobcard.createjobcard.jobs

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.domain.entities.RecommendedJob
import com.carworkz.dearo.extensions.*

/**
 * Created by Farhan on 24/8/17.
 */
class RecommendedJobAdapter(var context: Context, var recommendedList: MutableList<RecommendedJob>, val listProvider: SelectedJobListProvider) : RecyclerView.Adapter<RecommendedJobAdapter.ViewHolder>() {

    companion object {
        const val OFFER = "offer"
        const val RECOMMENDED = "recommended"
        const val HISTORY = "history"
    }

    override fun getItemCount(): Int = recommendedList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_recommended_job, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recommendedObj = recommendedList[position]
        holder.jobTitle.text = recommendedObj.text
        holder.checkBox.tag = recommendedObj
        holder.checkBox.isChecked = recommendedObj.isSelected
        when (recommendedObj.source) {
            HISTORY -> holder.type.image = AppCompatResources.getDrawable(context, R.drawable.ic_history_black_24dp)
            RECOMMENDED -> holder.type.image = AppCompatResources.getDrawable(context, R.drawable.ic_error_black_24dp)
            OFFER -> holder.type.image = AppCompatResources.getDrawable(context, R.drawable.ic_tag)
        }
        if (recommendedObj.isRecommended)
            holder.setApproved()
        else
            holder.setUnApproved()
    }

    fun getList(): List<RecommendedJob> {
        return recommendedList.filter { !it.isRecommended || it.isSelected }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var checkBox: CheckBox = itemView.find(R.id.cb_row_recommended)
        var jobTitle: TextView = itemView.find(R.id.tv_recommended_repair)
        var type: ImageView = itemView.find(R.id.type)
        var clear: ImageButton = itemView.find(R.id.dismiss_btn)

        init {

            checkBox.setOnClickListener { v ->
                val obj = v.tag as RecommendedJob
                if (checkBox.isChecked) {
                    val job = listProvider.demandedJobList.find { it.text == obj.text }
                    if (job != null) {
                        checkBox.isChecked = false
                        context.toast("Job already added")
                    } else {
                        recommendedList[adapterPosition].isSelected = true
                    }
                } else {
                    recommendedList[adapterPosition].isSelected = false
                }
            }

            clear.setOnClickListener {
                if (!recommendedList[adapterPosition].isRecommended) {
                    setApproved()
                } else {
                    setUnApproved()
                }
            }
        }

        fun setApproved() {
            jobTitle.textColor = ContextCompat.getColor(context, R.color.black)
            jobTitle.paintFlags = jobTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            checkBox.isEnabled = true
            checkBox.backgroundColor = ContextCompat.getColor(context, android.R.color.transparent)
            clear.image = ContextCompat.getDrawable(context, R.drawable.ic_clear_black_24dp)
            recommendedList[adapterPosition].isRecommended = true
        }

        fun setUnApproved() {
            jobTitle.textColor = ContextCompat.getColor(context, R.color.old_lavender)
            jobTitle.paintFlags = jobTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            checkBox.backgroundColor = ContextCompat.getColor(context, R.color.light_grey)
            checkBox.isEnabled = false
            checkBox.isChecked = false
            recommendedList[adapterPosition].isRecommended = false
            recommendedList[adapterPosition].isSelected = false
            clear.image = ContextCompat.getDrawable(context, R.drawable.ic_check_black_24dp)
        }
    }
}