package com.example.watchly.ui.fragments.video_uploading

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.watchly.R
import com.example.watchly.adapters.GalleryVideoAdapter
import com.example.watchly.databinding.FragmentSelectAnVideoBinding
import com.example.watchly.models.GalleryVideo
import com.example.watchly.ui.activities.VideoUploadActivity
import com.example.watchly.uils.ReusableResource
import com.example.watchly.uils.ReusableResource.getPath
import com.example.watchly.uils.ReusableResource.openGallery
import com.example.watchly.uils.ReusableResource.videoGallery
import com.example.watchly.use_cases.GalleryVideos
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.util.jar.Manifest

class SelectAnVideo : Fragment() {

    private lateinit var binding: FragmentSelectAnVideoBinding
    private lateinit var viewAdapter: GalleryVideoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSelectAnVideoBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        val videoActivity =  requireActivity() as VideoUploadActivity
        videoActivity.setSupportActionBar(binding.toolbar)
        videoActivity.supportActionBar?.title =  getString(R.string.select_and_video)
        videoActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        videoActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        setHasOptionsMenu(true)
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.OpenGallery -> {
                    videoGallery(555)
                }
            }
            true
        }
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
        askForExternalStoragePermission()

        viewAdapter.setOnClickListener {
            Log.e("uri", "$it")
            val action = SelectAnVideoDirections.actionSelectAnVideoToSelectedVideoView(it)
            findNavController().navigate(action)
        }

    }

    private fun askForExternalStoragePermission() {
        Dexter.withContext(requireContext())
            .withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    binding.videosRv.apply {
                        layoutManager = GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
                        adapter = viewAdapter
                    }
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    requireActivity().onBackPressed()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?,
                ) {
                    p1?.continuePermissionRequest()
                }
            }).check()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.select_an_video_menu, menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 555 && resultCode == Activity.RESULT_OK && data?.data != null){
            when(requestCode){
                555 -> {
                    data.data?.let {
                        val path = getPath(it, requireActivity() as AppCompatActivity)

                        val galleryVideo = GalleryVideo(
                            path!!,
                            path
                        )

                        Log.e("uri", "$galleryVideo")
                        val action = SelectAnVideoDirections.actionSelectAnVideoToSelectedVideoView(galleryVideo)
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }


    private fun setupRecyclerView() {
        val videos = GalleryVideos().get(
            requireActivity() as AppCompatActivity
        )
        viewAdapter = GalleryVideoAdapter()
        viewAdapter.differ.submitList(videos)
    }


}