package com.example.watchly.ui.fragments.login

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.watchly.R
import com.example.watchly.databinding.FragmentRigisterBinding
import com.example.watchly.ui.viewmodels.AuthViewModel
import com.example.watchly.uils.AuthState
import com.example.watchly.uils.ReusableResource.hideLoadingDialog
import com.example.watchly.uils.ReusableResource.setupToolBarOnBackPressed
import com.example.watchly.uils.ReusableResource.showLoadingDialog
import com.example.watchly.uils.ReusableResource.showLoadingDialogFragment
import com.example.watchly.uils.ReusableResource.showTopSnackBar


class RegisterFragment : Fragment(R.layout.fragment_rigister) {

    private lateinit var binding: FragmentRigisterBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRigisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolBarOnBackPressed(
            binding.tlSingUp,
            null,
            null,
            activity as AppCompatActivity
        )

        viewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application).create(AuthViewModel::class.java)

        binding.btnRegister.setOnClickListener {

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

            if(binding.etConfirmPassword.text.toString().isEmpty()){
                binding.tvErrorConfirmPassword.text = getString(R.string.confirm_pass_empty)
                return@setOnClickListener
            }else{
                binding.tvErrorConfirmPassword.text = ""
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString()).matches()){
                binding.tvErrorEmail.text = getString(R.string.invaild_email)
                return@setOnClickListener
            }else{
                binding.tvErrorEmail.text = ""
            }

            if(binding.etPassword.text.toString().length < 6){
                binding.tvErrorPassword.text = getString(R.string.weak_password)
                return@setOnClickListener
            }else{
                binding.tvErrorPassword.text = ""
            }

            if(binding.etPassword.text.toString() != binding.etConfirmPassword.text.toString()){
                binding.tvErrorPassword.text = getString(R.string.password_do_not_match)
                binding.tvErrorConfirmPassword.text = getString(R.string.password_do_not_match)
                return@setOnClickListener
            }else{
                binding.tvErrorConfirmPassword.text = ""
            }
            registerUser()
        }

    }



    private fun registerUser() {
        viewModel.register(
            binding.etEmail.text.toString(),
            binding.etPassword.text.toString()
        )
        viewModel.registerState.observe(viewLifecycleOwner){authState ->
            when(authState){
                is AuthState.Error -> {
                    hideLoadingDialog()
                    val exception = authState.exception?.message
                    if(exception != null){
                        showTopSnackBar(exception)
                    }
                }
                AuthState.Loading -> {
                    showLoadingDialog()
                }
                AuthState.Success -> {
                    hideLoadingDialog()
                    requireActivity().onBackPressed()
                }
            }

        }
    }


}