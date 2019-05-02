package com.bigthinkapps.pipebuilding.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.bigthinkapps.pipebuilding.extension.ifNotNull
import com.bigthinkapps.pipebuilding.model.SectionLine


enum class TypePipeline(val resId: Int) {
    HYDRO(Color.BLUE),
    GAS(Color.parseColor("#FF9933")),
    SANITARY(Color.YELLOW);
}

class FingerLine : View {

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintLines: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var startX: Float = 0.toFloat()
    private var startY: Float = 0.toFloat()
    private var endX: Float = 0.toFloat()
    private var endY: Float = 0.toFloat()

    var measureHeight = 0.0
    var measureWidth = 0.0

    private lateinit var canvasBitmap: Bitmap
    private lateinit var drawCanvas: Canvas
    private lateinit var canvasBitmapLines: Bitmap
    private lateinit var drawCanvasLines: Canvas

    lateinit var addMeasurePipeline: (Double) -> Unit

    private var isUndo = false
    var isEditable = true

    private val sectionLineList = ArrayList<SectionLine>()

    private var isDownPipe = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = 10f
        paint.color = Color.BLUE

        paintLines.style = Paint.Style.STROKE
        paintLines.strokeWidth = 0.5f
        paintLines.color = Color.GRAY
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap)
        canvasBitmapLines = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvasLines = Canvas(canvasBitmapLines)
        drawVerticalLines(drawCanvasLines)
        drawHorizontalLines(drawCanvasLines)
    }

    fun setTypePipeline(typePipeline: TypePipeline) {
        paint.color = typePipeline.resId
    }

    fun setStrokePipeline(stroke: Int) {
        paint.strokeWidth = stroke.toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(canvasBitmapLines, 0f, 0f, paint)
        canvas?.drawBitmap(canvasBitmap, 0f, 0f, paint)

        if (isUndo) {
            isUndo = false
        } else {
            if (isEditable && endX != 0.0f && endY != 0.0f) {
                canvas?.drawLine(startX, startY, endX, endY, paint)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean {
        motionEvent.ifNotNull { event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = if (endX == 0.toFloat()) event.x else endX
                    startY = if (endY == 0.toFloat()) event.y else endY
                    endX = event.x
                    endY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!isDownPipe) {
                        endX = event.x
                        endY = event.y
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (isDownPipe) {
                        drawCanvas.drawCircle(event.x, event.y, 10f, paint)
                        isDownPipe = false
                    } else {
                        endX = event.x
                        endY = event.y
                        sectionLineList.add(SectionLine(startX, startY, endX, endY, paint.color))
                        drawSections()
                        isEditable = false
                        addMeasurePipeline(getDistance())
                    }
                }
            }
        }
        invalidate()
        return true
    }

    private fun drawSections() {
        sectionLineList.forEach {
            paint.color = it.color
            drawCanvas.drawLine(it.startX, it.startY, it.endX, it.endY, paint)
        }
    }

    fun undoSection() {
        if (sectionLineList.isNotEmpty()) {
            sectionLineList.removeAt(sectionLineList.lastIndex)
            drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            isUndo = true
            drawSections()
            invalidate()
        }
    }

    private fun getDistance(): Double {
        val dx = (startX - endX) * measureWidth
        val dy = (startY - endY) * measureHeight
        return Math.sqrt(((dx * dx) + (dy * dy)))
    }

    fun initPipeline() {
        endX = 0.toFloat()
        endY = 0.toFloat()
    }

    fun setDownPipe() {
        isDownPipe = true
    }

    private fun drawVerticalLines(canvas: Canvas) {
        val widthCanvas = width / 15f
        for (i in 1 until widthCanvas.toInt()) {
            val partitionX = i / widthCanvas
            canvas.drawLine(width * partitionX, 0f, width * partitionX, height.toFloat(), paintLines)
        }
    }

    private fun drawHorizontalLines(canvas: Canvas) {
        val heightCanvas = height / 15f
        for (i in 1 until heightCanvas.toInt()) {
            val partitionY = i / heightCanvas
            canvas.drawLine(0f, height * partitionY, width.toFloat(), height * partitionY, paintLines)
        }
    }
}