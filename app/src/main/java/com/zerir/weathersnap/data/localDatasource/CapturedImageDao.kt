package com.zerir.weathersnap.data.localDatasource

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zerir.weathersnap.data.entity.CapturedImageEntity

@Dao
interface CapturedImageDao {

    @Query("SELECT * FROM captured_images ORDER BY timestamp DESC")
    fun getAllImagesPaginated(): PagingSource<Int, CapturedImageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: CapturedImageEntity)

    @Query("DELETE FROM captured_images WHERE id = :id")
    suspend fun deleteImageById(id: String)

    @Query("DELETE FROM captured_images")
    suspend fun deleteAllImages()
}