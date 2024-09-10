package com.carworkz.dearo.base

import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

abstract class DelegateSwipeAdapter<T>(private val items: MutableList<out T>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var pendingRemovalDelay: Long = PENDING_REMOVAL_TIMEOUT

    protected val itemsPendingRemoval = mutableListOf<T>()
    private val pendingRunnables = hashMapOf<T, Runnable>()
    private val handler = Handler()

    abstract fun onItemDeleted(position: Int,item: T)

    abstract fun onItemDeletedUndo(position: Int)

    fun pendingRemoval(position: Int) {
        if (position == -1)
            return

        val item = items[position]
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item)
            notifyItemChanged(position)
            val pendingRunnable = Runnable {
                remove(items.indexOf(item))
                Timber.d("item removed $item")
                Timber.d("updated list $items")
            }
            handler.postDelayed(pendingRunnable, pendingRemovalDelay)
            pendingRunnables[item] = pendingRunnable
        }
    }

    fun isPendingRemoval(position: Int): Boolean {
        if (position == -1)
            return false
        if (position == items.size)
            return false

        val item = items[position]
        return itemsPendingRemoval.contains(item)
    }

    fun isPendingRemoval(item: T): Boolean {
        return itemsPendingRemoval.contains(item)
    }

    fun isPendingRemoval(): Boolean = itemsPendingRemoval.isNotEmpty()

    private fun remove(position: Int) {
        if (position == -1)
            return

        val item = items[position]
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item)
        }
        if (items.contains(item)) {
            items.remove(item)
            onItemDeleted(position,item)
            notifyItemRemoved(position)
        }
    }

    fun undoDelete(item: T) {
        val pendingRemovalRunnable = pendingRunnables[item]
        pendingRunnables.remove(item)
        if (pendingRemovalRunnable != null)
            handler.removeCallbacks(pendingRemovalRunnable)
        itemsPendingRemoval.remove(item)
        // this will rebind the row in "normal" state
        val position = items.indexOf(item)
        if (position != -1) {
            notifyItemChanged(position)
            onItemDeletedUndo(position)
        }
    }

    private companion object {
        const val PENDING_REMOVAL_TIMEOUT = 3000L // 3 sec
    }
}