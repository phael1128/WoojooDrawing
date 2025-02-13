package com.woojoo.tabletdrawing.customView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.createBitmap
import com.woojoo.tabletdrawing.R
import com.woojoo.tabletdrawing.SerializablePath
import com.woojoo.tabletdrawing.interfaces.SaveDrawingPictureListener
import com.woojoo.tabletdrawing.utils.getCropPaint
import com.woojoo.tabletdrawing.utils.getDrawingEraser
import com.woojoo.tabletdrawing.utils.getDrawingPen
import java.util.Hashtable
import kotlin.collections.ArrayList

class DrawingCanvas : AppCompatImageView {

    constructor(context: Context) : super(context) { init() }
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) { init() }
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) { init() }

    private var penMode  = 0
    private lateinit var drawingPaint: Paint
    private lateinit var areaEraserPaint: Paint
    private lateinit var cropPaint: Paint

    private var cropStartPoint: PointF? = null
    private var cropLastPoint: PointF? = null
    private var currentImageBitmap: Bitmap? = null
    private var parentBitmap: Bitmap? = null
    private var savedBitmap: Bitmap? = null
    private var parentCanvas: Canvas? = null
    private lateinit var savedCanvas: Canvas

    private var cropBitmap: Bitmap? = null

    private var path = SerializablePath()
    private var strokePathList = ArrayList<SerializablePath>()

    private val hashTable = Hashtable<XYCoordination, Options>()

    private lateinit var onSaveDrawingPictureListenerListener: SaveDrawingPictureListener

    // 초기화
    private fun init() {
        drawingPaint = getDrawingPen()
        areaEraserPaint = getDrawingEraser()
        cropPaint = getCropPaint()
        penMode = MODE_PEN
        this.setBackgroundColor(this.rootView.context.getColor(R.color.white))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        parentBitmap = createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        parentCanvas = getParentCanvas(parentBitmap!!)

        savedBitmap = createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        savedCanvas = Canvas(savedBitmap!!)
    }

    //실질적으로 그리기
    @SuppressLint("DrawAllocation")
    //이 canvas 는 항상 초기화 된 canvas
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (currentImageBitmap != null && !currentImageBitmap?.isRecycled!!) {
            currentImageBitmap?.recycle()
        }

        currentImageBitmap?.let {
            canvas.setBitmap(currentImageBitmap)
        }

        when (penMode) {
            MODE_CLEAR_ALL -> {
                parentCanvas?.drawBitmap(parentBitmap!!, 0f, 0f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.TRANSPARENT
                })
            }
            MODE_CROP -> {
                canvas.drawBitmap(parentBitmap!!, 0f, 0f, Paint(Paint.ANTI_ALIAS_FLAG))
                canvas.drawRect(cropStartPoint?.x!!, cropStartPoint?.y!!, cropLastPoint?.x!!, cropLastPoint?.y!!, getCurrentPaint())
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
            MotionEvent.ACTION_DOWN -> actionDown(event)
            MotionEvent.ACTION_MOVE -> actionMove(event)
            MotionEvent.ACTION_UP -> actionUp()
        }
        invalidate()
        return true
    }

    private fun actionDown(event: MotionEvent) {
        if (penMode == MODE_CROP) {
            cropStartPoint = PointF()
            cropLastPoint = PointF()
            cropStartPoint?.set(event.x, event.y)
        } else {
            path.moveTo(event.x, event.y)
        }
    }

    private fun actionMove(event: MotionEvent) {
        when (penMode) {
            MODE_PEN -> {
//                val key = XYCoordination(event.x, event.y)
//                hashTable[key] = Options(true)
                path.lineTo(event.x, event.y)
                parentCanvas?.drawPath(path, getCurrentPaint())
            }
            MODE_AREA_ERASER -> {
                path.reset()
                path.addCircle(event.x + 10, event.y + 10, 30f, Path.Direction.CW)
//                if (isPenArea(event.x, event.y)) {
//                    parentCanvas?.drawPath(path, getCurrentPaint())
//                }
                parentCanvas?.drawPath(path, getCurrentPaint())
            }
            MODE_CROP -> {
                cropLastPoint?.set(event.x, event.y)
            }
        }
    }

    private fun actionUp() {
        when (penMode) {
            MODE_PEN ->  strokePathList.add(path)
            MODE_CROP -> saveCropBitmap()
        }
        path = SerializablePath()
    }

    private fun getCurrentPaint(): Paint  = when(penMode) {
        MODE_PEN -> drawingPaint
        MODE_AREA_ERASER -> areaEraserPaint
        MODE_CROP -> cropPaint
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
            parentCanvas = getParentCanvas(parentBitmap!!)
            invalidate()
        }
    }

    fun setImageBitmap(uri: Uri) {
        val uriBitmap = ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(this.rootView.context.contentResolver, uri)
        ) { decoder: ImageDecoder, _: ImageDecoder.ImageInfo?, _: ImageDecoder.Source? ->
            decoder.isMutableRequired = true
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }
        val convertBitmap = Bitmap.createScaledBitmap(uriBitmap, 600, 600, false)
        // 비트맵 로딩 하기전 hashTable 에 넣기
        parentCanvas?.let {
            it.drawBitmap(convertBitmap, 0f, 0f, Paint(Paint.ANTI_ALIAS_FLAG))
        }
    }

    fun saveDrawing() {
        savedBitmap = parentBitmap
        savedBitmap?.let { bitmap ->
            savedCanvas.drawBitmap(bitmap, 0f, 0f, Paint(Paint.ANTI_ALIAS_FLAG))
            onSaveDrawingPictureListenerListener.onSave(bitmap)
        }
    }
    private fun saveCropBitmap() {
        val cropWidthSize = cropLastPoint?.x!!.coerceAtLeast(cropStartPoint?.x!!) - cropLastPoint?.x!!.coerceAtMost(cropStartPoint?.x!!)
        val cropHeightSize = cropLastPoint?.y!!.coerceAtLeast(cropStartPoint?.y!!) - cropLastPoint?.y!!.coerceAtMost(cropStartPoint?.y!!)
        if (cropBitmap != null) cropBitmap?.recycle()
        //createBitmap(@NonNull Bitmap source, int x, int y, int width, int height)
        cropBitmap = Bitmap.createBitmap(parentBitmap!!, cropStartPoint?.x!!.toInt(), cropStartPoint?.y!!.toInt(), cropWidthSize.toInt(), cropHeightSize.toInt())
        // 기존 비트맵을 기준으로 x, y 좌표값에서 부터 width, height 만큼 따오는 느낌
        // 마치 누끼 따는것 처럼
        onSaveDrawingPictureListenerListener.onSave(cropBitmap!!)

    }

    fun setSavePictureListener(listener: SaveDrawingPictureListener) {
        onSaveDrawingPictureListenerListener = listener
    }

    private fun getParentCanvas(bitmap: Bitmap) = Canvas(bitmap).apply {
        drawColor(Color.WHITE)
    }

    private fun isPenArea(x: Float, y: Float): Boolean {
        val key = XYCoordination(x, y)
        val value = hashTable.getOrDefault(key, null)

        Log.d("isPenArea : ", "${key.hashCode()} $value")
        return value != null && value.isPenLine
    }

    fun isImageArea(x: Float, y: Float): Boolean {
        val currentCoordinates = XYCoordination(x, y)
        /*
        * 좌표를 탐색해서 이미지를 찾는다.
        * */
        val isBitmapArea = hashTable.getOrDefault(currentCoordinates, null)
        return isBitmapArea != null
    }

    companion object {
        const val MODE_PEN = 1
        const val MODE_AREA_ERASER = 2
        const val MODE_CROP = 3
        const val MODE_CLEAR_ALL = 4
    }
}