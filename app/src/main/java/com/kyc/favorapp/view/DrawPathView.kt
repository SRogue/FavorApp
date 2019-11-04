package com.kyc.favorapp.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.min

class DrawPathView :
    View {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private lateinit var bitmap: Bitmap
    private lateinit var mCanvas: Canvas

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        canvas?.drawBitmap(bitmap, 0f, 0f, null)
        canvas?.drawPath(mPath, mPaint)
        Log.e("ondraw", "is drawing")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444)
        mCanvas = Canvas(bitmap)
    }


    private val mPaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        strokeWidth = 10f
        style = Paint.Style.STROKE
        isDither = true
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND

    }

    private var mPath = Path()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var wide = 10
        var height = 10

        var wideMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val wideSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        when (wideMode) {
            MeasureSpec.EXACTLY -> {
                wide = wideSize
            }

            MeasureSpec.AT_MOST -> {
                wide = min(100, wideSize)
            }

            MeasureSpec.UNSPECIFIED -> {
                wide = 10
            }
        }

        when (heightMode) {
            MeasureSpec.EXACTLY -> {
                height = heightSize
            }

            MeasureSpec.AT_MOST -> {
                height = min(100, heightSize)

            }

            MeasureSpec.UNSPECIFIED -> {
                height = 10
            }
        }

        setMeasuredDimension(wide, height)

    }

    private var lastX: Float = 0f
    private var lastY: Float = 0f
    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y
                mPath.moveTo(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                if (abs(lastX - event.x) < 3 || abs(lastY - event.y) < 3) {
                    mPath.lineTo(event.x, event.y)
                } else {
                    mPath.quadTo(lastX, lastY, (event.x + lastX) / 2, (event.y + lastY) / 2)  //贝塞尔曲线优化抗锯齿
                }
                lastX = event.x
                lastY = event.y
            }

        }
        invalidate()
        return true
    }


    fun cleanPaint() {
        mPath.reset()
        invalidate()
    }


}