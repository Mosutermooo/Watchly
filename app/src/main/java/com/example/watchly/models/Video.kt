package com.example.watchly.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.PrimaryKey
import java.io.Serializable

data class Video (
    val id: Int? = null,
    val name: String? = null,
    val description: String? = null,
    val thumbnail: String? = null,
    val video: String? = null,
    val visibility: String? = null,
    val channel: String? = null,
    val user: String? = null,
    val videoId: String? = null,
    val views: Int? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(thumbnail)
        parcel.writeString(video)
        parcel.writeString(visibility)
        parcel.writeString(channel)
        parcel.writeString(user)
        parcel.writeString(videoId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Video> {
        override fun createFromParcel(parcel: Parcel): Video {
            return Video(parcel)
        }

        override fun newArray(size: Int): Array<Video?> {
            return arrayOfNulls(size)
        }
    }
}