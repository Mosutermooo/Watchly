package com.example.watchly.ui.fragments.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.watchly.R
import com.example.watchly.databinding.FragmentProfileBinding
import com.example.watchly.models.User
import com.example.watchly.ui.activities.ChannelActivity
import com.example.watchly.ui.activities.CreateChannelActivity
import com.example.watchly.ui.activities.LoginActivity
import com.example.watchly.ui.activities.UserSettings
import com.example.watchly.ui.viewmodels.UserViewModel
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource
import com.example.watchly.uils.ReusableResource.showSnackBar


class Profile : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
            .create(UserViewModel::class.java)
        getUserInformation()

        binding.LogOut.setOnClickListener {
            ReusableResource.signOutWithEverythingDeleted(requireActivity() as AppCompatActivity)
            Intent(requireActivity(), LoginActivity::class.java).apply {
                startActivity(this)
                requireActivity().finish()
            }
        }

        binding.UserSettings.setOnClickListener {
            Intent(requireActivity(), UserSettings::class.java).apply {
                startActivity(this)
            }
        }



    }

    private fun getUserInformation(){
        userViewModel.getUserData()
        userViewModel.userData.observe(viewLifecycleOwner){
            when(it){
                is Resource.Error -> {

                }
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    if(it.data != null){
                        user = it.data
                        displayUserData(it.data)
                    }
                }
            }
        }
    }



    private fun displayUserData(data: User) {
        Glide.with(requireActivity())
            .load(data.image)
            .placeholder(R.color.GrayTextColor)
            .centerCrop()
            .into(binding.userImage)
        binding.username.text = data.username
        binding.Name.text = "${data.name} ${data.lastname}"
    }




}