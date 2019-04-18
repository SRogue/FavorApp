package com.kyc.favorapp.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.PointFEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.View.MeasureSpec.getMode
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import com.kyc.favorapp.R
import com.kyc.favorapp.model.DragTouchListener

/**
 * QQ气泡效果
 * copy by oliver from netEast product!!
 */
class DragBubbleView constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        View(context, attrs, defStyleAttr) {
    private var mScreenWidth = 0
    private var mScreenHeight = 0
    /**
     * 气泡默认状态--静止
     */
    private val BUBBLE_STATE_DEFAULT = 0
    /**
     * 气泡相连
     */
    private val BUBBLE_STATE_CONNECT = 1
    /**
     * 气泡分离
     */
    private val BUBBLE_STATE_APART = 2
    /**
     * 气泡消失
     */
    private val BUBBLE_STATE_DISMISS = 3

    /**
     * 气泡半径
     */
    var mBubbleRadius: Float = 0.toFloat()
    /**
     * 气泡颜色
     */
    var mBubbleColor: Int = Color.RED
    /**
     * 气泡消息文字
     */
    var mTextStr: String? = "99"
    /**
     * 气泡消息文字颜色
     */
    private var mTextColor: Int = 0
    /**
     * 气泡消息文字大小
     */
    private var mTextSize: Float = 0.toFloat()
    /**
     * 不动气泡的半径
     */
    private var mBubFixedRadius: Float = 0.toFloat()
    /**
     * 可动气泡的半径
     */
    private var mBubMovableRadius: Float = 0.toFloat()
    /**
     * 不动气泡的圆心
     */
    private var mBubFixedCenter: PointF? = null
    /**
     * 可动气泡的圆心
     */
    private var mBubMovableCenter: PointF? = null
    /**
     * 气泡的画笔
     */
    private var mBubblePaint: Paint? = null
    /**
     * 贝塞尔曲线path
     */
    private var mBezierPath: Path? = null

    private var mTextPaint: Paint? = null

    //文本绘制区域
    private var mTextRect: Rect? = null

    private var mBurstPaint: Paint? = null

    //爆炸绘制区域
    private var mBurstRect: Rect? = null

    /**
     * 气泡状态标志
     */
    private var mBubbleState = BUBBLE_STATE_DEFAULT
    /**
     * 两气泡圆心距离
     */
    private var mDist: Float = 0.toFloat()
    /**
     * 气泡相连状态最大圆心距离
     */
    private var mMaxDist: Float = 0.toFloat()
    /**
     * 手指触摸偏移量
     */
    private var MOVE_OFFSET: Float = 0.toFloat()

    /**
     * 气泡爆炸的bitmap数组
     */
    private var mBurstBitmapsArray: ArrayList<Bitmap>? = ArrayList()
    /**
     * 是否在执行气泡爆炸动画
     */
    private val mIsBurstAnimStart = false

    /**
     * 当前气泡爆炸图片index
     */
    var mCurDrawableIndex: Int = 0


    private var dragListener: DragTouchListener? = null

    /**
     * 气泡爆炸的图片id数组
     */
    private val mBurstDrawablesArray =
            intArrayOf(R.mipmap.burst_1, R.mipmap.burst_2, R.mipmap.burst_3, R.mipmap.burst_4, R.mipmap.burst_5)

    init {
        mBubbleRadius = TypedValue.complexToFloat(10)
        mBubbleColor = Color.RED
        mTextStr = "99"
        Log.e("787888", "mTextRect = init")

        mTextSize = TypedValue.complexToFloat(12)
        mTextColor = Color.WHITE

        initBase(context, attrs, defStyleAttr)
    }

    constructor(context: Context, listener: DragTouchListener) : this(context, null) {
        dragListener = listener
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private fun initBase(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.DragBubbleView, defStyleAttr, 0).apply {
            mBubbleRadius = getDimension(R.styleable.DragBubbleView_bubble_radius, mBubbleRadius)
            mBubbleColor = getColor(R.styleable.DragBubbleView_bubble_color, Color.RED)
            mTextStr = getString(R.styleable.DragBubbleView_bubble_text)
            Log.e("787888", "mTextRect = initBase")

            mTextSize = getDimension(R.styleable.DragBubbleView_bubble_textSize, mTextSize)
            mTextColor = getColor(R.styleable.DragBubbleView_bubble_textColor, Color.WHITE)
            recycle()
        }


        //两个圆半径大小一致
        mBubFixedRadius = mBubbleRadius
        mBubMovableRadius = mBubFixedRadius
        mMaxDist = 8 * mBubbleRadius

        MOVE_OFFSET = mMaxDist / 4

        //抗锯齿
        mBubblePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = mBubbleColor
            style = Paint.Style.FILL
        }

