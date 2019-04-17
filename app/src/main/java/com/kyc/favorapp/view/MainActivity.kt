package com.kyc.favorapp.view

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kyc.favorapp.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var fragments: ArrayList<Fragment>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initListener()
        initData()
    }

    private fun initData() {

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
