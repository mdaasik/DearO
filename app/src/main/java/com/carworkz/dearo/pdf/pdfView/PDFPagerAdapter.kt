package com.carworkz.dearo.pdf.pdfView

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.carworkz.dearo.R
import com.carworkz.dearo.extensions.find
import com.github.chrisbanes.photoview.PhotoView
import timber.log.Timber
import java.io.ByteArrayOutputStream

/**
 * Created by kush on 20/11/17.
 */

class PDFPagerAdapter(private val pdfPages: List<Bitmap>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val v = LayoutInflater.from(container.context).inflate(R.layout.pdf_viewholder, container, false)
        val pdfPage = v.find<PhotoView>(R.id.pdfPage)
        val stream = ByteArrayOutputStream()
        pdfPages[position].compress(Bitmap.CompressFormat.PNG, 100, stream)
        Glide.with(v.context)
                .asBitmap()
                .load(stream.toByteArray())
                .into(pdfPage)
        Timber.d(position.toString())
        container.addView(v)
        return v
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    override fun getCount(): Int = pdfPages.size

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}