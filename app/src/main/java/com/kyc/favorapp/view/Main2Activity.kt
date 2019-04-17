package com.kyc.favorapp.view

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.kyc.favorapp.R
import com.kyc.favorapp.model.DragTouchListener
import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : AppCompatActivity(), DragTouchListener {

    private var mWm: WindowManager? = null
    private var mParams: WindowManager.LayoutParams? = null

    override fun beOnTouch() {

        when (dragbubbleview.parent) {
            is ViewGroup -> {
                val viewGroup = dragbubbleview.parent as ViewGroup
                viewGroup!!.removeAllViews()
                mWm!!.addView(dragbubbleview, mParams)
            }
        }
    }

    override fun lossTouch() {

        mWm!!.removeView(dragbubbleview)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        initView()
        initListener()
        dragbubbleview.setDragTouchListener(this)
    }

    private fun initView() {
        mWm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mParams = WindowManager.LayoutParams()
        mParams!!.format = PixelFormat.TRANSLUCENT//使窗口支持透明度

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mParams!!.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        }
    }

    private fun initListener() {

    }
}
