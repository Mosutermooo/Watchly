package com.example.watchly.ui.fragments.channel

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.example.watchly.R
import com.example.watchly.databinding.FragmentCreateChannelBinding
import com.example.watchly.models.Channel
import com.example.watchly.ui.viewmodels.ChannelViewModel
import com.example.watchly.ui.viewmodels.UserViewModel
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource
import com.example.watchly.uils.ReusableResource.hideLoadingDialog
import com.example.watchly.uils.ReusableResource.initLoadingDialog
import com.example.watchly.uils.ReusableResource.openGallery
import com.example.watchly.uils.ReusableResource.showLoadingDialog
import com.example.watchly.uils.ReusableResource.showSnackBar
import com.example.watchly.uils.ReusableResource.uid
import com.example.watchly.use_cases.ChannelId
import com.google.firebase.storage.FirebaseStorage


class CreateChannelFragment : Fragment() {

    private lateinit var binding: FragmentCreateChannelBinding
    private lateinit var channelViewModel: ChannelViewModel
    private lateinit var userViewModel: UserViewModel
    private var imageUri: Uri? = null
    private var downloadedUri: Uri? = null
    private val channelId = ChannelId()
    private val generatedUid = channelId.generate()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCreateChannelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLoadingDialog()

        channelViewModel = ViewModelProvider.AndroidViewModelFactory(
            requireActivity().application
        ).create(ChannelViewModel::class.java)
        userViewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
            .create(UserViewModel::class.java)
        ReusableResource.setupToolBarOnBackPressed(
            binding.toolbar,
            getString(R.string.create_channel), "Please fill the following information",
            requireActivity() as AppCompatActivity
        )

        binding.channelImage.setOnClickListener {
            openGallery(90)
        }

        binding.btnCreateChannel.setOnClickListener {
           checkValidation()
        }

    }

    private fun checkValidation() {
        if(binding.etChannelName.text.toString().isEmpty()){
            showSnackBar(getString(R.string.channel_name_missin))
            return
        }

        if(imageUri != null){
            showLoadingDialog()
            addUserImageToStorage()
        }else{
            userViewModel.getUserData()
            userViewModel.userData.observe(viewLifecycleOwner){
                if(it.data?.image != null){
                    downloadedUri = it.data.image.toUri()
                    showLoadingDialog()
                    observeResult(generatedUid)
                }else{
                    showLoadingDialog()
                    observeResult(generatedUid)
                }

            }
        }
    }

    private fun observeResult(generatedUid: String) {
        val channel = Channel(
            null,
            binding.etChannelName.text.toString(),
            binding.etChannelDescription.text.toString(),
            downloadedUri.toString(),
            "",
            generatedUid,
            uid(),
            ReusableResource.currentDateAndTime()
        )
        channelViewModel.createChannelInFireStore(
            generatedUid,
            channel
        )
        channelViewModel.channelCreationState.observe(viewLifecycleOwner){
            when(it){
                is Resource.Error -> {
                    hideLoadingDialog()
                    if(it.message != null){
                        if(it.message == "empty fields"){
                            showSnackBar(getString(R.string.field_empty))
                        }
                        showSnackBar(it.message.toString())
                    }
                }
                is Resource.Success -> {
                    hideLoadingDialog()
                    showSnackBar(getString(R.string.succesfully_created_channel))
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 90 && resultCode == Activity.RESULT_OK){
            val uri = data?.data
            if(uri != null){
                imageUri = uri
                binding.channelImage.setImageURI(imageUri)
            }
        }
    }

    private fun addUserImageToStorage(){
        FirebaseStorage.getInstance().reference.child(ReusableResource.uid())
            .child("/userData")
            .child("channel")
            .child(generatedUid)
            .putFile(imageUri!!)
            .addOnSuccessListener{ task ->
                task.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    downloadedUri = it
                    observeResult(generatedUid)
                }
            }
    }

}