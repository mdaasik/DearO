package com.carworkz.dearo.extensions

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.view.View
import android.widget.*
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager

fun Parcel.writeBoolean(flag: Boolean?) {
    when (flag) {
        true -> writeInt(1)
        false -> writeInt(0)
        else -> writeInt(-1)
    }
}

fun Parcel.readBoolean(): Boolean? {
    return when (readInt()) {
        1 -> true
        0 -> false
        else -> null
    }
}

fun RadioGroup.checkedIndex(): Int {
    val checkedButton = findViewById(checkedRadioButtonId) as? RadioButton
    return indexOfChild(checkedButton)
}

fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
fun Context.toast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
fun Context.toast(@StringRes message: Int) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
fun Activity.toast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
fun Activity.toast(@StringRes message: Int) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
fun Activity.longToast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
fun Activity.longToast(@StringRes message: Int) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
fun Fragment.toast(message: String) =
    Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
fun Fragment.toast(@StringRes message: Int) =
    Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
fun Fragment.longToast(message: CharSequence) =
    Toast.makeText(this.context, message, Toast.LENGTH_LONG).show()
fun Fragment.longToast(@StringRes message: Int) =
    Toast.makeText(this.context, message, Toast.LENGTH_LONG).show()



var View.padding: Int
     get() = padding
    inline set(value) = setPadding(value, value, value, value)

var TextView.textColor: Int
     get() = textColor
    set(v) = setTextColor(v)

var ImageView.image: Drawable?
    inline get() = drawable
    inline set(value) = setImageDrawable(value)

var View.backgroundColor: Int
     get() = backgroundColor
    set(v) = setBackgroundColor(v)

fun ViewPager.onPageChangeListener(init: __ViewPager_OnPageChangeListener.() -> Unit) {
    val listener = __ViewPager_OnPageChangeListener()
    listener.init()
    addOnPageChangeListener(listener)
}

class __ViewPager_OnPageChangeListener : ViewPager.OnPageChangeListener {
    private var _onPageScrolled: ((Int, Float, Int) -> Unit)? = null
    private var _onPageSelected: ((Int) -> Unit)? = null
    private var _onPageScrollStateChanged: ((Int) -> Unit)? = null

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        _onPageScrolled?.invoke(position, positionOffset, positionOffsetPixels)
    }

    fun onPageScrolled(listener: (Int, Float, Int) -> Unit) {
        _onPageScrolled = listener
    }

    override fun onPageSelected(position: Int) {
        _onPageSelected?.invoke(position)
    }

    fun onPageSelected(listener: (Int) -> Unit) {
        _onPageSelected = listener
    }

    override fun onPageScrollStateChanged(state: Int) {
        _onPageScrollStateChanged?.invoke(state)
    }

    fun onPageScrollStateChanged(listener: (Int) -> Unit) {
        _onPageScrollStateChanged = listener
    }

}
