package com.example.tabletdrawing

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
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
    private lateinit var areaEraserPaint: Paint
    private lateinit var strokeEraserPaint: Paint

    private var parentBitmap: Bitmap? = null
    private lateinit var parentCanvas: Canvas

    private var path = Path()


    // 초기화
    private fun init() {
        //ANTI_ALIAS_FLAG : 계단현상 방지
        drawingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        areaEraserPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 50f
            color = Color.BLUE
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
        strokeEraserPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
        penMode = MODE_PEN
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        parentBitmap = createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        parentCanvas = Canvas(parentBitmap!!)
    }


    //실질적으로 그리기??
    @SuppressLint("DrawAllocation")
    //이 canvas 는 항상 초기화된 canvas
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        Log.d("yw event status", "drawing")

        canvas.drawBitmap(parentBitmap!!, 0f, 0f, Paint(Paint.ANTI_ALIAS_FLAG))
        canvas.drawPath(path, drawingPaint)

        Log.d("Current Paint", "${getCurrentPaint()}")
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
        path.moveTo(event.x, event.y)
    }

    private fun actionMove(event: MotionEvent) {
        when (penMode) {
            MODE_PEN -> {
                path.lineTo(event.x, event.y)
            }
            MODE_AREA_ERASER -> {
                //현재 라인 그려짐
                path.reset()
                path.addCircle(event.x + 10, event.y + 10, 30f, Path.Direction.CW)

            }
            MODE_STROKE_ERASER -> {

            }
        }
        parentCanvas.drawPath(path, getCurrentPaint())
    }

    private fun actionUp(event: MotionEvent) {
        path = Path()
    }

    private fun getCurrentPaint(): Paint  = when(penMode) {
        MODE_PEN -> drawingPaint
        MODE_AREA_ERASER -> areaEraserPaint
        MODE_STROKE_ERASER -> strokeEraserPaint
        else -> Paint(Paint.ANTI_ALIAS_FLAG)
    }



    /*
    * 지우개 좌표를 찾는 알고리즘을 수정 해보자
    * 현재 pen의 좌표를 담는 array에서 처음부터 끝까지 찾는 알고리즘이다.
    * 이건 길이가 길수록 시간적 효율이 너무 안 좋으니 반으로 잘라서해보자
    * 생각해보니 이 방법은 안될것 같음,, 시도할려는게 이분 탐색 알고리즘인데
    * 이분탐색 특성상 정렬이 안되면 의미가 없음
    * */





    fun setMode(mode: Int) {
        penMode = mode
    }

    companion object {
        const val MODE_PEN = 1
        const val MODE_AREA_ERASER = 2
        const val MODE_STROKE_ERASER = 3
    }
}
