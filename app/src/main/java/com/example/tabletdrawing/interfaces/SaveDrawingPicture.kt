package com.example.tabletdrawing.interfaces

import android.graphics.Bitmap

fun interface SaveDrawingPicture {
    fun onSave(bitmap: Bitmap)
}