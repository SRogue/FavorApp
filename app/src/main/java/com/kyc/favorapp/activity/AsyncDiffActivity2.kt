package com.kyc.favorapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.kyc.favorapp.R
import com.kyc.favorapp.adapter.AsyncDiffAdapter
import kotlinx.android.synthetic.main.activty_async_diff.*
import kotlin.random.Random

class AsyncDiffActivity2 : AppCompatActivity(), View.OnClickListener {
    private val mDatas: ArrayList<Int> by lazy { ArrayList<Int>() }

    private val adapters: AsyncDiffAdapter by lazy { AsyncDiffAdapter(mDatas) }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.change_bt -> {

            }

            R.id.add_bt -> {
                adapters.updataData(ArrayList<Int>().apply {
                    addAll(adapters.getCurrentList())
                    add(adapters.getCurrentList()[adapters.getCurrentList().size - 1] + 1)
                })

            }
            R.id.delete_bt -> {
                val currentList = adapters.getCurrentList()
                adapters.updataData(ArrayList<Int>().apply {
                    addAll(currentList)
                    if (this.size > 0) {
                        if (this.size == 1) {
                            remove(this[0])
                        } else {
                            remove(this[Random(1).nextInt(0, this.size - 1)])
                        }
                    }
                })

            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activty_async_diff)
        for (i in 1..10) {
            mDatas.add(i)
        }
        async_recyclerview.adapter = adapters
        async_recyclerview.layoutManager = LinearLayoutManager(this)
        async_recyclerview.itemAnimator?.apply {
            changeDuration = 0
            moveDuration = 0
            removeDuration = 0
            (this as SimpleItemAnimator).supportsChangeAnimations = false

        }
        change_bt.setOnClickListener(this)
        add_bt.setOnClickListener(this)
        delete_bt.setOnClickListener(this)
        testJump()
    }

    private fun testJump() {
        stander_model.text = "stander   999$taskId"


        toStander.setOnClickListener { startActivity(Intent(this, AsyncDiffActivity2::class.java)) }

        toSingleTop.setOnClickListener { startActivity(Intent(this, AsyncDiffActivity::class.java)) }
    }

    companion object {
        fun intentMe(context: Context) {
            context.startActivity(Intent(context, AsyncDiffActivity2::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }


}