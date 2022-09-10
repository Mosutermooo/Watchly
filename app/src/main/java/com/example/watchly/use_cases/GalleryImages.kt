package com.example.watchly.use_cases

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.example.watchly.models.GalleryVideo

class GalleryImages {


    private lateinit var uri: Uri
    private lateinit var cursor: Cursor
    private var thumb: Int? = null
    private var columnIndexData: Int? = null

    fun get(activity: AppCompatActivity) : ArrayList<String>{
        val galleryImages : ArrayList<String> = arrayListOf()
        var absolutePath: String? = null

        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projection : Array<String> = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        val orderBy = MediaStore.Video.Media.DATE_TAKEN
        cursor = activity.applicationContext.contentResolver.query(
            uri,
            projection,
            null,
            null,
            "$orderBy DESC"
        )!!

        columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

        while (cursor.moveToNext()){
            absolutePath = cursor.getString(columnIndexData!!)
            galleryImages.add(absolutePath)
        }
        return galleryImages
    }

}