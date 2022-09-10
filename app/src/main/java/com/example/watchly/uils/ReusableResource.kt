package com.example.watchly.uils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.example.watchly.R
import com.example.watchly.models.SubUser
import com.example.watchly.ui.dialogs.LoadingDialog
import com.example.watchly.ui.viewmodels.UserViewModel
import com.example.watchly.uils.Constants.LoadingDialogTag
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firestore.v1.Cursor
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


object ReusableResource {

    private lateinit var dialog: Dialog
    private lateinit var subUser: SubUser
    var thumbnailUri : Uri? = null

    fun setupToolBarOnBackPressed(
        toolbar: Toolbar,
        title: String?,
        subtitle: String?,
        activity: AppCompatActivity,
    ){
        activity.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_white)
            actionBar.title = title ?: ""
            actionBar.subtitle = subtitle ?: ""
        }

        toolbar.setNavigationOnClickListener {
            activity.onBackPressed()
        }

    }

    fun signOut(){
        FirebaseAuth.getInstance().signOut()
    }

    fun signOutWithEverythingDeleted(activity: AppCompatActivity){
        FirebaseAuth.getInstance().signOut()
        deleteSubUserFromDataStore(activity)
    }

    fun deleteSubUserFromDataStore(activity: AppCompatActivity){
        activity.lifecycleScope.launch {
            val dataStore = DataStore(activity)
            dataStore.delete()
        }
    }

    fun uid(): String{
        return FirebaseAuth.getInstance().uid.toString()
    }

    fun Fragment.showTopSnackBar(title: String){
        val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), title, Snackbar.LENGTH_SHORT)
        val view: View = snackBar.view
        val params : FrameLayout.LayoutParams =
            (view.layoutParams as FrameLayout.LayoutParams)
        params.gravity = Gravity.TOP
        view.layoutParams = params
        snackBar.show()
    }
    fun Fragment.showSnackBar(title: String){
        val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content), title, Snackbar.LENGTH_SHORT)
        val view: View = snackBar.view
        snackBar.show()
    }



    fun showLoadingDialogFragment(
        activity: AppCompatActivity,
    ){
        LoadingDialog().show(activity.supportFragmentManager, LoadingDialogTag)
    }
    fun hideLoadingDialog(
        tag: String,
        activity: AppCompatActivity,
    ){
        activity.supportFragmentManager.findFragmentByTag(tag)?.let {
            if(it is DialogFragment) it.dismiss() }
    }

    fun Fragment.initLoadingDialog(){
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_fragment_loading)
        val anim = dialog.findViewById<LottieAnimationView>(R.id.anim)
        anim.playAnimation()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
    }

    fun Fragment.showLoadingDialog(){
        dialog.show()
    }

    fun Fragment.hideLoadingDialog(){
        dialog.dismiss()
    }

    fun currentDateAndTime(): String {
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateTimeInstance()
        return formatter.format(date)
    }

    fun Fragment.openGallery(SELECT_IMAGE_CODE: Int){
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_PICK)
        startActivityForResult(Intent.createChooser(intent, "Select an Picture"), SELECT_IMAGE_CODE)
    }

    fun Fragment.videoGallery(SELECT_IMAGE_CODE: Int){
        val intent = Intent()
        intent.setType("video/*")
        intent.setAction(Intent.ACTION_PICK)
        startActivityForResult(Intent.createChooser(intent, "Select an Picture"), SELECT_IMAGE_CODE)
    }


    fun Activity.openGallery(SELECT_IMAGE_CODE: Int){
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_PICK)
        startActivityForResult(Intent.createChooser(intent, "Select an Picture"), SELECT_IMAGE_CODE)
    }



    fun Fragment.changeUserData(
        hashMap: HashMap<String, Any>,
        userViewModel: UserViewModel,
    ){
        initLoadingDialog()
        userViewModel.updateUserData(hashMap)
        userViewModel.updateUserDataState.observe(viewLifecycleOwner){
            when(it){
                is Resource.Error -> {
                    showSnackBar(it.message.toString())
                    hideLoadingDialog()
                }
                is Resource.Loading -> {
                    showLoadingDialog()
                }
                is Resource.Success -> {
                    hideLoadingDialog()
                    showSnackBar(getString(R.string.user_changes_made))
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    inline fun Fragment.setFragmentResultListener(
        requestKey: String,
        noinline listener: ((resultKey: String, bundle: Bundle) -> Unit),
    ) {
        parentFragmentManager.setFragmentResultListener(requestKey, this, listener)
    }

    fun Fragment.setFragmentResult(
        requestKey: String,
        result: Bundle,
    ) = parentFragmentManager.setFragmentResult(requestKey, result)

    fun getValueInDP(context: Context, value: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            value,
            context.resources.displayMetrics)
    }

    fun getPath(uri: Uri?, activity: AppCompatActivity): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor: android.database.Cursor? = activity.getContentResolver().query(uri!!, projection, null, null, null)
        return if (cursor != null) {
            val columnIndex: Int = cursor
                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } else null
    }

}