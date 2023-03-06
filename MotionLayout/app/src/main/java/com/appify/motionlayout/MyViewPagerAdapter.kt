package com.appify.motionlayout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyViewPagerAdapter : RecyclerView.Adapter<MyViewPagerAdapter.MyViewPagerViewHolder>() {
    inner class MyViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewPage: TextView = itemView.findViewById(R.id.tv_eachPage)
        val imageViewPage: ImageView = itemView.findViewById(R.id.imageView_eachPage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewPagerViewHolder {
        return MyViewPagerViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_each_page,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewPagerViewHolder, position: Int) {
        when (position) {
            0 -> {
                holder.imageViewPage.setImageResource(R.drawable.img_page1)
                holder.textViewPage.text = "This is the first page"
            }
            1 -> {
                holder.imageViewPage.setImageResource(R.drawable.img_page2)
                holder.textViewPage.text = "This is the second page"
            }
            else -> {
                holder.imageViewPage.setImageResource(R.drawable.img_page3)
                holder.textViewPage.text = "This is the third page"
            }
        }
    }

    override fun getItemCount(): Int = 3
}