package com.kyc.favorapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


class AsyncDiffAdapter(private val datas: ArrayList<Int>) :
    RecyclerView.Adapter<AsyncDiffAdapter.AsyncDiffViewholder>() {
    private val mDiffer: AsyncListDiffer<Int> by lazy {
        AsyncListDiffer<Int>(this, diffCallback)
    }

    init {
        mDiffer.submitList(datas)
    }

    override fun onBindViewHolder(holder: AsyncDiffViewholder, position: Int) {
        holder.textview.text = mDiffer.currentList[position].toString()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsyncDiffViewholder {
        val view = LayoutInflater.from(parent.context).inflate(com.kyc.favorapp.R.layout.item_async_adapter, null)
        return AsyncDiffViewholder(view)
    }

    override fun getItemCount(): Int {
        if (mDiffer.currentList.isNotEmpty()) {
            return mDiffer.currentList.size
        }
        return 0
    }


    class AsyncDiffViewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val textview: TextView = itemview.findViewById(com.kyc.favorapp.R.id.async_text)
    }


    fun updataData(datas: ArrayList<Int>) {
        mDiffer.submitList(datas)
    }

    fun getCurrentList(): ArrayList<Int> = ArrayList<Int>(mDiffer.currentList)

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<Int>() {
            override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
                return oldItem == newItem
            }
        }
    }


}