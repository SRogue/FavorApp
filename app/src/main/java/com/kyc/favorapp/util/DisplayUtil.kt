package com.kyc.favorapp.util

import android.content.Context

/**
 * Created by AchillesL on 2016/11/16.
 */

object DisplayUtil {

    /*手柄起始角度*/
    const val ROTATION_INIT_NEEDLE = -30f

    /*截图屏幕宽高*/
    private const val BASE_SCREEN_WIDTH = 1080.0.toFloat()
    private const val BASE_SCREEN_HEIGHT = 1920.0.toFloat()

    /*唱针宽高、距离等比例*/
    const val SCALE_NEEDLE_WIDTH = (276.0 / BASE_SCREEN_WIDTH).toFloat()
    const val SCALE_NEEDLE_MARGIN_LEFT = (500.0 / BASE_SCREEN_WIDTH).toFloat()
    const val SCALE_NEEDLE_PIVOT_X = (43.0 / BASE_SCREEN_WIDTH).toFloat()
    const val SCALE_NEEDLE_PIVOT_Y = (43.0 / BASE_SCREEN_WIDTH).toFloat()
    const val SCALE_NEEDLE_HEIGHT = (413.0 / BASE_SCREEN_HEIGHT).toFloat()
    const val SCALE_NEEDLE_MARGIN_TOP = (43.0 / BASE_SCREEN_HEIGHT).toFloat()

    /*唱盘比例*/
    const val SCALE_DISC_SIZE = (813.0 / BASE_SCREEN_WIDTH).toFloat()
    const val SCALE_DISC_MARGIN_TOP = 190 / BASE_SCREEN_HEIGHT

    /*专辑图片比例*/
    const val SCALE_MUSIC_PIC_SIZE = (533.0 / BASE_SCREEN_WIDTH).toFloat()

    /*设备屏幕宽度*/
    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    /*设备屏幕高度*/
    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }


}
