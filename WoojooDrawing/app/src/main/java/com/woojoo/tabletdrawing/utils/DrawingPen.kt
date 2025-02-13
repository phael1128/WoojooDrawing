package com.woojoo.tabletdrawing.utils

import android.graphics.*

fun getDrawingPen() = Paint().apply {
    color = Color.BLACK
    style = Paint.Style.STROKE
    strokeWidth = 5f
}

fun getDrawingEraser() = Paint().apply {
    strokeWidth = 50f
    color = Color.BLUE
    xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
}

fun getCropPaint() = Paint().apply {
    color = Color.BLUE
    strokeWidth = 3f
    style = Paint.Style.STROKE
    pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 3f)
}