package ru.fefu.activitiesfefu

import android.app.Application
import androidx.room.Room
import ru.fefu.activitiesfefu.data.AppDatabase

class ActivityApplication : Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
    }
} 