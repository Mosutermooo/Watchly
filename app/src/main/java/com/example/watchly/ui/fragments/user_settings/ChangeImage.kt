package com.example.watchly.ui.fragments.user_settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.watchly.R
import com.example.watchly.databinding.FragmentChangeImageBinding
import com.example.watchly.ui.viewmodels.UserViewModel
import com.example.watchly.uils.Constants
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource
import com.example.watchly.uils.ReusableResource.changeUserData
import com.example.watchly.uils.ReusableResource.hideLoadingDialog
import com.example.watchly.uils.ReusableResource.initLoadingDialog
import com.example.watchly.uils.ReusableResource.openGallery
import com.example.watchly.uils.ReusableResource.showLoadingDialog
import com.example.watchly.uils.ReusableResource.showSnackBar
import com.example.watchly.uils.ReusableResource.uid
import com.google.firebase.storage.FirebaseStorage
import java.net.URI

class ChangeImage : Fragment() {

    private lateinit var binding: FragmentChangeImageBinding
    private lateinit var userViewModel: UserViewModel
    private var imageUri: Uri? = null
    private var downloadedUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentChangeImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLoadingDialog()
        userViewModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(UserViewModel::class.java)

        userViewModel.getUserData()
        userViewModel.userData.observe(viewLifecycleOwner){
            Glide.with(requireActivity())
                .load(it.data?.image)
                .placeholder(R.color.GrayTextColor)
                .centerCrop()
                .into(binding.CurrentImage)
        }

        binding.Cancel.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.Save.setOnClickListener {
            if(imageUri != null){
                showLoadingDialog()
                addSubUserImageToStorage()
            }else{
                showSnackBar(getString(R.string.please_select_an_image))
            }
        }

        binding.btnOpenGallery.setOnClickListener {
            openGallery(1000)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1000 && resultCode == Activity.RESULT_OK){
            val uri = data?.data
            if(uri != null){
                imageUri = uri
                binding.NewImage.setImageURI(imageUri)
            }

        }
    }

    private fun addSubUserImageToStorage(){
        FirebaseStorage.getInstance().reference.child(ReusableResource.uid())
            .child("/userData")
            .child("${uid()}.jpg")
            .putFile(imageUri!!)
            .addOnSuccessListener{ task ->
                task.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    downloadedUri = it
                    updateUserImage()
                }
            }
    }

    private fun updateUserImage() {
        val imageHashMap = HashMap<String, Any>()
        imageHashMap[Constants.image] = downloadedUri.toString()
       changeUserData(
           imageHashMap,
           userViewModel
       )
    }

    private fun changeUserData(
        hashMap: HashMap<String, Any>,
        userViewModel: UserViewModel
    ){
        userViewModel.updateUserData(hashMap)
        userViewModel.updateUserDataState.observe(viewLifecycleOwner){
            when(it){
                is Resource.Error -> {
                    showSnackBar(it.message.toString())
                    hideLoadingDialog()
                }
                is Resource.Success -> {
                    hideLoadingDialog()
                    showSnackBar(getString(R.string.user_changes_made))
                    requireActivity().onBackPressed()
                }
            }
        }
    }



}