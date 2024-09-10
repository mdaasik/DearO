package com.carworkz.dearo.partfinder

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cursoradapter.widget.CursorAdapter
import com.carworkz.dearo.R
import com.carworkz.dearo.extensions.find

class SearchSuggestionAdapter(context: Context, cursor: Cursor?) : CursorAdapter(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER) {

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        val viewType = getItemViewType(cursor!!)
        val view: View
        view = if (viewType == PartFinderActivity.PART_IS_CUSTOM_TRUE) {
            LayoutInflater.from(context).inflate(R.layout.row_part_finder_custom, parent, false)
        } else
            LayoutInflater.from(context).inflate(R.layout.row_part_finder, parent, false)
        view.tag = SuggestionViewHolder(view)
        return view
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val holder = view?.tag as SuggestionViewHolder
        holder.partNameView?.text = cursor?.getString(cursor.getColumnIndex(PartFinderActivity.SUGGEST_COLUMN_PART_NAME))
        holder.partDescpView?.text = cursor?.getString(cursor.getColumnIndex(PartFinderActivity.SUGGEST_COLUMN_PART_DESCRIPTION))
        holder.partPriceView?.text = cursor?.getString(cursor.getColumnIndex(PartFinderActivity.SUGGEST_COLUMN_PART_RATE))
        holder.partNumberView?.text = cursor?.getString(cursor.getColumnIndex(PartFinderActivity.SUGGEST_COLUMN_PART_NUMBER))
        if (cursor?.getColumnIndex(PartFinderActivity.SUGGEST_COLUMN_PART_REMARK) != null) {
            holder.remarkView?.visibility = View.VISIBLE
            holder.remarkView?.text = cursor.getString(cursor.getColumnIndex(PartFinderActivity.SUGGEST_COLUMN_PART_REMARK))
        } else {
            holder.remarkView?.visibility = View.GONE
        }
    }

    private fun getItemViewType(cursor: Cursor): Int {
        return cursor.getInt(cursor.getColumnIndex(PartFinderActivity.SUGGEST_COLUMN_IS_CUSTOM))
    }

    override fun getItemViewType(position: Int): Int {
        val cursor = getItem(position) as Cursor
        return getItemViewType(cursor)
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    class SuggestionViewHolder(view: View?) {
        val partNameView = view?.find<TextView>(R.id.tv_part_title)
        val partDescpView = view?.find<TextView>(R.id.tv_part_description)
        val partPriceView = view?.find<TextView>(R.id.tv_part_finder_part_rate)
        val partNumberView = view?.find<TextView>(R.id.tv_part_finder_part_number)
        val partInStockView = view?.find<TextView>(R.id.tv_part_finder_instock)
        val remarkView = view?.find<TextView>(R.id.remarkView)
    }
}