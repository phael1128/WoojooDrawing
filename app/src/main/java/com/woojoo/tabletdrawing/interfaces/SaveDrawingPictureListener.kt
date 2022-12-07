package com.woojoo.tabletdrawing.interfaces

import android.graphics.Bitmap

fun interface SaveDrawingPictureListener {
    fun onSave(bitmap: Bitmap)
}