package com.example.tabletdrawing

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.createBitmap

class DrawingCanvas : View {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init()
    }

    private lateinit var paint: Paint
    private lateinit var drawInfoList: ArrayList<Pen>
    private val arrayList = arrayListOf<Pen>()
    private var bitmap: Bitmap? = null
    private var penMode = 1

    // 초기화
    private fun init() {
        //ANTI_ALIAS_FLAG : 계단현상 방지
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        drawInfoList = arrayListOf()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        bitmap = createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    }


    //실질적으로 그리기??
    @SuppressLint("DrawAllocation")
    //이 canvas 는 항상 초기화된 canvas
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        Log.d("yw event status", "drawing")
//
//        bitmapList.forEach {
//            canvas.drawBitmap(it, 0f, 0f, null)
//        }
        bitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        paint.color = Color.BLACK
        paint.strokeWidth = 5f


        for (i in arrayList.indices) {
            val current = arrayList[i]

            if (current.isMove()) {
                val prev = arrayList[i - 1]
                canvas.drawLine(prev.x, prev.y, current.x, current.y, paint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
//        return super.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("yw event status", "down")
                val state = 0
                arrayList.add(Pen(x = event.x, y = event.y, moveStatus = state))
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d("yw event status", "move")
                val state = 1
                arrayList.add(Pen(x = event.x, y = event.y, moveStatus = state))
            }
            MotionEvent.ACTION_UP -> {
                Log.d("yw event status", "up")
                drawBitmap(arrayList)
                arrayList.clear()
            }
        }
        //invalidate
        invalidate()
        return true
    }

    private fun drawBitmap(arrayList: ArrayList<Pen>) {
        val canvas = Canvas(bitmap!!)

        for (i in arrayList.indices) {
            val current = arrayList[i]

            if (current.isMove()) {
                val prev = arrayList[i - 1]
                canvas.drawLine(prev.x, prev.y, current.x, current.y, paint)
            }
        }
    }

    fun setMode(mode: Int) {
        penMode = mode
    }

    companion object {
        const val MODE_PEN = 1
        const val MODE_ERASER = 2
    }
}
