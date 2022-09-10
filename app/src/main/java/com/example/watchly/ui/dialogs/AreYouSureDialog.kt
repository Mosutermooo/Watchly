package com.example.watchly.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.watchly.R
import com.example.watchly.databinding.AreYouSureDialogBinding
import com.example.watchly.models.SubUser

class AreYouSureDialog(
    val message: String
) : DialogFragment(R.layout.are_you_sure_dialog) {

    private lateinit var binding: AreYouSureDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AreYouSureDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvYES.setOnClickListener {
            onYesClickListener?.invoke()
        }
        binding.messageDialog.text = message
        binding.tvNO.setOnClickListener {
            dismiss()
        }

    }

    private var onYesClickListener: (() -> Unit)? = null

    fun setOnYesClickListener(listener: () -> Unit){
        onYesClickListener = listener
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
    }


}