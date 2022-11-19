package com.example.tabletdrawing

import android.graphics.Bitmap

fun interface SaveDrawingPicture {
    fun onSave(bitmap: Bitmap)
}