package com.example.test.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat

fun getBitmapFromDrawable(context: Context, drawableId: Int, tint: Int? = null): Bitmap? {
    val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null

    if (tint != null) {
        drawable.setTint(tint)
    }

    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    ) ?: return null
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}