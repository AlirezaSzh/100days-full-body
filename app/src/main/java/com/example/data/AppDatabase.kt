package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.BodyWeightDao
import com.example.data.dao.ScheduleDao
import com.example.data.dao.WorkoutDao
import com.example.data.model.BodyWeightEntity
import com.example.data.model.LoggedSetEntity
import com.example.data.model.ProgramScheduleEntity
import com.example.data.model.WorkoutSessionEntity

@Database(
    entities = [
        WorkoutSessionEntity::class,
        LoggedSetEntity::class,
        BodyWeightEntity::class,
        ProgramScheduleEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
    abstract fun bodyWeightDao(): BodyWeightDao
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bws_training_hub.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
