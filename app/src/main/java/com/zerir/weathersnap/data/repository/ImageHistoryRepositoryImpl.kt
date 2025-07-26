package com.zerir.weathersnap.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.zerir.weathersnap.data.entity.CapturedImageEntity
import com.zerir.weathersnap.data.localDatasource.CapturedImageDao
import com.zerir.weathersnap.data.localDatasource.FileManagerService
import com.zerir.weathersnap.domain.model.CapturedImage
import com.zerir.weathersnap.domain.model.UiState
import com.zerir.weathersnap.domain.repository.ImageHistoryRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageHistoryRepositoryImpl @Inject constructor(
    private val capturedImageDao: CapturedImageDao,
    private val fileManagerService: FileManagerService
) : ImageHistoryRepository {

    override fun getAllImagesPaginated(): Flow<PagingData<CapturedImageEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 6,
                enablePlaceholders = true,
                initialLoadSize = 20,
            ),
            pagingSourceFactory = { capturedImageDao.getAllImagesPaginated() }
        ).flow
    }

    override suspend fun saveImage(capturedImage: CapturedImage, isCelsius: Boolean): UiState<Unit> {
        return try {
            val entity = CapturedImageEntity(
                id = UUID.randomUUID().toString(),
                filePath = capturedImage.imageFile.absolutePath,
                fileName = capturedImage.imageFile.name,
                locationName = capturedImage.weather.locationName,
                latitude = capturedImage.coordinates.latitude,
                longitude = capturedImage.coordinates.longitude,
                temperatureCelsius = capturedImage.weather.temperatureCelsius,
                temperatureFahrenheit = capturedImage.weather.temperatureFahrenheit,
                weatherDescription = capturedImage.weather.description,
                humidity = capturedImage.weather.humidity,
                windSpeedKph = capturedImage.weather.windSpeedKph,
                iconUrl = capturedImage.weather.iconUrl,
                timestamp = capturedImage.timestamp,
                fileSize = capturedImage.imageFile.length(),
                isCelsius = isCelsius
            )

            capturedImageDao.insertImage(entity)
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Failed to save image to database")
        }
    }

    override suspend fun deleteImage(imageId: String, filePath: String): UiState<Unit> {
        return try {
            capturedImageDao.deleteImageById(imageId)
            fileManagerService.deleteImageFile(filePath)
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Failed to delete image")
        }
    }

    override suspend fun deleteAllImages(): UiState<Unit> {
        return try {
            capturedImageDao.deleteAllImages()
            fileManagerService.deleteAllImageFiles()
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Failed to delete all images")
        }
    }
}