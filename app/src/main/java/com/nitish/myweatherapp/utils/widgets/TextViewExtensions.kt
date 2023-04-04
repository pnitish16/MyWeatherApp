package com.nitish.myweatherapp.utils.widgets

import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

private const val DRAWABLE_LEFT_INDEX = 0
private const val DRAWABLE_TOP_INDEX = 1
private const val DRAWABLE_RIGHT_INDEX = 2
private const val DRAWABLE_BOTTOM_INDEX = 3

fun TextView.setLeftDrawable(@DrawableRes drawableResId: Int) {
    val leftDrawable = if (drawableResId != 0) {
        ContextCompat.getDrawable(context, drawableResId)
    } else {
        null
    }
    val topDrawable = compoundDrawables[DRAWABLE_TOP_INDEX]
    val rightDrawable = compoundDrawables[DRAWABLE_RIGHT_INDEX]
    val bottomDrawable = compoundDrawables[DRAWABLE_BOTTOM_INDEX]

    setCompoundDrawablesWithIntrinsicBounds(
        leftDrawable,
        topDrawable,
        rightDrawable,
        bottomDrawable
    )
}

fun TextView.setRightDrawable(@DrawableRes drawableResId: Int) {
    val leftDrawable = compoundDrawables[DRAWABLE_LEFT_INDEX]
    val topDrawable = compoundDrawables[DRAWABLE_TOP_INDEX]
    val rightDrawable = if (drawableResId != 0) {
        ContextCompat.getDrawable(context, drawableResId)
    } else {
        null
    }
    val bottomDrawable = compoundDrawables[DRAWABLE_BOTTOM_INDEX]

    setCompoundDrawablesWithIntrinsicBounds(
        leftDrawable,
        topDrawable,
        rightDrawable,
        bottomDrawable
    )
}

fun TextView.setRightDrawable(drawableRes: Drawable? = null) {
    val leftDrawable = compoundDrawables[DRAWABLE_LEFT_INDEX]
    val topDrawable = compoundDrawables[DRAWABLE_TOP_INDEX]
    val bottomDrawable = compoundDrawables[DRAWABLE_BOTTOM_INDEX]

    setCompoundDrawablesWithIntrinsicBounds(
        leftDrawable,
        topDrawable,
        drawableRes,
        bottomDrawable
    )
}

fun TextView.checkRightDrawable(): Boolean {
    val rightDrawable = compoundDrawables[DRAWABLE_RIGHT_INDEX]
    return rightDrawable != null
}
