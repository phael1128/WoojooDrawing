package com.example.tabletdrawing

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.createBitmap

class DrawingCanvas : View {

    constructor(context: Context) : super(context) { init() }
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) { init() }
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) { init() }

    private var penMode  = 0
    private lateinit var drawingPaint: Paint
    private lateinit var eraserPaint: Paint
    private lateinit var drawInfoList: ArrayList<Pen>
    private val arrayList = arrayListOf<Pen>()
    private var parentBitmap: Bitmap? = null
    private lateinit var childBitmap: Bitmap

    // 초기화
    private fun init() {
        //ANTI_ALIAS_FLAG : 계단현상 방지
        drawingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            strokeWidth = 5f
        }
        eraserPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
        penMode = MODE_PEN
        drawInfoList = arrayListOf()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        parentBitmap = createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        childBitmap = createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    }


    //실질적으로 그리기??
    @SuppressLint("DrawAllocation")
    //이 canvas 는 항상 초기화된 canvas
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        Log.d("yw event status", "drawing")

        val parentCanvas = Canvas(parentBitmap!!)
        parentCanvas.drawBitmap(childBitmap, 0f, 0f, drawingPaint)

        for (i in arrayList.indices) {
            val current = arrayList[i]

            if (current.isMove()) {
                val prev = arrayList[i - 1]
                canvas.drawLine(prev.x, prev.y, current.x, current.y, drawingPaint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("yw event status", "down")
                actionDown(event)
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d("yw event status", "move")
                actionMove(event)
            }
            MotionEvent.ACTION_UP -> {
                Log.d("yw event status", "up")
                actionUp(event)
            }
        }
        invalidate()
        return true
    }

    private fun actionDown(event: MotionEvent) {
        when (penMode) {
            MODE_PEN -> {
                val state = 0
                arrayList.add(Pen(x = event.x, y = event.y, moveStatus = state))
            }
            MODE_ERASER -> {
                removeCoordinate(event)
            }
        }
    }

    private fun actionMove(event: MotionEvent) {
        when (penMode) {
            MODE_PEN -> {
                val state = 1
                arrayList.add(Pen(x = event.x, y = event.y, moveStatus = state))
            }
            MODE_ERASER -> {
                removeCoordinate(event)
            }
        }
    }

    private fun actionUp(event: MotionEvent) {
        when (penMode) {
            MODE_PEN -> {
                printBitmap(arrayList)
                Log.d("Current PenInfo", "${arrayList.size}")
            }
            MODE_ERASER -> {
                Log.d("Current PenInfo", "${arrayList.size}")
            }
        }
    }

    private fun printBitmap(arrayList: ArrayList<Pen>) {
        val canvas = Canvas(childBitmap)

        for (i in arrayList.indices) {
            val current = arrayList[i]

            if (current.isMove()) {
                val prev = arrayList[i - 1]
                canvas.drawLine(prev.x, prev.y, current.x, current.y, drawingPaint)
            }
        }
    }

    private fun removeCoordinate(event: MotionEvent) {
        //좌표
        val coordi = PointF(event.x, event.y)

        // 우선 array 전체 loop 돌면서 해당 좌표 있는지 찾아보기
        for (i in 0 until arrayList.size) {
            val indexValue = arrayList[i]
            if (indexValue.x == coordi.x && indexValue.y == coordi.y) {
                Log.d("Exist Current Coordinate", "${coordi.x} & ${coordi.y}")
                arrayList.remove(indexValue)
                printBitmap(arrayList)
            }
        }

    }

    /*
    * 지우개 좌표를 찾는 알고리즘을 수정 해보자
    * 현재 pen의 좌표를 담는 array에서 처음부터 끝까지 찾는 알고리즘이다.
    * 이건 길이가 길수록 시간적 효율이 너무 안 좋으니 반으로 잘라서해보자
    * 생각해보니 이 방법은 안될것 같음,, 시도할려는게 이분 탐색 알고리즘인데
    * 이분탐색 특성상 정렬이 안되면 의미가 없음
    * */

    fun removeCoordinate2(event: MotionEvent) {
        val standardIndex = arrayList.size / 2
        val coordi = PointF(event.x, event.y)

        for (i in 0 until standardIndex) {
            val indexValue = arrayList[i]
            if (indexValue.x == coordi.x && indexValue.y == coordi.y) {
                arrayList.remove(indexValue)
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
