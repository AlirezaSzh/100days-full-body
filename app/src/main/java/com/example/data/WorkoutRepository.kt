package com.example.data

import com.example.data.dao.BodyWeightDao
import com.example.data.dao.ScheduleDao
import com.example.data.dao.WorkoutDao
import com.example.data.model.BodyWeightEntity
import com.example.data.model.LoggedSetEntity
import com.example.data.model.ProgramScheduleEntity
import com.example.data.model.WorkoutSessionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WorkoutRepository(
    private val workoutDao: WorkoutDao,
    private val bodyWeightDao: BodyWeightDao,
    private val scheduleDao: ScheduleDao
) {
    val allSessions: Flow<List<WorkoutSessionEntity>> = workoutDao.getAllSessions()
    val allWeights: Flow<List<BodyWeightEntity>> = bodyWeightDao.getAllWeights()
    val scheduleFlow: Flow<ProgramScheduleEntity?> = scheduleDao.getScheduleFlow()

    fun getLoggedSets(date: String, dayNumber: Int): Flow<List<LoggedSetEntity>> {
        return workoutDao.getLoggedSetsForDay(date, dayNumber)
    }

    suspend fun saveSet(
        date: String,
        dayNumber: Int,
        exerciseId: String,
        setNumber: Int,
        weight: Float,
        reps: Int,
        rpe: Float?,
        isCompleted: Boolean
    ) = withContext(Dispatchers.IO) {
        val entity = LoggedSetEntity(
            sessionDate = date,
            dayNumber = dayNumber,
            exerciseId = exerciseId,
            setNumber = setNumber,
            weight = weight,
            reps = reps,
            rpe = rpe,
            isCompleted = isCompleted
        )
        workoutDao.insertOrUpdateSet(entity)
    }

    suspend fun finishSession(
        date: String,
        dayNumber: Int,
        notes: String = ""
    ) = withContext(Dispatchers.IO) {
        val sets = workoutDao.getAllSetsForDate(date).filter { it.dayNumber == dayNumber }
        var totalVol = 0.0
        var completedCount = 0
        for (s in sets) {
            if (s.isCompleted || (s.reps > 0 && s.weight > 0)) {
                totalVol += (s.weight * s.reps)
                completedCount++
            }
        }
        val session = WorkoutSessionEntity(
            date = date,
            dayNumber = dayNumber,
            volume = totalVol,
            setsCompleted = completedCount,
            notes = notes
        )
        workoutDao.insertSession(session)
    }

    suspend fun recordBodyWeight(weight: Float, date: String = PersianDateUtil.todayIso()) = withContext(Dispatchers.IO) {
        bodyWeightDao.insertWeight(BodyWeightEntity(date = date, weight = weight))
    }

    suspend fun deleteBodyWeight(id: Long) = withContext(Dispatchers.IO) {
        bodyWeightDao.deleteWeight(id)
    }

    suspend fun deleteSession(sessionId: Long) = withContext(Dispatchers.IO) {
        workoutDao.deleteSession(sessionId)
    }

    suspend fun getInitialSchedule(): ProgramScheduleEntity = withContext(Dispatchers.IO) {
        var sched = scheduleDao.getScheduleDirect()
        if (sched == null) {
            sched = ProgramScheduleEntity()
            scheduleDao.insertOrUpdateSchedule(sched)
        }
        sched
    }

    suspend fun updateSchedule(schedule: ProgramScheduleEntity) = withContext(Dispatchers.IO) {
        scheduleDao.insertOrUpdateSchedule(schedule)
    }

    /**
     * Postpone current day's workout schedule by pushing it to tomorrow (+1 day shift)
     */
    suspend fun postponeScheduleByOneDay(todayDateIso: String) = withContext(Dispatchers.IO) {
        val current = getInitialSchedule()
        val skipped = current.skippedDatesCsv.split(",").filter { it.isNotBlank() }.toMutableSet()
        skipped.add(todayDateIso)
        val updated = current.copy(
            dayShiftOffset = current.dayShiftOffset + 1,
            skippedDatesCsv = skipped.joinToString(",")
        )
        scheduleDao.insertOrUpdateSchedule(updated)
    }

    suspend fun resetDayShift() = withContext(Dispatchers.IO) {
        scheduleDao.resetDayShift()
    }

    suspend fun getSuggestionForExercise(exerciseId: String): String = withContext(Dispatchers.IO) {
        val exercise = PlanData.findExercise(exerciseId) ?: return@withContext ""
        val recentSets = workoutDao.getRecentSetsForExercise(exerciseId, limit = exercise.defaultSets)
        if (recentSets.isEmpty()) {
            return@withContext "برای جلسه اول، وزنه‌ای انتخاب کن که بتوانی رنج ${exercise.reps} تکرار را با تکنیک کنترل‌شده کامل کنی."
        }
        val targetReps = parseRepRange(exercise.reps)
        val validSets = recentSets.filter { it.reps > 0 }
        if (validSets.isEmpty()) {
            return@withContext "ثبت قبلی این حرکت فاقد تکرار معتبر است؛ فرم را با تمرکز حفظ کن."
        }
        val allHitTop = validSets.size >= exercise.defaultSets && validSets.all { it.reps >= targetReps.second }
        val maxWeight = validSets.maxOfOrNull { it.weight } ?: 0f
        if (allHitTop) {
            "جلسه قبل تمام ست‌ها سقف رنج تکرار را ثبت کردی. افزایش کوچک وزنه (+1 تا 2.5 کیلوگرم) به ${maxWeight + 2.5f} پیشنهاد می‌شود."
        } else if (validSets.any { it.reps < targetReps.first }) {
            "جلسه قبل برخی ست‌ها زیر کف رنج ${targetReps.first} تکرار بود. فعلاً وزنه را زیاد نکن و روی کیفیت تکرارها تمرکز کن."
        } else {
            "همین وزنه (${maxWeight} کیلوگرم) را نگه دار و سعی کن تعداد تکرارها را به سمت ${targetReps.second} افزایش بدهی."
        }
    }

    private fun parseRepRange(repsText: String): Pair<Int, Int> {
        val clean = repsText.replace(Regex("[^0-9-]"), "")
        val parts = clean.split("-")
        return if (parts.size >= 2) {
            Pair(parts[0].toIntOrNull() ?: 6, parts[1].toIntOrNull() ?: 12)
        } else {
            val single = clean.toIntOrNull() ?: 8
            Pair(single, single)
        }
    }

    /**
     * Calculates program days since start date considering shifts
     */
    fun calculateProgramWeekAndDay(startDateIso: String, dayShiftOffset: Int): Pair<Int, Int> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val startCal = Calendar.getInstance()
        try {
            startCal.time = sdf.parse(startDateIso) ?: Date()
        } catch (e: Exception) {
            startCal.time = Date()
        }
        val nowCal = Calendar.getInstance()
        nowCal.set(Calendar.HOUR_OF_DAY, 0)
        nowCal.set(Calendar.MINUTE, 0)
        nowCal.set(Calendar.SECOND, 0)
        nowCal.set(Calendar.MILLISECOND, 0)

        // Adjust effective start date by shift offset
        startCal.add(Calendar.DAY_OF_YEAR, dayShiftOffset)

        val diffMillis = nowCal.timeInMillis - startCal.timeInMillis
        val diffDays = (diffMillis / (1000 * 60 * 60 * 24)).toInt()

        val weekNum = if (diffDays >= 0) (diffDays / 7) + 1 else 1
        // Day of 5-day cycle:
        val dayInCycle = if (diffDays >= 0) {
            (diffDays % 7) + 1
        } else {
            1
        }
        return Pair(weekNum, dayInCycle)
    }

    /**
     * Export complete application data to a clean JSON string
     */
    suspend fun exportAllDataToJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("app", "BWS Training Hub")
        root.put("version", "1.0")
        root.put("exportedAt", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date()))

        // Schedule
        val schedule = getInitialSchedule()
        val schedJson = JSONObject()
        schedJson.put("startDateIso", schedule.startDateIso)
        schedJson.put("dayShiftOffset", schedule.dayShiftOffset)
        schedJson.put("userName", schedule.userName)
        schedJson.put("weightUnit", schedule.weightUnit)
        schedJson.put("fitnessGoal", schedule.fitnessGoal)
        schedJson.put("experienceLevel", schedule.experienceLevel)
        schedJson.put("skippedDatesCsv", schedule.skippedDatesCsv)
        root.put("profile", schedJson)

        // Sessions
        val sessions = allSessions.first()
        val sessionsArr = JSONArray()
        for (s in sessions) {
            val obj = JSONObject()
            obj.put("date", s.date)
            obj.put("dayNumber", s.dayNumber)
            obj.put("volume", s.volume)
            obj.put("setsCompleted", s.setsCompleted)
            obj.put("timestamp", s.timestamp)
            obj.put("notes", s.notes)
            sessionsArr.put(obj)
        }
        root.put("sessions", sessionsArr)

        // Logged Sets
        val allSets = workoutDao.getAllSetsDirect()
        val setsArr = JSONArray()
        for (ls in allSets) {
            val obj = JSONObject()
            obj.put("sessionDate", ls.sessionDate)
            obj.put("dayNumber", ls.dayNumber)
            obj.put("exerciseId", ls.exerciseId)
            obj.put("setNumber", ls.setNumber)
            obj.put("weight", ls.weight)
            obj.put("reps", ls.reps)
            obj.put("rpe", ls.rpe ?: JSONObject.NULL)
            obj.put("isCompleted", ls.isCompleted)
            obj.put("timestamp", ls.timestamp)
            setsArr.put(obj)
        }
        root.put("sets", setsArr)

        // Body Weights
        val weights = allWeights.first()
        val weightsArr = JSONArray()
        for (w in weights) {
            val obj = JSONObject()
            obj.put("date", w.date)
            obj.put("weight", w.weight)
            obj.put("timestamp", w.timestamp)
            weightsArr.put(obj)
        }
        root.put("bodyWeights", weightsArr)

        root.toString(2)
    }

    /**
     * Import and restore data from a JSON string
     */
    suspend fun importDataFromJson(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)

            if (root.has("profile")) {
                val p = root.getJSONObject("profile")
                val sched = ProgramScheduleEntity(
                    startDateIso = p.optString("startDateIso", "2026-09-19"),
                    dayShiftOffset = p.optInt("dayShiftOffset", 0),
                    userName = p.optString("userName", "ورزشکار"),
                    weightUnit = p.optString("weightUnit", "kg"),
                    fitnessGoal = p.optString("fitnessGoal", "عضله‌سازی"),
                    experienceLevel = p.optString("experienceLevel", "متوسط"),
                    skippedDatesCsv = p.optString("skippedDatesCsv", "")
                )
                scheduleDao.insertOrUpdateSchedule(sched)
            }

            if (root.has("sessions")) {
                val arr = root.getJSONArray("sessions")
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val s = WorkoutSessionEntity(
                        date = o.getString("date"),
                        dayNumber = o.getInt("dayNumber"),
                        volume = o.getDouble("volume"),
                        setsCompleted = o.getInt("setsCompleted"),
                        timestamp = o.optLong("timestamp", System.currentTimeMillis()),
                        notes = o.optString("notes", "")
                    )
                    workoutDao.insertSession(s)
                }
            }

            if (root.has("sets")) {
                val arr = root.getJSONArray("sets")
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val rpeVal = if (o.isNull("rpe")) null else o.optDouble("rpe").toFloat()
                    val s = LoggedSetEntity(
                        sessionDate = o.getString("sessionDate"),
                        dayNumber = o.getInt("dayNumber"),
                        exerciseId = o.getString("exerciseId"),
                        setNumber = o.getInt("setNumber"),
                        weight = o.getDouble("weight").toFloat(),
                        reps = o.getInt("reps"),
                        rpe = rpeVal,
                        isCompleted = o.optBoolean("isCompleted", false),
                        timestamp = o.optLong("timestamp", System.currentTimeMillis())
                    )
                    workoutDao.insertOrUpdateSet(s)
                }
            }

            if (root.has("bodyWeights")) {
                val arr = root.getJSONArray("bodyWeights")
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    val w = BodyWeightEntity(
                        date = o.getString("date"),
                        weight = o.getDouble("weight").toFloat(),
                        timestamp = o.optLong("timestamp", System.currentTimeMillis())
                    )
                    bodyWeightDao.insertWeight(w)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Generate a shareable formatted Persian text report
     */
    suspend fun generateShareableReport(): String = withContext(Dispatchers.IO) {
        val schedule = getInitialSchedule()
        val sessions = allSessions.first()
        val latestWeight = bodyWeightDao.getLatestWeight()

        val totalVolume = sessions.sumOf { it.volume }
        val persianStart = PersianDateUtil.toPersianDisplayDate(schedule.startDateIso)
        val todayPersian = PersianDateUtil.toPersianDisplayDate(PersianDateUtil.todayIso())

        val sb = StringBuilder()
        sb.append("📋 گزارش وضعیت تمرین — BWS Training Hub\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("👤 ورزشکار: ${schedule.userName}\n")
        sb.append("📅 تاریخ شروع برنامه: $persianStart\n")
        sb.append("🗓 تاریخ گزارش: $todayPersian\n")
        sb.append("🎯 هدف: ${schedule.fitnessGoal} (${schedule.experienceLevel})\n")
        if (schedule.dayShiftOffset > 0) {
            sb.append("↷ روزهای انتقال‌یافته به فردا: ${PersianDateUtil.toPersianDigits(schedule.dayShiftOffset)} روز\n")
        }
        sb.append("🏋️ کل جلسات ثبت‌شده: ${PersianDateUtil.toPersianDigits(sessions.size)}\n")
        sb.append("📈 مجموع حجم تمرین: ${PersianDateUtil.toPersianDigits(totalVolume.toInt())} ${schedule.weightUnit}\n")
        if (latestWeight != null) {
            sb.append("⚖️ آخرین وزن بدن: ${PersianDateUtil.toPersianDigits(latestWeight.weight)} ${schedule.weightUnit}\n")
        }
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("برنامه تمرینی بر پایه متد ۵ روزه فول‌بادی\n")
        sb.toString()
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        workoutDao.clearSessions()
        workoutDao.clearSets()
        bodyWeightDao.clearWeights()
        scheduleDao.resetDayShift()
    }
}
