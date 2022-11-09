package com.example.tabletdrawing

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
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

    private var path = SerializablePath()
    private var strokePathList = ArrayList<SerializablePath>()


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

        when (penMode) {
            MODE_CLEAR_ALL -> {
                parentCanvas.drawBitmap(parentBitmap!!, 0f, 0f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.TRANSPARENT
                })
            }
            MODE_STROKE_ERASER -> {
                parentCanvas.drawBitmap(parentBitmap!!, 0f, 0f, Paint(Paint.ANTI_ALIAS_FLAG))
                canvas.drawBitmap(parentBitmap!!, 0f, 0f, Paint(Paint.ANTI_ALIAS_FLAG))
            }
            else -> {
                canvas.drawBitmap(parentBitmap!!, 0f, 0f, Paint(Paint.ANTI_ALIAS_FLAG))
                canvas.drawPath(path, getCurrentPaint())
            }

        }

        Log.d("Current Paint", "${getCurrentPaint()}")
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER) return false

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
                actionUp()
            }
        }
        invalidate()
        return true
    }

    private val testList = ArrayList<Test>()


    private fun actionDown(event: MotionEvent) {
        path.moveTo(event.x, event.y)
        if (penMode == MODE_PEN) {
            testList.add(Test(event.x, event.y))
        }
    }

    private fun actionMove(event: MotionEvent) {

        when (penMode) {
            MODE_PEN -> {
                path.lineTo(event.x, event.y)
                testList.add(Test(event.x, event.y))
                parentCanvas.drawPath(path, getCurrentPaint())
            }
            MODE_AREA_ERASER -> {
                path.reset()
                path.addCircle(event.x + 10, event.y + 10, 30f, Path.Direction.CW)
                parentCanvas.drawPath(path, getCurrentPaint())
            }

            /*
            * 1. 펜으로 그릴떄마다 현재 좌표값을 list에 담는다.
            * 2. Stroke가 그려질때 마다 그 list의 현재 좌표값을 찾는다.
            * 3. 찾는다면 그 path를 받아서 clear로 drawPath를 한 후 invalidate(parentCanvas)
            * */

            /*
            * PointF를 받는 List 생성
            * 그리고 그 List를 매핑할 클래스 생성
            * 그 클래스는 Path를 상속받고, Serializable를 implement 하도록
            * 왜?
            * 우선 Path로는 그렸던 좌표값을 받을 수가 없음, 때문에 이를 데이터 직렬화 후 찾고 싶은 index를 받으면
            * 그게 Path이기 때문에 그 Path를 drawPath로 Paint Clear 하면 되지 않을까??
            * */
            MODE_STROKE_ERASER -> {
//                path.lineTo(event.x, event.y)
                for (i in 0 until testList.size) {
                    val indexValue = testList[i]
                    if (event.x == indexValue.x && event.y == indexValue.y) {
                        Log.d("exist current coordi", "${event.x}, ${event.y}")
                        return
                    }
                }
            }
        }
    }

    private fun actionUp() {
        if (penMode == MODE_PEN) {
            strokePathList.add(path)
        }

        if (penMode == MODE_STROKE_ERASER) {
            for (i in 0 until strokePathList.size) {
                val indexValue = strokePathList[i].getPathList()
                if (indexValue[i].x == path.getPathList()[i].x && indexValue[i].y == path.getPathList()[i].y) {
                    Log.d("current path is contain", "${indexValue[i]}")
                    val removePath = strokePathList[i]
                    parentCanvas.drawPath(removePath, getCurrentPaint())
                }
            }
        }



        Log.d("Pen History Size", "${testList.size}")
        Log.d("Serializable Path Size", "${strokePathList.size}")
        path = SerializablePath()
    }

    private fun getCurrentPaint(): Paint  = when(penMode) {
        MODE_PEN -> drawingPaint
        MODE_AREA_ERASER -> areaEraserPaint
        MODE_STROKE_ERASER -> strokeEraserPaint
        else -> drawingPaint
    }


    fun setMode(mode: Int) {
        penMode = mode

        if (mode == MODE_CLEAR_ALL) {
            clearCanvas()
        }
    }

    private fun clearCanvas() {
        if (parentBitmap != null) {
            parentBitmap?.recycle()
            parentBitmap = createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
            parentCanvas = Canvas(parentBitmap!!)
            invalidate()
        }
    }

    companion object {
        const val MODE_PEN = 1
        const val MODE_AREA_ERASER = 2
        const val MODE_STROKE_ERASER = 3
        const val MODE_CLEAR_ALL = 5
    }
}

data class Test(
    var x: Float,
    var y: Float
)