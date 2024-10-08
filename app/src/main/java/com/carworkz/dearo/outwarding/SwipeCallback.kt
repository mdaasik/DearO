package com.carworkz.dearo.outwarding

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.carworkz.dearo.R

abstract class SwipeCallback(dragDirs: Int, swipeDirs: Int, private val context: Context) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    private lateinit var background: Drawable
    private lateinit var deleteIcon: Drawable

    private var xMarkMargin: Int = 0

    private var initiated: Boolean = false
    var leftColorCode: Int = 0
    var leftSwipeLabel: String? = null

    private fun init() {
        background = ColorDrawable()
        xMarkMargin = context.resources.getDimension(R.dimen.ic_clear_margin).toInt()
        deleteIcon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_delete)!!
        deleteIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        initiated = true
    }

    override fun onMove(viewHolder: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val itemView = viewHolder.itemView
        if (!initiated) init()

        val itemHeight = itemView.bottom - itemView.top

        // Setting Swipe Background
        (background as ColorDrawable).color = leftColorCode
        background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        background.draw(c)

        val intrinsicWidth = deleteIcon.intrinsicWidth
        val intrinsicHeight = deleteIcon.intrinsicWidth

        val xMarkLeft = itemView.right - xMarkMargin - intrinsicWidth
        val xMarkRight = itemView.right - xMarkMargin
        val xMarkTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val xMarkBottom = xMarkTop + intrinsicHeight

        // Setting Swipe Icon
        deleteIcon.setBounds(xMarkLeft, xMarkTop + 16, xMarkRight, xMarkBottom)
        deleteIcon.draw(c)

        // Setting Swipe Text
        val paint = Paint()
        paint.color = Color.WHITE
        paint.textSize = context.resources.getDimension(R.dimen.body1)
        paint.textAlign = Paint.Align.CENTER
        leftSwipeLabel?.run {
            c.drawText(this, (xMarkLeft + 40).toFloat(), (xMarkTop + 10).toFloat(), paint)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}