        mBezierPath = Path()

        //文本画笔
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = mTextColor
            textSize = mTextSize
        }

        mTextRect = Rect()

        //爆炸画笔
        mBurstPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isFilterBitmap = true
        }

        mBurstRect = Rect()

        for (bitmapRes in mBurstDrawablesArray) {
            //将气泡爆炸的drawable转为bitmap
            val bitmap = BitmapFactory.decodeResource(resources, bitmapRes)
            mBurstBitmapsArray!!.add(bitmap)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.e("00000", "onsizeChange")

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 宽度测量
        val widthMode = getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        when (widthMode) {
            MeasureSpec.AT_MOST -> mScreenWidth = Math.min(widthSize, 100)
            MeasureSpec.EXACTLY -> mScreenWidth = widthSize
            MeasureSpec.UNSPECIFIED -> mScreenWidth = 100
        }

        // 高度测量
        val heightMode = getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        when (heightMode) {
            MeasureSpec.AT_MOST -> mScreenHeight = Math.min(heightSize, 100)
            MeasureSpec.EXACTLY -> mScreenHeight = heightSize
            MeasureSpec.UNSPECIFIED -> mScreenHeight = 100
        }
        setMeasuredDimension(mScreenWidth, mScreenHeight)

        init(mScreenWidth, mScreenHeight)
    }

    private fun init(w: Int, h: Int) {
        mBubbleState = BUBBLE_STATE_DEFAULT

        //设置固定气泡圆心初始坐标

        if (mBubFixedCenter == null) {
            mBubFixedCenter = PointF((w / 2).toFloat(), (h / 2).toFloat())
        } else {
            mBubFixedCenter!!.set((w / 2).toFloat(), (h / 2).toFloat())

        }
        Log.e("wwww", "mBubFixedCenter.x = ${mBubFixedCenter?.x}   mBubFixedCenter.y = ${mBubFixedCenter?.y}")
        //设置可动气泡圆心初始坐标
        if (mBubMovableCenter == null) {
            mBubMovableCenter = PointF((w / 2).toFloat(), (h / 2).toFloat())
        } else {
            mBubMovableCenter!!.set((w / 2).toFloat(), (h / 2).toFloat())
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //1. 连接情况绘制贝塞尔曲线  2.绘制圆背景以及文本 3.另外端点绘制一个圆
        //1. 静止状态  2，连接状态 3，分离状态  4，消失


        if (mBubbleState == BUBBLE_STATE_CONNECT) {
            //绘制静止的气泡
            canvas.drawCircle(mBubFixedCenter!!.x, mBubFixedCenter!!.y, mBubFixedRadius, mBubblePaint!!)
            //计算控制点的坐标
            val iAnchorX = ((mBubMovableCenter!!.x + mBubFixedCenter!!.x) / 2).toInt()
            val iAnchorY = ((mBubMovableCenter!!.y + mBubFixedCenter!!.y) / 2).toInt()

            val sinTheta = (mBubMovableCenter!!.y - mBubFixedCenter!!.y) / mDist
            val cosTheta = (mBubMovableCenter!!.x - mBubFixedCenter!!.x) / mDist

            //D
            val iBubFixedStartX = mBubFixedCenter!!.x - mBubFixedRadius * sinTheta
            val iBubFixedStartY = mBubFixedCenter!!.y + mBubFixedRadius * cosTheta
            //C
            val iBubMovableEndX = mBubMovableCenter!!.x - mBubMovableRadius * sinTheta
            val iBubMovableEndY = mBubMovableCenter!!.y + mBubMovableRadius * cosTheta

            //A
            val iBubFixedEndX = mBubFixedCenter!!.x + mBubFixedRadius * sinTheta
            val iBubFixedEndY = mBubFixedCenter!!.y - mBubFixedRadius * cosTheta
            //B
            val iBubMovableStartX = mBubMovableCenter!!.x + mBubMovableRadius * sinTheta
            val iBubMovableStartY = mBubMovableCenter!!.y - mBubMovableRadius * cosTheta

            mBezierPath!!.reset()
            mBezierPath!!.moveTo(iBubFixedStartX, iBubFixedStartY)
            mBezierPath!!.quadTo(iAnchorX.toFloat(), iAnchorY.toFloat(), iBubMovableEndX, iBubMovableEndY)

            mBezierPath!!.lineTo(iBubMovableStartX, iBubMovableStartY)
            mBezierPath!!.quadTo(iAnchorX.toFloat(), iAnchorY.toFloat(), iBubFixedEndX, iBubFixedEndY)
            mBezierPath!!.close()
            canvas.drawPath(mBezierPath!!, mBubblePaint!!)
        }

        //静止，连接，分离状态都需要绘制圆背景以及文本
        if (mBubbleState != BUBBLE_STATE_DISMISS) {
            canvas.drawCircle(mBubMovableCenter!!.x, mBubMovableCenter!!.y, mBubMovableRadius, mBubblePaint!!)
            Log.e("787888", "mtextpaint = null" + (mTextPaint == null))
            Log.e("787888", "mTextStr = null" + (mTextStr == null))
            Log.e("787888", "mTextRect = null" + (mTextRect == null))
            mTextPaint!!.getTextBounds(mTextStr, 0, mTextStr!!.length, mTextRect)
            canvas.drawText(
                    mTextStr!!,
                    mBubMovableCenter!!.x - mTextRect!!.width() / 2,
                    mBubMovableCenter!!.y + mTextRect!!.height() / 2,
                    mTextPaint!!
            )
        }

        // 认为是消失状态，执行爆炸动画
        if (mBubbleState == BUBBLE_STATE_DISMISS && mCurDrawableIndex < mBurstBitmapsArray!!.size) {
            mBurstRect!!.set(
                    (mBubMovableCenter!!.x - mBubMovableRadius).toInt(),
                    (mBubMovableCenter!!.y - mBubMovableRadius).toInt(),
                    (mBubMovableCenter!!.x + mBubMovableRadius).toInt(),
                    (mBubMovableCenter!!.y + mBubMovableRadius).toInt()
            )
            canvas.drawBitmap(mBurstBitmapsArray!![mCurDrawableIndex], null, mBurstRect!!, mBubblePaint)
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                dragListener!!.beOnTouch()
                Log.e("9999", "ontouch down   dragListener  = $dragListener")
                if (mBubbleState != BUBBLE_STATE_DISMISS) {
                    mDist =
                            Math.hypot(
                                    (event.x - mBubFixedCenter!!.x).toDouble(),
                                    (event.y - mBubFixedCenter!!.y).toDouble()
                            )
                                    .toFloat()
                    mBubbleState = if (mDist < mBubbleRadius + MOVE_OFFSET) {
                        //加上MOVE_OFFSET是为了方便拖拽
                        BUBBLE_STATE_CONNECT
                    } else {
                        BUBBLE_STATE_DEFAULT
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> if (mBubbleState != BUBBLE_STATE_DEFAULT) {
                mDist =
                        Math.hypot((event.x - mBubFixedCenter!!.x).toDouble(), (event.y - mBubFixedCenter!!.y).toDouble())
                                .toFloat()
                mBubMovableCenter!!.x = event.x
                mBubMovableCenter!!.y = event.y
                if (mBubbleState == BUBBLE_STATE_CONNECT) {
                    if (mDist < mMaxDist - MOVE_OFFSET) {
                        mBubFixedRadius = mBubbleRadius - mDist / 8
                    } else {
                        mBubbleState = BUBBLE_STATE_APART
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> if (mBubbleState == BUBBLE_STATE_CONNECT) {
                // 橡皮筋动画
                startBubbleRestAnim()
            } else if (mBubbleState == BUBBLE_STATE_APART) {
                if (mDist < 2 * mBubbleRadius) {
                    //反弹动画
                    startBubbleRestAnim()
                } else {
                    // 爆炸动画
                    startBubbleBurstAnim()
                }
            }
        }
        return true
    }

    /**
     * 连接状态下松开手指，执行类似橡皮筋动画
     */
    private fun startBubbleRestAnim() {
        val anim = ValueAnimator.ofObject(
                PointFEvaluator(),
                PointF(mBubMovableCenter!!.x, mBubMovableCenter!!.y),
                PointF(mBubFixedCenter!!.x, mBubFixedCenter!!.y)
        )
        anim.duration = 200
        anim.interpolator = OvershootInterpolator(5f)
        anim.addUpdateListener { animation ->
            mBubMovableCenter = animation.animatedValue as PointF
            invalidate()
        }
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                mBubbleState = BUBBLE_STATE_DEFAULT
                dragListener!!.lossTouch()

            }
        })
        anim.start()

    }

    /**
     * 爆炸动画
     */
    private fun startBubbleBurstAnim() {
        //将气泡改成消失状态
        mBubbleState = BUBBLE_STATE_DISMISS
        val animator = ValueAnimator.ofInt(0, mBurstBitmapsArray!!.size)
        animator.interpolator = LinearInterpolator()
        animator.duration = 500
        animator.addUpdateListener { animation ->
            mCurDrawableIndex = animation.animatedValue as Int
            invalidate()
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                dragListener!!.lossTouch()

            }
        })

        animator.start()
    }

    fun setDragTouchListener(listener: DragTouchListener) {
        dragListener = listener
    }

}
