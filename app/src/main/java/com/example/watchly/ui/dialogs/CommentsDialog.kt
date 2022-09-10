package com.example.watchly.ui.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchly.adapters.CommentsAdapter
import com.example.watchly.databinding.DialogCommentsBinding
import com.example.watchly.ui.viewmodels.VideoViewModel
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource.hideLoadingDialog
import com.example.watchly.uils.ReusableResource.showLoadingDialog
import com.example.watchly.uils.ReusableResource.showSnackBar
import com.example.watchly.uils.ReusableResource.showTopSnackBar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collect

class CommentsDialog(private var videoId: String) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogCommentsBinding
    private lateinit var videoViewModel: VideoViewModel
    private lateinit var commentAdapter: CommentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoViewModel = ViewModelProvider.AndroidViewModelFactory(
            requireActivity().application
        ).create(VideoViewModel::class.java)
        setupRecyclerView()

        binding.btnAddComment.setOnClickListener {
            addComment()
        }

        videoViewModel.getComments(videoId)
        lifecycleScope.launchWhenStarted {
            videoViewModel.getCommentsState.collect {
                when(it){
                    is Resource.Error -> {
                        showSnackBar(it.message.toString())
                        binding.progressBar.visibility = View.GONE
                    }
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        if(it.data != null && it.data.isNotEmpty()){
                            commentAdapter.differ.submitList(it.data)
                            binding.commentsDisplay.visibility = View.VISIBLE
                            binding.anim.visibility = View.GONE
                            binding.firstCommentText.visibility = View.GONE
                            binding.anim.pauseAnimation()
                            Log.e("data", "${it.data}")
                        }else{
                            Log.e("data", "${it.data}")
                            binding.anim.visibility = View.VISIBLE
                            binding.firstCommentText.visibility = View.VISIBLE
                            binding.anim.playAnimation()
                            binding.commentsDisplay.visibility = View.GONE
                        }
                    }
                }
            }
        }




    }

    private fun setupRecyclerView(){
        commentAdapter = CommentsAdapter(videoId)
        binding.commentsDisplay.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
        }
    }

    private fun addComment() {
        lifecycleScope.launchWhenStarted {
            videoViewModel.addAComment(binding.etComment.text.toString(), videoId)
            videoViewModel.commentState.collect {
                when(it){
                    is Resource.Error -> {
                        showSnackBar(it.message.toString())
                    }
                    is Resource.Loading -> {
                        //showLoadingDialog()
                    }
                    is Resource.Success -> {
                        hideLoadingDialog()
                        binding.etComment.setText("")
                        showTopSnackBar("Comment Added")
                    }
                }
            }
        }
    }

}