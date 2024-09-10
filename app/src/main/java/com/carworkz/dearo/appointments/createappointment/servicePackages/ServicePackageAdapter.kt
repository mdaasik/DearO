package com.carworkz.dearo.appointments.createappointment.servicePackages

import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.carworkz.dearo.R
import com.carworkz.dearo.domain.entities.ServicePackage
import com.carworkz.dearo.utils.Utility
import timber.log.Timber

/**
 * Created by Kush Singh Chibber on 11/25/2017.
 */
class ServicePackageAdapter(private var packages: List<ServicePackage>) :
    RecyclerView.Adapter<ServicePackageAdapter.ViewHolder>() {

    var selectedList = ArrayList<ServicePackage>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.row_service_package, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = packages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pkg = packages[position]
        holder.oemPriceView.text = Utility.convertToCurrency(pkg.actualPrice.toDouble())
        if (pkg.actualPrice <= 0) {
            holder.oemPriceView.visibility = View.GONE
        } else {
            holder.oemPriceView.visibility = View.VISIBLE
        }
        holder.packagePriceView.text = Utility.convertToCurrency(pkg.offerPrice?.amount?.toDouble())
        holder.nameView.text = pkg.name
        holder.descView.text = pkg.description
        if (selectedList.contains(pkg)) {
            holder.selectedImage.setImageDrawable(
                AppCompatResources.getDrawable(
                    holder.itemView.context,
                    R.drawable.ic_beenhere_black_8dp
                )
            )
            holder.packageCard.background =
                ContextCompat.getDrawable(holder.packageCard.context, R.drawable.selection)
        } else {
            holder.selectedImage.setImageDrawable(null)
            holder.packageCard.background = null
        }
    }

    fun applyFilter(packages: List<ServicePackage>) {
        Timber.d("Filtered")
        this.packages = packages
        notifyDataSetChanged()
    }

    fun selectExisting(existingIds: List<String>) {
        existingIds.forEach { existingId ->
            if (packages.find { it.packageId == existingId } != null) {
                selectedList.add(packages.find { it.packageId == existingId }!!)
            }
        }
    }

    internal fun getSelection(): List<ServicePackage> {
        return selectedList
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var selectedImage: ImageView = view.findViewById(R.id.checkImage)
        internal var packageCard: CardView = view.findViewById(R.id.packageCard)
        private val detailParent: LinearLayout = view.findViewById(R.id.ll_button_parent)
        private val detailBtn: TextView = view.findViewById(R.id.detail_btn)
        internal val packagePriceView: TextView = view.findViewById(R.id.tv_package_price)
        internal val oemPriceView: TextView = view.findViewById(R.id.tv_oem_price)
        internal val nameView: TextView = view.findViewById(R.id.tv_package_name)
        internal val descView: TextView = view.findViewById(R.id.tv_description)

        init {
            oemPriceView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            detailParent.setOnClickListener {
                val intent = Intent(detailParent.context, PackageDetailActivity::class.java)
                intent.putExtra(PackageDetailActivity.PACKAGE, packages[adapterPosition])
                detailParent.context.startActivity(intent)
            }
            detailBtn.setOnClickListener {
                val intent = Intent(detailBtn.context, PackageDetailActivity::class.java)
                intent.putExtra(PackageDetailActivity.PACKAGE, packages[adapterPosition])
                detailBtn.context.startActivity(intent)
            }
            view.setOnClickListener {
                if (selectedList.contains(packages[adapterPosition])) {
                    selectedList.remove(packages[adapterPosition])
                } else {
                    selectedList.add(packages[adapterPosition])
                }
                notifyItemChanged(adapterPosition)
            }
        }
    }
}
