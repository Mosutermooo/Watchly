package com.example.watchly.ui.fragments.video_uploading

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.watchly.R
import com.example.watchly.adapters.GalleryImageAdapter
import com.example.watchly.databinding.FragmentCustomThumbnailBinding
import com.example.watchly.ui.viewmodels.ChannelViewModel
import com.example.watchly.uils.ReusableResource
import com.example.watchly.use_cases.GalleryImages


class CustomThumbnail : Fragment() {


    private lateinit var binding: FragmentCustomThumbnailBinding
    private val args by navArgs<CustomThumbnailArgs>()
    private lateinit var viewAdapter : GalleryImageAdapter
    private var image : String? = null
    private lateinit var channelViewModel: ChannelViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCustomThumbnailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        channelViewModel =  ViewModelProvider.AndroidViewModelFactory(
            requireActivity().application
        ).create(ChannelViewModel::class.java)

        viewAdapter = GalleryImageAdapter()

        ReusableResource.setupToolBarOnBackPressed(
            binding.toolbar,
            getString(R.string.add_custom_thumbnail),
            null,
            requireActivity() as AppCompatActivity
        )

        image = "file://" + args.thumbnail
        displayImage(image!!)



        val imagesFromGallery = GalleryImages().get(requireActivity() as AppCompatActivity)
        viewAdapter.differ.submitList(imagesFromGallery)


        binding.ImagesFromGallery.apply {
            layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
            adapter = viewAdapter
        }

        viewAdapter.setOnClickListener {
            displayImage(it)
        }

    }

    private fun displayImage(image: String){
        Glide.with(this).load(image)
            .skipMemoryCache(false)
            .fitCenter()
            .placeholder(R.color.GrayTextColor)
            .into(binding.Thumbnail)
    }


}