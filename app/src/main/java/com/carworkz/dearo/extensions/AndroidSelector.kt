package com.carworkz.dearo.extensions

import android.app.Fragment
import android.content.Context
import android.content.DialogInterface

inline fun AnkoContext<*>.selector(
    title: CharSequence? = null,
    items: List<CharSequence>,
    noinline onClick: (DialogInterface, Int) -> Unit
): Unit = ctx.selector(title, items, onClick)

inline fun Fragment.selector(
    title: CharSequence? = null,
    items: List<CharSequence>,
    noinline onClick: (DialogInterface, Int) -> Unit
): Unit = activity.selector(title, items, onClick)

fun Context.selector(
    title: CharSequence? = null,
    items: List<CharSequence>,
    onClick: (DialogInterface, Int) -> Unit
) {
    with(AndroidAlertBuilder(this)) {
        if (title != null) {
            this.title = title
        }
        items(items, onClick)
        show()
    }
}