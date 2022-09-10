package com.example.watchly.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.watchly.R
import com.example.watchly.ui.viewmodels.AuthViewModel
import com.example.watchly.ui.viewmodels.UserViewModel
import com.example.watchly.uils.*
import com.example.watchly.uils.ReusableResource.hideLoadingDialog
import com.example.watchly.uils.ReusableResource.showLoadingDialogFragment
import com.example.watchly.uils.ReusableResource.showTopSnackBar
import com.example.watchly.uils.ReusableResource.signOut
import kotlinx.coroutines.launch

class StartActivity : AppCompatActivity() {
    private lateinit var viewModel : AuthViewModel
    private lateinit var userViewModel : UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        viewModel = ViewModelProvider.AndroidViewModelFactory(application).create(AuthViewModel::class.java)
        userViewModel = ViewModelProvider.AndroidViewModelFactory(application).create(UserViewModel::class.java)
        viewModel.isLoggedIn()
        viewModel.isLoggedInState.observe(this){ state ->
            when(state){
                AuthState.IsNotSignedIn ->{
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                AuthState.IsSignedIn -> {
                    userViewModel.getUserData()
                    userViewModel.userData.observe(this){
                        when(it){
                            is Resource.Error -> {
                                signOut()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                                hideLoadingDialog(Constants.LoadingDialogTag, this)
                                if(it.message != null){
                                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                                }
                            }
                            is Resource.Loading -> {
                                showLoadingDialogFragment(this)
                            }
                            is Resource.Success -> {
                                val user = it.data
                                hideLoadingDialog(Constants.LoadingDialogTag, this)
                                if(user != null){
                                    if(user.registrationState != "not finished"){
                                        val intent = Intent(this@StartActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }else{
                                        val intent = Intent(this, FinishRegistrationActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            }
                            else -> {
                                signOut()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                                hideLoadingDialog(Constants.LoadingDialogTag, this)
                                if(it.message != null){
                                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}