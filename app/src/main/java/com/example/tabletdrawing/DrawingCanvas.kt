package com.example.tabletdrawing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingCanvas: View {

    constructor(context: Context): super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet?): super(context, attributeSet) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int): super(context, attributeSet, defStyleAttr) {
        init()
    }

    private val penColor = Color.BLACK
    private val penSize = 5
    private lateinit var paint: Paint
    private var drawingBitmap: Bitmap? = null
    private lateinit var drawInfoList: ArrayList<Pen>

    // 초기화
    private fun init() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        drawingBitmap = null
        drawInfoList = arrayListOf()
    }

    //현재까지 그린 그림을 bitmap으로 반환하기
//    fun getCurrentCanvas(): Bitmap {
//        val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        this.draw(canvas)
//        return bitmap
//    }

    //실질적으로 그리기??
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(Color.WHITE)

        for (i in 0 until drawInfoList.size) {
            val pen = drawInfoList[i]
            paint.color = penColor
            paint.strokeWidth = penSize.toFloat()

            if (pen.isMove()) {
                val prev = drawInfoList[i - 1]
                canvas?.drawLine(prev.x, prev.y, pen.x, pen.y, paint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        return super.onTouchEvent(event)
        val action = event?.action
        val state = if (action == MotionEvent.ACTION_DOWN) {
            0
        } else {
            1
        }
        drawInfoList.add(
            Pen(
                x = event?.x!!,
                y = event.y!!,
                moveStatus = state
            )
        )
        invalidate()
        return true
    }
}