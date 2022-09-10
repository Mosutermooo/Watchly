package com.example.watchly.ui.fragments.video_uploading

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.watchly.R
import com.example.watchly.databinding.FragmentSelectedVideoViewBinding
import com.example.watchly.models.GalleryVideo
import com.example.watchly.uils.ReusableResource
import com.example.watchly.uils.ReusableResource.hideLoadingDialog
import com.example.watchly.uils.ReusableResource.initLoadingDialog
import com.example.watchly.uils.ReusableResource.showLoadingDialog
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import java.io.File

class SelectedVideoView : Fragment() {

    private lateinit var binding: FragmentSelectedVideoViewBinding
    private lateinit var exoPlayer: SimpleExoPlayer
    private val args by navArgs<SelectedVideoViewArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSelectedVideoViewBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val galleryVideo: GalleryVideo = args.galleryVideo
        initLoadingDialog()
        val toolbar = view.findViewById<Toolbar>(R.id.toolbarGalleryVideoPlayer)
        ReusableResource.setupToolBarOnBackPressed(
            toolbar,
            null,null,
            requireActivity() as AppCompatActivity
        )

        binding.btnContinue.setOnClickListener {
            val action = SelectedVideoViewDirections.actionSelectedVideoViewToUploadVideo(galleryVideo)
            findNavController().navigate(action)
        }

        exoPlayer = SimpleExoPlayer.Builder(requireContext()).build()
        binding.player.player = exoPlayer
        binding.player.keepScreenOn = true
        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        showLoadingDialog()
                    }
                    else -> {
                        hideLoadingDialog()
                    }
                }
            }
        })
        val videoSource = Uri.parse(galleryVideo.absolutePath)
        val mediaItem = MediaItem.fromUri(videoSource)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        exoPlayer.stop()
    }

}