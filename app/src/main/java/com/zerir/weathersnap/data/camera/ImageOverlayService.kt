package com.zerir.weathersnap.data.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.core.graphics.toColorInt
import com.zerir.weathersnap.domain.model.WeatherOverlayData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface ImageOverlayService {
    suspend fun addWeatherOverlay(originalImageFile: File, overlayData: WeatherOverlayData): File
}

@Singleton
class ImageOverlayServiceImpl @Inject constructor() : ImageOverlayService {

    override suspend fun addWeatherOverlay(
        originalImageFile: File,
        overlayData: WeatherOverlayData
    ): File = withContext(Dispatchers.IO) {

        // Load original image
        val originalBitmap = BitmapFactory.decodeFile(originalImageFile.absolutePath)
            ?: throw Exception("Cannot decode image file")

        // Create mutable copy
        val overlayBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Draw weather overlay
        val canvas = Canvas(overlayBitmap)
        drawWeatherOverlay(canvas, overlayBitmap.width, overlayBitmap.height, overlayData)

        // Save new image (non-blocking)
        val overlayFile = File(
            originalImageFile.parent,
            "weather_${originalImageFile.name}"
        )

        overlayFile.outputStream().use { out ->
            overlayBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }

        // Clean up
        originalBitmap.recycle()
        overlayBitmap.recycle()

        overlayFile
    }

    private fun drawWeatherOverlay(
        canvas: Canvas,
        width: Int,
        height: Int,
        data: WeatherOverlayData
    ) {
        val padding = (width * 0.05f).toInt() // 5% padding
        val overlayHeight = (height * 0.25f).toInt() // 25% of image height
        val overlayWidth = width - (padding * 2)

        // Draw semi-transparent background
        val backgroundPaint = Paint().apply {
            color = "#CC000000".toColorInt()
            isAntiAlias = true
        }

        val backgroundRect = RectF(
            padding.toFloat(),
            (height - overlayHeight - padding).toFloat(),
            (width - padding).toFloat(),
            (height - padding).toFloat()
        )

        canvas.drawRoundRect(backgroundRect, 20f, 20f, backgroundPaint)

        // Text paints
        val titlePaint = Paint().apply {
            color = "#FFFFFF".toColorInt()
            textSize = (overlayHeight * 0.2f)
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        val detailPaint = Paint().apply {
            color = "#FFFFFF".toColorInt()
            textSize = (overlayHeight * 0.12f)
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }

        // Calculate text positions
        val textStartX = padding + 20f
        val textStartY = height - overlayHeight - padding + (overlayHeight * 0.2f)

        // Draw temperature (large)
        canvas.drawText(
            "${data.temperature}${data.temperatureUnit}",
            textStartX,
            textStartY,
            titlePaint
        )

        // Draw condition
        canvas.drawText(
            data.condition,
            textStartX,
            textStartY + (overlayHeight * 0.25f),
            detailPaint
        )

        // Draw location
        canvas.drawText(
            "📍 ${data.locationName}",
            textStartX,
            textStartY + (overlayHeight * 0.4f),
            detailPaint
        )

        // Draw humidity and wind (side by side)
        canvas.drawText(
            "💧 ${data.humidity}",
            textStartX,
            textStartY + (overlayHeight * 0.55f),
            detailPaint
        )

        canvas.drawText(
            "💨 ${data.windSpeed}",
            textStartX + (overlayWidth * 0.35f),
            textStartY + (overlayHeight * 0.55f),
            detailPaint
        )

        // Draw timestamp
        canvas.drawText(
            data.timestamp,
            textStartX,
            textStartY + (overlayHeight * 0.7f),
            detailPaint
        )
    }
}