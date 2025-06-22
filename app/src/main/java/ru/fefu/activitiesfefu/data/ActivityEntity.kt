package ru.fefu.activitiesfefu.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val type: ActivityType,
    val startDate: Date,
    val endDate: Date,
    val distance: Float,
    val durationMillis: Long,
    val coordinates: List<Coordinates>
) 