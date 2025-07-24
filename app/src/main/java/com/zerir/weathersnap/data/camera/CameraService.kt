package com.zerir.weathersnap.data.camera

import android.content.Context
import android.os.Environment
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import com.zerir.weathersnap.domain.model.Weather
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface CameraService {
    suspend fun capturePhoto(imageCapture: ImageCapture): File
    suspend fun overlayWeatherOnImage(imageFile: File, weather: Weather): File
}

@Singleton
class CameraServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CameraService {

    override suspend fun capturePhoto(imageCapture: ImageCapture): File =
        suspendCancellableCoroutine { continuation ->
            val outputDirectory = getOutputDirectory()
            val photoFile = File(
                outputDirectory,
                SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
                    .format(System.currentTimeMillis()) + ".jpg"
            )

            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            imageCapture.takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        continuation.resume(photoFile)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        continuation.resumeWithException(exception)
                    }
                }
            )
        }

    override suspend fun overlayWeatherOnImage(imageFile: File, weather: Weather): File {
        // TODO: Implement weather overlay using Canvas/Bitmap
        // For now, return the original file
        return imageFile
    }

    private fun getOutputDirectory(): File {
        val picturesDir =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "WeatherSnap")
        if (!picturesDir.exists()) {
            picturesDir.mkdirs()
        }
        return picturesDir
    }
}