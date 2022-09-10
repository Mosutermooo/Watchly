package com.example.watchly.use_cases

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.watchly.models.GalleryVideo

class GalleryVideos {

    private lateinit var uri: Uri
    private lateinit var cursor: Cursor
    private var thumb: Int? = null
    private var columnIndexData: Int? = null


    fun get(activity: AppCompatActivity) : ArrayList<GalleryVideo> {
        val galleryVideos : ArrayList<GalleryVideo> = arrayListOf()
        var absolutePath: String? = null


        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val projection : Array<String> = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media._ID,
            MediaStore.Video.Thumbnails.DATA
        )

        val orderBy: String = MediaStore.Images.Media.DATE_TAKEN

        cursor = activity.applicationContext.contentResolver.query(
            uri,
            projection,
            null,null,
            orderBy
        )!!

        thumb = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA)
        columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

        while (cursor.moveToNext()){
            absolutePath = cursor.getString(columnIndexData!!)
            val galleryVideo = GalleryVideo(
                absolutePath,
                cursor.getString(thumb!!)
            )
            Log.e("galleryVideoModel" , "$galleryVideo")
            Log.e("cursor" , "$cursor")
            Log.e("absolutePath" , "$absolutePath")
            galleryVideos.add(galleryVideo)
        }

        return galleryVideos
    }

}