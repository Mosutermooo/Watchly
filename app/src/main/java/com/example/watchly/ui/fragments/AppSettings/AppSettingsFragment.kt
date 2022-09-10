package com.example.watchly.ui.fragments.AppSettings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.watchly.R
import com.example.watchly.databinding.FragmentAppSettingsBinding
import com.example.watchly.models.User
import com.example.watchly.ui.activities.ChannelActivity
import com.example.watchly.ui.activities.CreateChannelActivity
import com.example.watchly.ui.viewmodels.UserViewModel
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource
import com.example.watchly.uils.ReusableResource.initLoadingDialog
import com.example.watchly.uils.ReusableResource.showSnackBar


class AppSettingsFragment : Fragment() {

    private lateinit var binding: FragmentAppSettingsBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var user: User



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAppSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
            .create(UserViewModel::class.java)
        initLoadingDialog()
        ReusableResource.setupToolBarOnBackPressed(
            binding.toolbar,
            getString(R.string.app_settings),
            getString(R.string.change_app_settings),
            requireActivity() as AppCompatActivity
        )
        getUserInformation()

        binding.ChannelSettings.setOnClickListener {
            if(user.channel != null){
                Intent(requireActivity(), ChannelActivity::class.java).apply {
                    startActivity(this)
                }
            }else{
                showSnackBar(getString(R.string.please_create_a_channel))
            }
        }


        binding.CreateChannel.setOnClickListener {
            if(user.channel != null && user.channel != ""){
                showSnackBar(getString(R.string.channel_already_created))
            }else{
                Intent(requireActivity(), CreateChannelActivity::class.java).apply {
                    startActivity(this)
                }
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
                    }
                }
            }
        }
    }





}