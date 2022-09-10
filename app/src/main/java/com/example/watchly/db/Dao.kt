package com.example.watchly.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import com.example.watchly.models.Channel
import com.example.watchly.models.Search
import com.example.watchly.models.SubUser
import com.example.watchly.models.UploadVideo


@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllSubUsers(subUsers: List<SubUser>)

    @Query("SELECT * FROM subUserTable")
    fun getAllSubUsers(): LiveData<List<SubUser>>

    @Query("DELETE FROM subUserTable")
    suspend fun deleteAllSubUsers()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSubUser(subUser: SubUser)

    @Query("SELECT * FROM subUserTable where subUserUid = :subUserId")
    fun getSavedSubUser(subUserId: String): LiveData<SubUser>

    @Insert(onConflict= OnConflictStrategy.REPLACE)
    suspend fun cacheChannel(channel: Channel)

    @Query("SELECT * FROM channel")
    fun getCachedChannel() : LiveData<Channel>

    @Query("DELETE FROM channel")
    suspend fun deleteCachedChannel()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun cacheUploadVideo(uploadVideo: UploadVideo)

    @Query("SELECT * FROM uploadVideo")
    fun receiveCachedUploadVideo() : LiveData<List<UploadVideo>>

    @Query("Delete from uploadVideo where id = :id")
    suspend fun deleteCachedUploadVideo(id: Int)

    @Query("delete from uploadVideo")
    suspend fun deleteEverythingFromUploadVideo()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun query(search: Search)

    @Query("Select * from searchTable")
    fun quarries(): LiveData<List<Search>>

    @Query("delete from searchTable where id = :id")
    suspend fun deleteQuery(id: Int)

    @Query("delete from searchTable")
    suspend fun deleteEveryQuery()










}