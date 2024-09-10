package com.carworkz.dearo.addjobcard.createjobcard.accidental

import android.content.DialogInterface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.carworkz.dearo.R
import com.carworkz.dearo.databinding.RowDocumentBinding
import com.carworkz.dearo.domain.entities.FileObject
import com.carworkz.dearo.domain.entities.JobCard

class DocumentAdapter(
    val list: MutableList<FileObject>,
    private val interaction: DocumentInteraction,
    private val status: String
) : RecyclerView.Adapter<DocumentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowDocumentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val obj = list[position]
        if (obj.originalName.contains(".jpg") || obj.originalName.contains(".jpeg") || obj.originalName.contains(".png")) {
            holder.bindImage(obj)
        } else {
            holder.bindDoc(obj)
        }
    }

    override fun getItemCount(): Int = list.size

    fun deleteItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class ViewHolder(private val binding: RowDocumentBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindDoc(obj: FileObject) {
            binding.fileView.setImageDrawable(itemView.context.getDrawable(R.drawable.file_pdf))
            binding.titleView.text = obj.meta?.category

            if (status != JobCard.STATUS_COMPLETED && status != JobCard.STATUS_CLOSED) {
                binding.optionView.setOnClickListener {
                    showPopupMenu(it, list[adapterPosition], adapterPosition)
                }
            }
        }

        private fun showPopupMenu(view: View, fileObject: FileObject, position: Int) {
            val popupMenu = PopupMenu(view.context, view, Gravity.NO_GRAVITY, androidx.appcompat.R.attr.actionOverflowMenuStyle, 0)
            popupMenu.inflate(R.menu.pop_up_menu)
            popupMenu.menu.getItem(0).isVisible = false
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.delete -> {
                        val alertDialog = AlertDialog.Builder(view.context)

                        alertDialog.setTitle("Delete?")
                        alertDialog.setMessage("Are you sure, you want to delete this document?")
                        alertDialog.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                            interaction.deleteDocument(fileObject, position)
                        }
                        alertDialog.setNegativeButton("No") { dialog: DialogInterface, _: Int ->
                            dialog.dismiss()
                        }
                        alertDialog.show()
                    }
                }
                true
            }
            popupMenu.show()
        }

        fun bindImage(obj: FileObject) {
            if (obj.uri != null) {
                Glide.with(itemView.context)
                    .load(obj.uri)
                    .into(binding.fileView)
            } else {
                Glide.with(itemView.context)
                    .load(obj.url)
                    .into(binding.fileView)
            }
            binding.titleView.text = obj.meta?.category

            if (status != JobCard.STATUS_COMPLETED && status != JobCard.STATUS_CLOSED) {
                binding.optionView.setOnClickListener {
                    showPopupMenu(it, list[adapterPosition], adapterPosition)
                }
            }
        }
    }

    interface DocumentInteraction {
        fun deleteDocument(fileObject: FileObject, position: Int)
    }
}
