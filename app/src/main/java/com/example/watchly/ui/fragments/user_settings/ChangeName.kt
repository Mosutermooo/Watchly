package com.example.watchly.ui.fragments.user_settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.watchly.R
import com.example.watchly.databinding.FragmentChangeNameBinding
import com.example.watchly.ui.viewmodels.UserViewModel
import com.example.watchly.uils.Constants
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource
import com.example.watchly.uils.ReusableResource.changeUserData
import com.example.watchly.uils.ReusableResource.hideLoadingDialog
import com.example.watchly.uils.ReusableResource.initLoadingDialog
import com.example.watchly.uils.ReusableResource.showLoadingDialog
import com.example.watchly.uils.ReusableResource.showSnackBar
import com.google.firebase.firestore.auth.User

class ChangeName : Fragment() {


    private lateinit var binding: FragmentChangeNameBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentChangeNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(UserViewModel::class.java)

        initLoadingDialog()
        getUserData()
        binding.Save.setOnClickListener {
            changeUserData()
        }

        binding.Cancel.setOnClickListener {
            requireActivity().onBackPressed()
        }

    }

    private fun changeUserData() {
        if(binding.etName.text.toString().isEmpty()){
            showSnackBar(getString(R.string.field_empty))
            return
        }
        val nameHash = HashMap<String, Any>()
        nameHash[Constants.name] = binding.etName.text.toString()
        changeUserData(nameHash, userViewModel)
    }


    private fun getUserData() {
        userViewModel.getUserData()
        userViewModel.userData.observe(viewLifecycleOwner){
            when(it){
                is Resource.Error -> {
                    binding.name.text = getString(R.string.failed_to_get_name)
                }
                is Resource.Success -> {
                    binding.name.text = "${getString(R.string.current_name)} ${it.data?.name}"
                }
            }
        }
    }

}