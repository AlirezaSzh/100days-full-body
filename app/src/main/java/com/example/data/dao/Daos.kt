package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.model.BodyWeightEntity
import com.example.data.model.LoggedSetEntity
import com.example.data.model.ProgramScheduleEntity
import com.example.data.model.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Query("SELECT * FROM workout_sessions ORDER BY date DESC, timestamp DESC")
    fun getAllSessions(): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions WHERE date = :date LIMIT 1")
    suspend fun getSessionByDate(date: String): WorkoutSessionEntity?

    @Query("SELECT * FROM workout_sessions WHERE dayNumber = :dayNumber ORDER BY date DESC LIMIT 1")
    suspend fun getLastSessionForDay(dayNumber: Int): WorkoutSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSessionEntity): Long

    @Query("DELETE FROM workout_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long)

    @Query("SELECT * FROM logged_sets WHERE sessionDate = :date AND dayNumber = :dayNumber ORDER BY exerciseId, setNumber")
    fun getLoggedSetsForDay(date: String, dayNumber: Int): Flow<List<LoggedSetEntity>>

    @Query("SELECT * FROM logged_sets WHERE exerciseId = :exerciseId AND reps > 0 ORDER BY sessionDate DESC, timestamp DESC LIMIT :limit")
    suspend fun getRecentSetsForExercise(exerciseId: String, limit: Int = 10): List<LoggedSetEntity>

    @Query("SELECT * FROM logged_sets WHERE sessionDate = :date")
    suspend fun getAllSetsForDate(date: String): List<LoggedSetEntity>

    @Query("SELECT * FROM logged_sets")
    suspend fun getAllSetsDirect(): List<LoggedSetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSet(set: LoggedSetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<LoggedSetEntity>)

    @Query("DELETE FROM logged_sets WHERE sessionDate = :date AND dayNumber = :dayNumber")
    suspend fun deleteSetsForSession(date: String, dayNumber: Int)

    @Query("DELETE FROM workout_sessions")
    suspend fun clearSessions()

    @Query("DELETE FROM logged_sets")
    suspend fun clearSets()
}

@Dao
interface BodyWeightDao {
    @Query("SELECT * FROM body_weights ORDER BY date DESC")
    fun getAllWeights(): Flow<List<BodyWeightEntity>>

    @Query("SELECT * FROM body_weights ORDER BY date DESC LIMIT 1")
    suspend fun getLatestWeight(): BodyWeightEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeight(entry: BodyWeightEntity)

    @Query("DELETE FROM body_weights WHERE id = :id")
    suspend fun deleteWeight(id: Long)

    @Query("DELETE FROM body_weights")
    suspend fun clearWeights()
}

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM program_schedule WHERE id = 1 LIMIT 1")
    fun getScheduleFlow(): Flow<ProgramScheduleEntity?>

    @Query("SELECT * FROM program_schedule WHERE id = 1 LIMIT 1")
    suspend fun getScheduleDirect(): ProgramScheduleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSchedule(schedule: ProgramScheduleEntity)

    @Query("UPDATE program_schedule SET dayShiftOffset = dayShiftOffset + :delta WHERE id = 1")
    suspend fun adjustDayShift(delta: Int)

    @Query("UPDATE program_schedule SET dayShiftOffset = 0, skippedDatesCsv = '' WHERE id = 1")
    suspend fun resetDayShift()
}
