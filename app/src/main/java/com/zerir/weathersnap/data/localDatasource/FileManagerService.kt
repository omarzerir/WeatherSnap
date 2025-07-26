package com.zerir.weathersnap.data.localDatasource

import android.content.Context
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface FileManagerService {
    suspend fun getImagesDirectory(): File
    suspend fun deleteImageFile(filePath: String): Boolean
    suspend fun deleteAllImageFiles(): Boolean
}

@Singleton
class FileManagerServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FileManagerService {

    override suspend fun getImagesDirectory(): File = withContext(Dispatchers.IO) {
        val picturesDir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "WeatherSnap"
        )
        if (!picturesDir.exists()) {
            picturesDir.mkdirs()
        }
        picturesDir
    }

    override suspend fun deleteImageFile(filePath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(filePath)
            file.exists() && file.delete()
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteAllImageFiles(): Boolean = withContext(Dispatchers.IO) {
        try {
            val imagesDir = getImagesDirectory()
            val files = imagesDir.listFiles() ?: return@withContext true

            files.all { file ->
                !file.isFile || file.delete()
            }
        } catch (e: Exception) {
            false
        }
    }
}