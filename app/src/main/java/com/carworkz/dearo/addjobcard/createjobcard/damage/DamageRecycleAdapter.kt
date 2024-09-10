package com.carworkz.dearo.addjobcard.createjobcard.damage

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.carworkz.dearo.R
import com.carworkz.dearo.addjobcard.createjobcard.damage.damagegallery.DamageGalleryActivity
import com.carworkz.dearo.domain.entities.FileObject
import com.carworkz.dearo.domain.entities.JobCard
import com.carworkz.dearo.extensions.alert
import com.carworkz.dearo.extensions.cancelButton
import com.carworkz.dearo.extensions.customView
import com.carworkz.dearo.extensions.padding
import com.carworkz.dearo.utils.Utility
import timber.log.Timber
/**
 * Created by kush on 14/8/17.
 */

class DamageRecycleAdapter(
    private val imageList: ArrayList<FileObject>,
    private val interaction: EditDamageCallback,
    private val isEditable: Boolean,
    private val type: String
) : RecyclerView.Adapter<DamageRecycleAdapter.ViewHolder>() {

    private fun setPlaceHolder(context: Context): Drawable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getDrawable(R.drawable.ic_cardamage)!!
        } else {
            ContextCompat.getDrawable(context, R.drawable.ic_cardamage)!!
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.image_card_grid,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (imageList[position].pid.isEmpty()) {
            imageList.removeAt(position)
        }

        holder.captionView.text = imageList[position].title

        if (!(imageList[position].url).isNullOrEmpty()) {
            Glide
                .with(holder.damageImageView.context)
                .load(imageList[position].url)
                .into(holder.damageImageView)
                .onLoadFailed(setPlaceHolder(holder.damageImageView.context))
        } else {
            Timber.d(imageList[position].uri)
            Glide
                .with(holder.damageImageView.context)
                .load(imageList[position].uri)
                .into(holder.damageImageView)
                .onLoadFailed(setPlaceHolder(holder.damageImageView.context))
        }

        if (isEditable) {
            holder.captionView.setOnTouchListener(View.OnTouchListener { it, event ->
                val drawableRight = 2
                if (event.rawX >= (it.right - holder.captionView.compoundDrawables[drawableRight].bounds.width())) {
                    val popupMenu = PopupMenu(
                        it.context,
                        holder.captionView,
                        Gravity.NO_GRAVITY,
                        androidx.appcompat.R.attr.actionOverflowMenuStyle,
                        0
                    )
                    popupMenu.inflate(R.menu.pop_up_menu)

                    if (!imageList[position].isUploaded)
                        popupMenu.menu.getItem(0).isVisible = false

                    popupMenu.setOnMenuItemClickListener { item ->
                        when (item!!.itemId) {
                            R.id.edit -> {

                              /*  val mRlayout = RelativeLayout(it.context)
                                val mRparams: RelativeLayout.LayoutParams =
                                    RelativeLayout.LayoutParams(
                                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT
                                    )
                                val myEditText = EditText(it.context)
                                myEditText.layoutParams = mRparams
                                mRlayout.addView(myEditText)

                                val builder = AlertDialog.Builder(it.context)

                                builder.setPositiveButton("Done") { dialog, which ->
                                    interaction.onEditCaption(
                                        imageList[position].id!!,
                                        myEditText.text.toString()
                                    )
                                    notifyItemChanged(position)
                                }

                                builder.setNegativeButton(android.R.string.cancel) { dialog, which ->
                                    Timber.d("Alert box", "Canceled")
                                }
                                builder.setCancelable(false)
                                builder.show()*/



                                it.context.alert {
                                    customView {

                                        val newCaption= EditText(it.context)
                                        newCaption.hint="Enter New Caption"
//                                        newCaption.padding= 5
                                        addView(newCaption,null)

                                        positiveButton("Done") {
                                            interaction.onEditCaption(
                                                imageList[position].id!!,
                                                newCaption.text.toString()
                                            )
                                            notifyItemChanged(position)
                                        }
                                        cancelButton {
                                            Timber.d("Alert box", "Canceled")
                                        }
                                    }
                                }.show()

                            }
                            R.id.delete -> {
                                interaction.onDeleteImage(imageList[position])
                                imageList.removeAt(position)
                                notifyItemRemoved(position)
                            }
                        }
                        true
                    }
                    popupMenu.show()

                    return@OnTouchListener false
                }
                false
            })
        } else {
            holder.captionView.setCompoundDrawables(null, null, null, null)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var captionView: TextView = itemView.findViewById(R.id.tv_damage) as TextView
        var damageImageView: ImageView = itemView.findViewById(R.id.iv_damage) as ImageView

        init {
            damageImageView.setOnClickListener {
                val intent = Intent(it.context, DamageGalleryActivity::class.java)
                intent.putExtra(
                    DamageGalleryActivity.ARG_JOBCARD_ID,
                    imageList[adapterPosition].jobCardID!!
                )
                intent.putExtra(DamageGalleryActivity.ARG_TYPE, type)
                intent.putExtra(DamageGalleryActivity.ARG_SELECTION, adapterPosition)
                it.context.startActivity(intent)
            }
        }
    }
}
