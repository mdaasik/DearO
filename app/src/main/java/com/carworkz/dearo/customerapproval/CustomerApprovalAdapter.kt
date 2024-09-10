package com.carworkz.dearo.customerapproval

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R
import com.carworkz.dearo.extensions.find
import com.carworkz.dearo.outwarding.helper.OutwardItem
import com.carworkz.dearo.outwarding.helper.OutwardSection
import com.carworkz.dearo.outwarding.helper.OutwardStep
import com.carworkz.dearo.utils.Utility

class CustomerApprovalAdapter(
    var items: MutableList<OutwardStep>,
    val isFirstTime: Boolean,
    val isViewOnly: Boolean,
    val listener: CallBackListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        return when (viewType)
        {
            VIEW_TYPE_PART -> PartViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.row_customer_approve,
                    parent,
                    false
                )
            )
            VIEW_TYPE_LABOUR -> LabourViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.row_customer_approve,
                    parent,
                    false
                )
            )
            VIEW_TYPE_SECTION -> SectionViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.new_estimator_title,
                    parent,
                    false
                )
            )
            VIEW_TYPE_SERVICE_PACKAGE -> PackageViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.row_customer_approve,
                    parent,
                    false
                )
            )
            else -> EmptyViewHolder(View(parent.context))
        }
    }

    override fun getItemCount(): Int
    {
        return items.size
    }

    override fun getItemViewType(position: Int): Int
    {
        return when (val item = items[position])
        {
            is OutwardItem -> {
                when (item.type) {
                    OutwardItem.TYPE_SERVICE_PKG -> VIEW_TYPE_SERVICE_PACKAGE
                    OutwardItem.TYPE_LABOUR -> VIEW_TYPE_LABOUR
                    OutwardItem.TYPE_PART -> VIEW_TYPE_PART
                    else -> -1
                }
            }
            is OutwardSection -> VIEW_TYPE_SECTION
            else -> -1
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        when (getItemViewType(position))
        {
            VIEW_TYPE_PART -> {
                holder as PartViewHolder
                holder.itemNameText.setTag(items[position])
                val item = items[position] as OutwardItem
                holder.itemNameText.text = item.text
                holder.customerApprovedCheck.isChecked = if(item.isApproved==null) true else item.isApproved==true
                holder.itemTotalText.text =
                    if (item.amount != null) Utility.convertToCurrency(item.amount!!) else "-"
                if(isViewOnly)
                {
                    holder.customerApprovedCheck.isEnabled=false
                }
            }
            VIEW_TYPE_LABOUR -> {
                holder as LabourViewHolder
                holder.itemNameText.setTag(items[position])
                val item = items[position] as OutwardItem
                holder.itemNameText.text = item.text
                holder.customerApprovedCheck.isChecked = if(item.isApproved==null) true else item.isApproved==true
                holder.itemTotalText.text =
                    if (item.amount != null) Utility.convertToCurrency(item.amount!!) else "-"
                if(isViewOnly)
                {
                    holder.customerApprovedCheck.isEnabled=false
                }
            }
            VIEW_TYPE_SECTION -> {
                holder as SectionViewHolder
                val section = items[position] as OutwardSection
                holder.sectionTitleView.text = section.title
            }
            VIEW_TYPE_SERVICE_PACKAGE -> {
                holder as PackageViewHolder
                holder.itemNameText.setTag(items[position])
                val item = items[position] as OutwardItem
                holder.itemNameText.text = item.text
                holder.customerApprovedCheck.isChecked = if(item.isApproved==null) true else item.isApproved==true
                holder.itemTotalText.text =
                    if (item.amount != null) Utility.convertToCurrency(item.amount!!) else "-"
                if(isViewOnly)
                {
                    holder.customerApprovedCheck.isEnabled=false
                }
            }

        }
    }


    inner class SectionViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val sectionTitleView: TextView = view.find(R.id.tv_section_title)
        val sectionTotalView: TextView = view.find(R.id.tv_section_total)
    }

    inner class PartViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val itemNameText: TextView = view.findViewById(R.id.itemNameText)
        val itemTotalText: TextView = view.findViewById(R.id.itemTotalText)
        val customerApprovedCheck: CheckBox = view.findViewById(R.id.customerApprovedCheck)
        init
        {
            customerApprovedCheck.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val item= items[adapterPosition] as OutwardItem
            item.isApproved=customerApprovedCheck.isChecked
            listener.dataChanged(true)
        }
    }


    inner class LabourViewHolder(val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        val itemNameText: TextView = view.findViewById(R.id.itemNameText)
        val itemTotalText: TextView = view.findViewById(R.id.itemTotalText)
        val customerApprovedCheck: CheckBox = view.findViewById(R.id.customerApprovedCheck)
        init
        {
            customerApprovedCheck.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val item= items[adapterPosition] as OutwardItem
            item.isApproved=customerApprovedCheck.isChecked
            listener.dataChanged(true)

        }
    }

    inner class PackageViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        val itemNameText: TextView = view.findViewById(R.id.itemNameText)
        val itemTotalText: TextView = view.findViewById(R.id.itemTotalText)
        val customerApprovedCheck: CheckBox = view.findViewById(R.id.customerApprovedCheck)
        init
        {
            customerApprovedCheck.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val item= items[adapterPosition] as OutwardItem
            item.isApproved=customerApprovedCheck.isChecked
            listener.dataChanged(true)
        }
    }
    inner class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
    }

    companion object
    {
        const val VIEW_TYPE_SECTION = 0
        const val VIEW_TYPE_PART = 1
        const val VIEW_TYPE_LABOUR = 2
        const val VIEW_TYPE_SERVICE_PACKAGE = 3
    }

    interface CallBackListener {
        fun dataChanged(isChanged: Boolean)
    }
}