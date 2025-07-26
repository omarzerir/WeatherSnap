package com.zerir.weathersnap.domain.repository

import androidx.paging.PagingData
import com.zerir.weathersnap.data.entity.CapturedImageEntity
import com.zerir.weathersnap.domain.model.CapturedImage
import com.zerir.weathersnap.domain.model.UiState
import kotlinx.coroutines.flow.Flow

interface ImageHistoryRepository {
    fun getAllImagesPaginated(): Flow<PagingData<CapturedImageEntity>>
    suspend fun saveImage(capturedImage: CapturedImage, isCelsius: Boolean): UiState<Unit>
    suspend fun deleteImage(imageId: String, filePath: String): UiState<Unit>
    suspend fun deleteAllImages(): UiState<Unit>
}