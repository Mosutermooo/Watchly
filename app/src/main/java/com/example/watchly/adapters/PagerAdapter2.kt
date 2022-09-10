package com.example.watchly.adapters

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.watchly.ui.channel_fragments.About
import com.example.watchly.ui.channel_fragments.ChannelHome
import com.example.watchly.ui.channel_fragments.Videos
import com.example.watchly.ui.fragments.user_perspective_chanel.AboutChannel
import com.example.watchly.ui.fragments.user_perspective_chanel.ChannelVideos
import com.example.watchly.ui.fragments.user_perspective_chanel.UserPrespChannelHome

class PagerAdapter2 (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UserPrespChannelHome()
            1 -> ChannelVideos()
            2 -> AboutChannel()
            else -> {
                throw Resources.NotFoundException()
            }
        }
    }
}