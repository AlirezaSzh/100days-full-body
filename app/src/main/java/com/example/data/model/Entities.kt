package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_sessions",
    indices = [Index(value = ["date", "dayNumber"], unique = true)]
)
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val dayNumber: Int,
    val volume: Double,
    val setsCompleted: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(
    tableName = "logged_sets",
    indices = [Index(value = ["sessionDate", "dayNumber", "exerciseId", "setNumber"], unique = true)]
)
data class LoggedSetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionDate: String,
    val dayNumber: Int,
    val exerciseId: String,
    val setNumber: Int,
    val weight: Float,
    val reps: Int,
    val rpe: Float? = null,
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "body_weights",
    indices = [Index(value = ["date"], unique = true)]
)
data class BodyWeightEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val weight: Float,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "program_schedule")
data class ProgramScheduleEntity(
    @PrimaryKey
    val id: Int = 1,
    val startDateIso: String = "2026-09-19", // 28 Shahrivar 1405
    val dayShiftOffset: Int = 0,             // Postponed days count
    val userName: String = "ورزشکار",
    val weightUnit: String = "kg",
    val fitnessGoal: String = "عضله‌سازی",
    val experienceLevel: String = "متوسط",
    val skippedDatesCsv: String = ""         // Dates marked as rest / postponed
)
