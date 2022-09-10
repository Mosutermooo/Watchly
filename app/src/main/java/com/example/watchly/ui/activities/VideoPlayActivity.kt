package com.example.watchly.ui.activities

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.watchly.R
import com.example.watchly.adapters.NewVideoSuggestionsAdapter
import com.example.watchly.databinding.ActivityVideoPlayBinding
import com.example.watchly.models.Channel
import com.example.watchly.models.Video
import com.example.watchly.ui.dialogs.CommentsDialog
import com.example.watchly.ui.fragments.user_perspective_chanel.ChannelBottomSheet
import com.example.watchly.ui.viewmodels.ChannelViewModel
import com.example.watchly.ui.viewmodels.VideoViewModel
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource.getValueInDP
import com.example.watchly.uils.ReusableResource.uid
import com.example.watchly.use_cases.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.coroutines.flow.collect


class VideoPlayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoPlayBinding
    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var channelViewModel: ChannelViewModel
    private lateinit var videoViewModel: VideoViewModel
    private lateinit var videoTitle: TextView
    private lateinit var videoSettings: ImageButton
    private lateinit var videoClose: ImageButton
    private lateinit var fullScreen: ImageButton
    private lateinit var videoAdapter: NewVideoSuggestionsAdapter
    private lateinit var pg: ProgressBar
    private var isFullScreen = false
    private var isVideoLiked = false
    var isVideoDisliked = false
    var isSubscribed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoClose = findViewById(R.id.videoClose)
        videoSettings = findViewById(R.id.videoPlaySettings)
        videoTitle = findViewById(R.id.videoPlayTitle)
        fullScreen = findViewById(R.id.videoFullscreen)
        pg = findViewById(R.id.videoBuffer)

        val video = intent.getParcelableExtra<Video>("video")
        Log.e("video", "$video")
        Log.e("video id", "${video?.videoId}")

        videoViewModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
            .create(VideoViewModel::class.java)

        channelViewModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(application)
            .create(ChannelViewModel::class.java)
        if(video != null){
            setupLikes(video)
            getAllLikes(videoId = video.videoId.toString())
            isVideoAlreadyLiked(video.videoId.toString())
            isVideoAlreadyDislikedCheck(video.videoId.toString())
            getChannelData(video = video)
            setupPlayer(video)
            setupSubscribe(video)
            isAlreadySubscribed(video)
            getNewVideoData()
            getViews(video.videoId.toString())
            showChannel(video.channel.toString())
        }


        showComments(video)



        fullScreen.setOnClickListener {
            requestedOrientation = if(!isFullScreen){
                binding.player.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }else{
                binding.player.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    getValueInDP(this, 250F).toInt()
                )
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            isFullScreen = !isFullScreen
        }

        videoClose.setOnClickListener {
            exoPlayer.stop()
            finish()
        }



    }

    private fun getNewVideoData(){
        setupRecyclerView()
        videoViewModel.fetchNewSuggestionVideos()
        lifecycleScope.launchWhenStarted {
            videoViewModel.fetchNewSuggestionVideosState.collect {
                when(it){
                    is Resource.Error -> {
                        binding.videoProgressBar.visibility = View.GONE
                    }
                    is Resource.Loading -> {
                        binding.videoProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.videoProgressBar.visibility = View.GONE
                        videoAdapter.differ.submitList(it.data)
                        Log.e("Video 123123", "${it.data}")
                    }
                }
            }
        }
    }

    private fun setupRecyclerView(){
        videoAdapter = NewVideoSuggestionsAdapter()
        binding.newVideosSuggestionRV.apply {
            layoutManager = LinearLayoutManager(this@VideoPlayActivity)
            adapter = videoAdapter
        }



        videoAdapter.setOnVideoClickListener {newVideo ->
            exoPlayer.stop()
            setupLikes(newVideo)
            getAllLikes(videoId = newVideo.videoId.toString())
            isVideoAlreadyLiked(newVideo.videoId.toString())
            isVideoAlreadyDislikedCheck(newVideo.videoId.toString())
            getChannelData(video = newVideo)
            setupPlayer(newVideo)
            setupSubscribe(newVideo)
            isAlreadySubscribed(newVideo)
            getNewVideoData()
            showComments(newVideo)
            getViews(newVideo.videoId.toString())
            showChannel(newVideo.channel.toString())
        }




    }

    private fun getViews(videoId: String) {
        videoViewModel.getViewers(videoId)
        lifecycleScope.launchWhenStarted {
            videoViewModel.viewsState.collect {
                when(it){
                    is Resource.Success -> {
                        if(it.data != null && it.data.isNotEmpty()){
                            binding.VideoViews.text = "${ it.data.size.toString()} ${getString(R.string.views)}"
                        }else{
                            binding.VideoViews.text = "0"
                        }
                    }
                }
            }
        }
    }

    private fun showComments(video: Video?) {
        binding.comments.setOnClickListener {
            if(video != null){
                val commentsDialog = CommentsDialog(video.videoId.toString())
                commentsDialog.show(supportFragmentManager, "comments")
            }
        }
    }

    private fun showChannel(channelId: String){
       binding.channelImage.setOnClickListener {
           channelViewModel.getChannelData(channelId = channelId)
           channelViewModel.channelState.observe(this){
               when(it){
                   is Resource.Success -> {
                       val channel = it.data
                       if(channel != null){
                           if(channel.ownedBy != uid()){
                               ChannelBottomSheet(channel).show(supportFragmentManager, "channel")
                           }
                       }
                   }
               }
           }
       }
    }

    private fun setupPlayer(video: Video){
        exoPlayer = SimpleExoPlayer.Builder(this)
            .setSeekBackIncrementMs(5000)
            .setSeekForwardIncrementMs(5000).build()
        binding.player.player = exoPlayer
        binding.player.keepScreenOn = true
        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        pg.visibility = View.GONE
                    }
                    Player.STATE_BUFFERING -> {
                        pg.visibility = View.VISIBLE
                    }
                    else -> {
                        pg.visibility = View.GONE
                    }
                }
            }
        })

        Handler().postDelayed(
            {
                videoViewModel.addVideoView(video.videoId.toString())
            }, 5000
        )





        val videoSource = Uri.parse(video.video)
        val mediaItem = MediaItem.fromUri(videoSource)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()

    }

    private fun setupSubscribe(video: Video){
        binding.Subscribe.setOnClickListener {
            if(!isSubscribed){
                channelViewModel.subscribe(video.channel.toString(), video.user.toString())
                lifecycleScope.launchWhenStarted {
                    channelViewModel.subscribeState.collect {
                        when(it){
                           is Resource.Success -> {
                               alreadySubscribed()
                           }
                        }
                    }
                }
            }else{
                channelViewModel.unSubscribe(video.channel.toString())
                lifecycleScope.launchWhenStarted {
                    channelViewModel.unSubscribeState.collect {
                        when(it){
                            is Resource.Success -> {
                                notSubscribed()
                            }
                        }
                    }
                }
            }
            isSubscribed = !isSubscribed
        }
    }

    private fun isAlreadySubscribed(video: Video){
        channelViewModel.isAlreadySubscribed(video.channel.toString())
        lifecycleScope.launchWhenStarted {
            channelViewModel.isAlredySubscriber.collect {
                when(it){
                    is Resource.Error -> {
                        isSubscribed = false
                        notSubscribed()
                    }
                    is Resource.Success -> {
                        isSubscribed = true
                        alreadySubscribed()
                    }
                }
            }
        }
    }

    private fun setupLikes(video: Video){
        val likeVideo = LikeVideo()
        val dislikeVideo = DislikeVideo()
        binding.likeVideo.setOnClickListener {

            if(!isVideoLiked){
                likeVideo.like(video.videoId.toString(), uid())
                likeVideo.likeVideoState.observe(this){
                    when(it){
                        is Resource.Success -> {
                            binding.like.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_thumb_up_24))
                            isVideoDisliked = false
                        }
                    }
                }
            }else{
                likeVideo.removeLike(videoId = video.videoId.toString())
                likeVideo.removeLikeVideoState.observe(this){
                    binding.like.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_thumb_up_off_alt_24))
                    isVideoDisliked = true
                }
            }
            isVideoLiked = !isVideoLiked
        }

        binding.dislikeVideo.setOnClickListener {
            if(!isVideoDisliked){
                dislikeVideo.dislike(videoId = video.videoId.toString(), uid())
                dislikeVideo.dislikeVideoState.observe(this){
                    when(it){
                        is Resource.Success -> {
                            binding.dislike.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_thumb_down_alt_24))
                            isVideoLiked = false
                        }
                    }
                }
            }else{
                dislikeVideo.removeDislike(video.videoId.toString(), uid())
                dislikeVideo.dislikeVideoState.observe(this){
                    when(it){
                        is Resource.Success -> {
                            binding.dislike.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_thumb_down_off_alt_24))
                            isVideoLiked = true
                        }
                    }
                }
            }
            isVideoDisliked = !isVideoDisliked
        }
    }

    private fun getChannelData(video: Video?){
        channelViewModel.getChannelData(video?.user.toString())
        channelViewModel.channelState.observe(this){
            when(it){
                is Resource.Error -> {

                }
                is Resource.Success ->
                    if(it.data != null && video != null){
                        displayData(video, it.data)
                    }
            }
        }
    }

    private fun isVideoAlreadyLiked(videoId: String){
        val isVideoAlreadyLiked = IsVideoAlreadyLiked()
        isVideoAlreadyLiked.check(videoId)
        isVideoAlreadyLiked.alreadyLikedState.observe(this){
            when(it){
                is Resource.Error -> {
                    isVideoDisliked = false
                    Log.e("liked by", "${it.data}")
                    binding.like.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_thumb_up_off_alt_24))
                }
                is Resource.Success -> {
                    if(it.data != null){
                        isVideoDisliked = true
                        Log.e("liked by", "${it.data}")
                        binding.like.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_thumb_up_24))
                    }
                }
            }
        }
    }

    private fun isVideoAlreadyDislikedCheck(videoId: String){
        val isVideoAlreadyDisliked = IsVideoAlreadyDisliked()
        isVideoAlreadyDisliked.check(videoId)
        isVideoAlreadyDisliked.alreadyDislikedState.observe(this){
            when(it){
                is Resource.Error -> {
                    isVideoDisliked = false
                    Log.e("liked by", "${it.data}")
                    binding.dislike.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_thumb_down_off_alt_24))
                }
                is Resource.Success -> {
                    if(it.data != null){
                        isVideoDisliked = true
                        Log.e("liked by", "${it.data}")
                        binding.dislike.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_thumb_down_alt_24))
                    }
                }
            }
        }

    }

    private fun displayData(video: Video, channel:Channel){
        binding.VideoTitle.text = video.name
        binding.ChannelName.text = channel.name
        videoTitle.text = video.name
        Glide.with(this).load(channel.image)
            .placeholder(R.color.GrayTextColor)
            .centerCrop()
            .into(binding.channelImage)
        if(channel.ownedBy == uid()){
            binding.Subscribe.visibility = View.GONE
        }else{
            binding.Subscribe.visibility = View.VISIBLE
        }
    }

    private fun alreadySubscribed(){
        binding.Subscribe.setTextColor(Color.YELLOW)
    }


    private fun notSubscribed(){
        binding.Subscribe.setTextColor(Color.RED)
    }

    private fun getAllLikes(videoId: String){
        val allLikes = AllLikes()
        val allDislikes = AllDislikes()
        allLikes.get(videoId = videoId)
        allLikes.allLikesState.observe(this){
            when(it){
                is Resource.Error -> binding.AllLikes.text = it.message
                is Resource.Success -> {
                    if(it.data != null && it.data.isNotEmpty()){
                        Log.e("likes ", "${it.data.size}")
                        Log.e("likes ", "${it.data}")
                        binding.AllLikes.text = it.data.size.toString()
                    }else{
                        binding.AllLikes.text = "0"
                    }
                }
            }
        }
        allDislikes.get(videoId)
        allDislikes.allDislikesState.observe(this){
            when(it){
                is Resource.Error -> binding.allDislikes.text = it.message
                is Resource.Success -> {
                    if(it.data != null && it.data.isNotEmpty()){
                        binding.allDislikes.text = it.data.size.toString()
                    }else{
                        binding.allDislikes.text = "0"
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.stop()
    }



}
