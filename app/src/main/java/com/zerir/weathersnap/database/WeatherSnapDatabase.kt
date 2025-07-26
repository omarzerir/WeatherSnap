package com.zerir.weathersnap.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zerir.weathersnap.data.entity.CapturedImageEntity
import com.zerir.weathersnap.data.localDatasource.CapturedImageDao

@Database(
    entities = [CapturedImageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherSnapDatabase : RoomDatabase() {

    abstract fun capturedImageDao(): CapturedImageDao

    companion object {
        const val DATABASE_NAME = "weather_snap_database"
    }
}