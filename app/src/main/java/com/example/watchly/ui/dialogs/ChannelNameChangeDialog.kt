package com.example.watchly.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.watchly.R
import com.example.watchly.databinding.ChannelNameChangeDialogBinding
import com.example.watchly.ui.viewmodels.AuthViewModel
import com.example.watchly.ui.viewmodels.ChannelViewModel
import com.example.watchly.uils.Resource
import com.example.watchly.uils.ReusableResource.hideLoadingDialog
import com.example.watchly.uils.ReusableResource.showLoadingDialog
import com.example.watchly.uils.ReusableResource.showSnackBar

class ChannelNameChangeDialog(
    private var channelName: String,
    private var channelId: String
) : DialogFragment() {

    private lateinit var binding: ChannelNameChangeDialogBinding
    private lateinit var channelViewModel: ChannelViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ChannelNameChangeDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etName.setText(channelName)
        channelViewModel = ViewModelProvider.AndroidViewModelFactory(requireActivity().application).create(
            ChannelViewModel::class.java)
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
        binding.tvOK.setOnClickListener {
            if(binding.etName.text.toString() != channelName
                && binding.etName.text?.length!! < 20){
                val channelHashMap = HashMap<String, Any>()
                channelHashMap["name"] = binding.etName.text.toString()
                channelViewModel.updateChannelData(
                    channelId,
                    channelHashMap
                )
                channelViewModel.channelUpdateState.observe(viewLifecycleOwner){
                    when(it){
                        is Resource.Error -> {
                            showSnackBar(it.message.toString())
                            hideLoadingDialog()
                        }
                        is Resource.Loading -> {
                            //showLoadingDialog()
                        }
                        is Resource.Success -> {
                            hideLoadingDialog()
                            dismiss()
                        }
                    }
                }
            }else{
                showSnackBar(getString(R.string.no_changes_made))
                dismiss()
            }

        }

    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
    }

}