package com.example.watchly.ui.fragments.user_settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.watchly.R
import com.example.watchly.databinding.FragmentChangeLastNameBinding
import com.example.watchly.ui.viewmodels.UserViewModel
import com.example.watchly.uils.Constants
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource.changeUserData
import com.example.watchly.uils.ReusableResource.initLoadingDialog
import com.example.watchly.uils.ReusableResource.showSnackBar

class ChangeLastName : Fragment() {

    private lateinit var binding: FragmentChangeLastNameBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeLastNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider
            .AndroidViewModelFactory
            .getInstance(requireActivity().application)
            .create(UserViewModel::class.java)

        getUserData()

        binding.Cancel.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.Save.setOnClickListener {
            changeUserData()
        }





    }

    private fun changeUserData() {
        if(binding.etLastname.text.toString().isEmpty()){
            showSnackBar(getString(R.string.field_empty))
            return
        }
        val lastNameHash = HashMap<String, Any>()
        lastNameHash[Constants.lastname] = binding.etLastname.text.toString()
        changeUserData(lastNameHash, userViewModel)
    }


    private fun getUserData() {
        userViewModel.getUserData()
        userViewModel.userData.observe(viewLifecycleOwner){
            when(it){
                is Resource.Error -> {
                    binding.lastname.text = getString(R.string.failed_to_get_name)
                }
                is Resource.Success -> {
                    binding.lastname.text = "${getString(R.string.current_lastname)}: ${it.data?.lastname}"
                }
            }
        }
    }

}