package com.appify.motionlayout

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlin.math.nextUp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myViewPager: ViewPager2 =
            findViewById(R.id.viewPager_mainActivity); myViewPager.adapter = MyViewPagerAdapter()
        val myMotionLayout: MotionLayout = findViewById(R.id.layout_mainMotionLayout)
        val buttonNextPage: ImageButton = findViewById(R.id.button_nextPage)
        val progressIndicator: CircularProgressIndicator =
            findViewById(R.id.main_progressInidicator)

        buttonNextPage.setOnClickListener {
            if (myViewPager.currentItem != 2) {
                myViewPager.setCurrentItem(myViewPager.currentItem + 1, true)
            }
        }

        myViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                val currProgress = (position + positionOffset) / 2
                myMotionLayout.progress = currProgress
                progressIndicator.progress =
                    (((myViewPager.currentItem + 1) / 3f).nextUp() * 100).toInt()

                when (position) {
                    2 -> buttonNextPage.setImageResource(R.drawable.ic_done)
                    else -> buttonNextPage.setImageResource(R.drawable.ic_next)
                }
            }
        })
    }
}