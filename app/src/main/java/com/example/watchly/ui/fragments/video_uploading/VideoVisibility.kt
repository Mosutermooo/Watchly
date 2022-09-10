package com.example.watchly.ui.fragments.video_uploading

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.example.watchly.R
import com.example.watchly.databinding.FragmentVideoVisibilityBinding
import com.example.watchly.uils.ReusableResource
import com.example.watchly.uils.ReusableResource.setFragmentResult
import com.example.watchly.uils.ReusableResource.showSnackBar

class VideoVisibility : Fragment() {

    private lateinit var binding: FragmentVideoVisibilityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentVideoVisibilityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ReusableResource.setupToolBarOnBackPressed(
            binding.toolbar,
            getString(R.string.set_video_visibility),
            null,
            requireActivity() as AppCompatActivity
        )
        binding.radioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            val checkRadioButton = radioGroup.findViewById<RadioButton>(checkedId)
            if(checkRadioButton == binding.Public){
                showSnackBar(getString(R.string.public_video))
                setFragmentResult(
                    "visibilityResult",
                    bundleOf("visibility" to "public")
                )
            }
            if(checkRadioButton == binding.Private){
                showSnackBar(getString(R.string.private_video))
                setFragmentResult(
                    "visibilityResult",
                    bundleOf("visibility" to "private")
                )
            }
        }
    }


}