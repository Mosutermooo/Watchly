package com.example.watchly.ui.fragments.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.watchly.R
import com.example.watchly.databinding.FragmentLoginBinding
import com.example.watchly.ui.activities.FinishRegistrationActivity
import com.example.watchly.ui.activities.MainActivity
import com.example.watchly.ui.viewmodels.AuthViewModel
import com.example.watchly.ui.viewmodels.UserViewModel
import com.example.watchly.uils.AuthState
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource.hideLoadingDialog
import com.example.watchly.uils.ReusableResource.initLoadingDialog
import com.example.watchly.uils.ReusableResource.showLoadingDialog
import com.example.watchly.uils.ReusableResource.showTopSnackBar
import com.example.watchly.uils.ReusableResource.signOut


class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application).create(AuthViewModel::class.java)
        userViewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application).create(UserViewModel::class.java)

        binding.tvRegisterFragment.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        initLoadingDialog()

        binding.SignIn.setOnClickListener {
            if(binding.etEmail.text.toString().isEmpty() && binding.etPassword.text.toString().isEmpty()){
                binding.tvErrorEmail.text = getString(R.string.field_empty)
                binding.tvErrorPassword.text = getString(R.string.field_empty)
                return@setOnClickListener
            }else{
                binding.tvErrorEmail.text = ""
                binding.tvErrorPassword.text = ""
            }
            if(binding.etEmail.text.toString().isEmpty()){
                binding.tvErrorEmail.text = getString(R.string.email_empty)
                return@setOnClickListener
            }else{
                binding.tvErrorEmail.text = ""
            }
            if(binding.etPassword.text.toString().isEmpty()){
                binding.tvErrorPassword.text = getString(R.string.password_empty)
                return@setOnClickListener
            }else{
                binding.tvErrorPassword.text = ""
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString()).matches()){
                binding.tvErrorEmail.text = getString(R.string.invaild_email)
                return@setOnClickListener
            }else{
                binding.tvErrorEmail.text = ""
            }
            loginUser()

        }

    }

    private fun loginUser() {
        authViewModel.login(
            binding.etEmail.text.toString(),
            binding.etPassword.text.toString()
        )
        authViewModel.state.observe(viewLifecycleOwner){ authState ->
            when(authState){
                is AuthState.Error -> {
                    val exception = authState.exception?.message
                    if(exception != null){
                        showTopSnackBar(exception)
                    }
                    hideLoadingDialog()
                }
                AuthState.Loading -> {
                    showLoadingDialog()
                }
                AuthState.Success -> {
                    userViewModel.getUserData()
                    userViewModel.userData.observe(viewLifecycleOwner){
                        when(it){
                            is Resource.Error -> {
                                hideLoadingDialog()
                                signOut()
                                if(it.message != null){
                                    showTopSnackBar(it.message.toString())
                                }
                            }
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                hideLoadingDialog()
                                val user = it.data
                                if(user != null){
                                    if(user.registrationState != "not finished"){
                                        val intent = Intent(requireActivity(), MainActivity::class.java)
                                        startActivity(intent)
                                        requireActivity().finish()
                                    }else{
                                        val intent = Intent(requireActivity(), FinishRegistrationActivity::class.java)
                                        startActivity(intent)
                                        requireActivity().finish()
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }

}