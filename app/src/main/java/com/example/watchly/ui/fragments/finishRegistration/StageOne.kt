package com.example.watchly.ui.fragments.finishRegistration

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.watchly.databinding.FragmentStageOneBinding
import com.example.watchly.ui.activities.MainActivity
import com.example.watchly.ui.viewmodels.UserViewModel
import com.example.watchly.uils.Constants
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource.hideLoadingDialog
import com.example.watchly.uils.ReusableResource.initLoadingDialog
import com.example.watchly.uils.ReusableResource.showLoadingDialog
import com.example.watchly.uils.ReusableResource.showTopSnackBar


class StageOne : Fragment() {

    private lateinit var binding: FragmentStageOneBinding
    private lateinit var userViewModel: UserViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStageOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLoadingDialog()
        userViewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
            .create(UserViewModel::class.java)

        binding.btnFinish.setOnClickListener {
            if(binding.etName.text.toString().isEmpty()){
                showTopSnackBar("Please enter your name")
                requireActivity().onBackPressed()
                return@setOnClickListener
            }
            if(binding.etLastname.text.toString().isEmpty()){
                showTopSnackBar("Please enter your lastname")
                requireActivity().onBackPressed()
                return@setOnClickListener
            }
            if(binding.etUsername.text.toString().isEmpty()){
                showTopSnackBar("Please add an username")
                requireActivity().onBackPressed()
                return@setOnClickListener
            }
            finishUserRegistration()
        }



    }


    private fun finishUserRegistration(){
        val userHashMap = HashMap<String, Any>()
        userHashMap[Constants.name] = binding.etName.text.toString()
        userHashMap[Constants.lastname] = binding.etLastname.text.toString()
        userHashMap[Constants.username] = binding.etUsername.text.toString()
        userHashMap[Constants.registrationState] = "finished"

        userViewModel.updateUserData(userHashMap)
        userViewModel.updateUserDataState.observe(viewLifecycleOwner){
            when(it){
                is Resource.Error -> {
                    hideLoadingDialog()
                    if(it.message != null){
                        showTopSnackBar(it.message.toString())
                    }
                }
                is Resource.Loading -> {
                    showLoadingDialog()
                }
                is Resource.Success -> {
                    hideLoadingDialog()
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }
    }


}