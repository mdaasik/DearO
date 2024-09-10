package com.carworkz.dearo.cardslisting

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by farhan on 03/01/18
 */

abstract class CardListingBaseAdapter<VH : CardListingBaseViewHolder> : RecyclerView.Adapter<VH>() {

    abstract fun removeItemAndRefresh(id: String)
}