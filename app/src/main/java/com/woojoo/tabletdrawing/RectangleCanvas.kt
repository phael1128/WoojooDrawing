package com.woojoo.tabletdrawing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.createBitmap


class RectangleCanvas: View {
    constructor(context: Context)
            : super(context) {
                init()
            }

    constructor(context: Context, attributeSet: AttributeSet?)
            : super(context, attributeSet) {
                init()
            }

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int)
            : super(context, attributeSet, defStyleAttr) {
                init()
            }


    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val drawPathArray = ArrayList<Pen>()
    private var bitmap: Bitmap? = null
    private val pointF = PointF()

    private var eventTest = ArrayList<PointF>()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        bitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        for (i in drawPathArray.indices) {
            val current = drawPathArray[drawPathArray.size - 1]

            if (current.isMove()) {
                canvas.drawRect(pointF.x, pointF.y, current.x, current.y, paint)

            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val statue = 0
                pointF.x = event.x
                pointF.y = event.y
                drawPathArray.add(Pen(x = event.x, y = event.y, moveStatus = statue))
                eventTest.clear()
                eventTest.add(PointF(event.x, event.y))
            }

            MotionEvent.ACTION_MOVE -> {
                val state = 1
                drawPathArray.add(Pen(x = event.x, y = event.y, moveStatus = state))
                eventTest.add(PointF(event.x, event.y))
            }

            MotionEvent.ACTION_UP -> {
                val statue = 1
                drawPathArray.clear()

                drawPathArray.add(Pen(x = event.x, y = event.y, moveStatus = statue))
                drawBitmap(drawPathArray)

                Log.d("eventTest Size", "${eventTest.size}")
                Log.d("eventTest Size", "${eventTest.size}")
            }
        }

        invalidate()
        return true
    }

    private fun drawBitmap(arrayList: ArrayList<Pen>) {
        val canvas = Canvas(bitmap!!)

        for (i in arrayList.indices) {
            val current = arrayList[i]

            if (current.isMove()) {
                canvas.drawRect(pointF.x, pointF.y, current.x, current.y, paint)
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        bitmap = createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    }

    private fun init() {
        val dotLine = DashPathEffect(floatArrayOf(5f, 5f), 3f)
        paint.color = Color.BLUE
        paint.strokeWidth = 3f
        paint.style = Paint.Style.STROKE
        paint.pathEffect = dotLine
    }
}
