package com.carworkz.dearo.appointments.createappointment.timeSlot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.domain.entities.Slot
import com.carworkz.dearo.extensions.*

/**
 * Created by kush on 8/12/17.
 */
class TimeSlotAdapter(private val timeSlotList: List<Slot>) : RecyclerView.Adapter<TimeSlotAdapter.ViewHolder>() {

    private var selection = -1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.timeSlotView.isChecked = selection == position
        if (holder.timeSlotView.isChecked) {
            holder.timeSlotView.textColor = ContextCompat.getColor(holder.timeSlotView.context, R.color.white)
        } else {
            holder.timeSlotView.textColor = ContextCompat.getColor(holder.timeSlotView.context, R.color.black)
        }
        holder.timeSlotView.text = timeSlotList[position].displayTime
        holder.timeSlotView.isEnabled = timeSlotList[position].available
        if (!timeSlotList[position].available) {
            // holder.timeSlotView.background= ContextCompat.getDrawable(holder.timeSlotView.context, R.color.light_grey)
            holder.timeSlotView.textColor = ContextCompat.getColor(holder.timeSlotView.context, R.color.noFocus)
        }
    }

    internal fun getTimeSlot(): Slot? {
        return if (selection != -1) timeSlotList[selection] else null
    }

    override fun getItemCount(): Int {
        return timeSlotList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_timeslot, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val timeSlotView = itemView.find<CheckBox>(R.id.timeslotView)

        init {
            itemView.setOnClickListener {
                (itemView as CheckBox).textColor = ContextCompat.getColor(timeSlotView.context, R.color.white)
                if (selection > -1) {
                    val old = selection
                    selection = adapterPosition
                    notifyItemChanged(old)
                    notifyItemChanged(selection)
                } else {
                    selection = adapterPosition
                }
            }
        }
    }
}