package com.example.watchly.adapters

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.watchly.ui.channel_fragments.About
import com.example.watchly.ui.channel_fragments.ChannelHome
import com.example.watchly.ui.channel_fragments.Videos

class PagerAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ChannelHome()
            1 -> Videos()
            2 -> About()
            else -> {
                throw Resources.NotFoundException()
            }
        }
    }
}