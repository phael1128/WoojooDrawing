package com.example.tabletdrawing

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.createBitmap

class DrawingCanvas : AppCompatImageView {

    constructor(context: Context) : super(context) { init() }
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) { init() }
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) { init() }

    private var penMode  = 0
    private lateinit var drawingPaint: Paint
    private lateinit var areaEraserPaint: Paint
    private lateinit var strokeEraserPaint: Paint

    private var imageBitmap: Bitmap? = null
    private var parentBitmap: Bitmap? = null
    private lateinit var parentCanvas: Canvas

    private var path = SerializablePath()
    private var strokePathList = ArrayList<SerializablePath>()
    private var strokeEraserList = ArrayList<SerializablePath>()


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

        imageBitmap?.let {
            canvas.setBitmap(imageBitmap)
        }

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
//        if (event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER) return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                actionDown(event)
            }
            MotionEvent.ACTION_MOVE -> {
                actionMove(event)
            }
            MotionEvent.ACTION_UP -> {
                actionUp()
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
                parentCanvas.drawPath(path, getCurrentPaint())
            }
            MODE_AREA_ERASER -> {
                path.reset()
                path.addCircle(event.x + 10, event.y + 10, 30f, Path.Direction.CW)
                parentCanvas.drawPath(path, getCurrentPaint())
            }
            MODE_STROKE_ERASER -> {
                path.lineTo(event.x, event.y)
                strokeEraserList.add(path)
            }
        }
    }

    private fun actionUp() {

        when (penMode) {
            MODE_PEN ->  strokePathList.add(path)
            MODE_STROKE_ERASER -> {

            }
        }

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


    fun convertUriToBitmap(uri: Uri) {
        val source = ImageDecoder.createSource(this.rootView.context.contentResolver, uri)
        imageBitmap = Bitmap.createBitmap(
            ImageDecoder.decodeBitmap(source)
        )

        invalidate()
    }

    companion object {
        const val MODE_PEN = 1
        const val MODE_AREA_ERASER = 2
        const val MODE_STROKE_ERASER = 3
        const val MODE_CLEAR_ALL = 5
    }
}

data class Test(
    var x: Int,
    var y: Int
)