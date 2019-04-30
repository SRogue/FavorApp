package com.kyc.favorapp.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.animation.AccelerateInterpolator
import android.widget.RelativeLayout
import com.kyc.favorapp.R


/**
 * Created by AchillesL on 2016/11/18.
 */
/**
 * 自定义一个控件，继承RelativeLayout
 */
class BackgourndAnimationRelativeLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val DURATION_ANIMATION = 500
    private val INDEX_BACKGROUND = 0
    private val INDEX_FOREGROUND = 1
    /**
     * LayerDrawable[0]: background drawable
     * LayerDrawable[1]: foreground drawable
     */
    private var layerDrawable: LayerDrawable? = null
    private var objectAnimator: ObjectAnimator? = null
    private val musicPicRes = -1

    init {
        initLayerDrawable()
        initObjectAnimator()
    }

    private fun initLayerDrawable() {
        val backgroundDrawable = context.getDrawable(R.drawable.ic_blackground)
        val drawables = arrayOfNulls<Drawable>(2)

        /*初始化时先将前景与背景颜色设为一致*/
        drawables[INDEX_BACKGROUND] = backgroundDrawable
        drawables[INDEX_FOREGROUND] = backgroundDrawable

        layerDrawable = LayerDrawable(drawables)
    }

    private fun initObjectAnimator() {
        objectAnimator = ObjectAnimator.ofFloat(this, "number", 0f, 1.0f)
        objectAnimator!!.duration = DURATION_ANIMATION.toLong()
        objectAnimator!!.interpolator = AccelerateInterpolator()
        objectAnimator!!.addUpdateListener { animation ->
            val foregroundAlpha = (animation.animatedValue as Float * 255).toInt()
            /*动态设置Drawable的透明度，让前景图逐渐显示*/
            layerDrawable!!.getDrawable(INDEX_FOREGROUND).alpha = foregroundAlpha
            this@BackgourndAnimationRelativeLayout.background = layerDrawable
        }
        objectAnimator!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                /*动画结束后，记得将原来的背景图及时更新*/
                layerDrawable!!.setDrawable(
                    INDEX_BACKGROUND, layerDrawable!!.getDrawable(
                        INDEX_FOREGROUND
                    )
                )
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
    }

    override fun setForeground(drawable: Drawable) {
        layerDrawable!!.setDrawable(INDEX_FOREGROUND, drawable)
    }

    //对外提供方法，用于开始渐变动画
    fun beginAnimation() {
        objectAnimator!!.start()
    }

    fun isNeed2UpdateBackground(musicPicRes: Int): Boolean {
        if (this.musicPicRes == -1) return true
        return if (musicPicRes != this.musicPicRes) {
            true
        } else false
    }
}
