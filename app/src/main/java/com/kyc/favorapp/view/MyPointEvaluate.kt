package com.kyc.favorapp.view

import android.animation.TypeEvaluator
import android.graphics.PointF

class MyPointEvaluate constructor(private val mPoint: PointF? = null) : TypeEvaluator<PointF> {
    constructor() : this(PointF()){}

    override fun evaluate(fraction: Float, startValue: PointF?, endValue: PointF?): PointF {
        val x = startValue!!.x + fraction * (endValue!!.x - startValue!!.x)
        val y = startValue!!.y + fraction * (endValue!!.y - startValue!!.y)

        return if (mPoint != null) {
            mPoint.x = x
            mPoint.y = y
            mPoint
        } else {
            PointF(x, y)
        }
    }
}