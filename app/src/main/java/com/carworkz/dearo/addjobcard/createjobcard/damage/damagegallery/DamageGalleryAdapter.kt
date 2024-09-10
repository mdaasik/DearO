package com.carworkz.dearo.addjobcard.createjobcard.damage.damagegallery

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.carworkz.dearo.R
import com.carworkz.dearo.domain.entities.FileObject
import timber.log.Timber

class DamageGalleryAdapter(private val imageList: MutableList<FileObject>) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ImageView
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = LayoutInflater.from(container.context).inflate(R.layout.row_damage_gallery, container, false)
        val imageView = itemView.findViewById(R.id.imageView) as ImageView
        Timber.d("Loading Image")
        if (!(imageList[position].url).isNullOrEmpty()) {
            Glide
                    .with(imageView.context)
                    .load(imageList[position].url)
                    .into(imageView)
                    .onLoadFailed(getDefaultPlaceHolder(imageView.context))
        }
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ImageView)
    }

    override fun getCount(): Int {
        return imageList.size
    }

    private fun getDefaultPlaceHolder(context: Context): Drawable {
        return ContextCompat.getDrawable(context, R.drawable.ic_car)!!
    }
}