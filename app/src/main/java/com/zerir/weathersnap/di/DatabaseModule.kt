package com.zerir.weathersnap.di

import android.content.Context
import androidx.room.Room
import com.zerir.weathersnap.data.localDatasource.CapturedImageDao
import com.zerir.weathersnap.database.WeatherSnapDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideWeatherSnapDatabase(
        @ApplicationContext context: Context
    ): WeatherSnapDatabase {
        return Room.databaseBuilder(
            context,
            WeatherSnapDatabase::class.java,
            WeatherSnapDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideCapturedImageDao(database: WeatherSnapDatabase): CapturedImageDao {
        return database.capturedImageDao()
    }
}