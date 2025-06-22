package ru.fefu.activitiesfefu.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [ActivityEntity::class, ActivityUserEntity::class], // <-- обязательно обе сущности!
    version = 3 // <-- увеличиваем версию из-за добавления индексов
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun userDao(): ActivityUserDao
    
    companion object {
        const val DATABASE_NAME = "activity_database"
    }
} 