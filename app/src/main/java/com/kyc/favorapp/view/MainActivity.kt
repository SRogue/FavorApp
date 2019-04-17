package com.kyc.favorapp.view

import android.content.Context
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kyc.favorapp.R
import com.kyc.favorapp.model.DragTouchListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainFragment.OnFragmentInteractionListener, DragTouchListener {
    var fragments: ArrayList<Fragment>? = null

    private var mParams: ViewGroup.LayoutParams? = null

    private var currentbubbleView: DragBubbleView? = null

    private var mWindowManager: WindowManager? = null


    override fun beOnTouch() {
        currentbubbleView = DragBubbleView(this)
        val params = WindowManager.LayoutParams()
        params.format = PixelFormat.TRANSLUCENT
        params.type = params.type or WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
        mWindowManager!!.addView(currentbubbleView, params)


    }

    override fun lossTouch() {
        mWindowManager!!.removeView(currentbubbleView)
        currentbubbleView = null
    }

    override fun onFragmentInteraction(uri: Uri) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
        initListener()
    }

    private fun initData() {

        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        mParams = frame_layout.layoutParams


        fragments = arrayListOf(
            MainFragment.newInstance("first", "This is the first"),
            MainFragment.newInstance("second", "This is the second"),
            MainFragment.newInstance("third", "This is the third"),
            MainFragment.newInstance("fourth", "This is the fourth")
        )

        viewpager2.isUserInputEnabled = false
        viewpager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItem(position: Int): Fragment = fragments!![position]
            override fun getItemCount(): Int = fragments!!.size
        }

    }


    private fun initListener() {
        dragbubbleview.setDragTouchListener(this)
        rg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.first -> gotoPage(0)
                R.id.second -> gotoPage(1)
                R.id.third -> gotoPage(2)
                R.id.fourth -> gotoPage(3)
            }
        }
    }

    fun gotoPage(position: Int) {
        if (position < fragments!!.size) {
            viewpager2.setCurrentItem(position, false)
        }
    }


}
