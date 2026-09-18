package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.PersianDateUtil
import com.example.data.PlanData
import com.example.data.WorkoutRepository
import com.example.data.model.BodyWeightEntity
import com.example.data.model.LoggedSetEntity
import com.example.data.model.ProgramScheduleEntity
import com.example.data.model.WorkoutSessionEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class AppScreen(val title: String, val iconName: String) {
    DASHBOARD("داشبورد", "dashboard"),
    WORKOUT("تمرین امروز", "workout"),
    PLAN("برنامه ۵ روزه", "plan"),
    LIBRARY("حرکات و جایگزین‌ها", "library"),
    PROGRESS("پیشرفت و سابقه", "progress"),
    COACH("مربی و دستیار", "coach"),
    SETTINGS("تنظیمات", "settings")
}

data class TimerState(
    val remainingSeconds: Int = 0,
    val totalSeconds: Int = 120,
    val isRunning: Boolean = false,
    val isVisible: Boolean = false,
    val exerciseName: String = ""
)

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WorkoutRepository
    private var timerJob: Job? = null

    val currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val selectedDayNumber = MutableStateFlow(1)

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    val sessions: StateFlow<List<WorkoutSessionEntity>>
    val bodyWeights: StateFlow<List<BodyWeightEntity>>
    val schedule: StateFlow<ProgramScheduleEntity?>

    // Key format: "date|day|exerciseId|setNum"
    val loggedSetsMap = MutableStateFlow<Map<String, LoggedSetEntity>>(emptyMap())

    // Chat
    val chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())

    init {
        val db = AppDatabase.getDatabase(application)
        repository = WorkoutRepository(db.workoutDao(), db.bodyWeightDao(), db.scheduleDao())

        sessions = repository.allSessions.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        bodyWeights = repository.allWeights.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        schedule = repository.scheduleFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

        viewModelScope.launch {
            repository.getInitialSchedule()
            refreshCurrentDaySelection()
            loadTodayLoggedSets()
        }

        initCoachWelcomeMessage()
    }

    private fun initCoachWelcomeMessage() {
        chatMessages.value = listOf(
            ChatMessage(
                id = "welcome",
                text = "سلام! من مربی هوشمند برنامه ۵ روزه فول‌بادی BWS هستم. درباره روزهای تمرین، انتخاب وزنه، جایگزین دستگاه‌های اشغال، تایمر استراحت و انتقال جلسات به فردا هر سوالی داری در خدمتم.",
                isUser = false
            )
        )
    }

    fun setScreen(screen: AppScreen) {
        currentScreen.value = screen
    }

    fun selectDay(dayNumber: Int) {
        selectedDayNumber.value = dayNumber
        loadTodayLoggedSets()
    }

    private fun refreshCurrentDaySelection() {
        val sched = schedule.value ?: ProgramScheduleEntity()
        val (_, cycleDay) = repository.calculateProgramWeekAndDay(
            sched.startDateIso,
            sched.dayShiftOffset
        )
        val defaultDay = if (cycleDay in 1..5) cycleDay else 1
        selectedDayNumber.value = defaultDay
    }

    fun loadTodayLoggedSets() {
        viewModelScope.launch {
            val today = PersianDateUtil.todayIso()
            val day = selectedDayNumber.value
            repository.getLoggedSets(today, day).collect { sets ->
                val map = mutableMapOf<String, LoggedSetEntity>()
                for (s in sets) {
                    map["${s.sessionDate}|${s.dayNumber}|${s.exerciseId}|${s.setNumber}"] = s
                }
                loggedSetsMap.value = map
            }
        }
    }

    fun onSetUpdated(
        exerciseId: String,
        setNumber: Int,
        weight: Float,
        reps: Int,
        rpe: Float?,
        isCompleted: Boolean,
        autoTriggerTimer: Boolean = true
    ) {
        val today = PersianDateUtil.todayIso()
        val day = selectedDayNumber.value

        viewModelScope.launch {
            repository.saveSet(
                date = today,
                dayNumber = day,
                exerciseId = exerciseId,
                setNumber = setNumber,
                weight = weight,
                reps = reps,
                rpe = rpe,
                isCompleted = isCompleted
            )

            if (isCompleted && autoTriggerTimer) {
                val exercise = PlanData.findExercise(exerciseId)
                val rest = exercise?.restSeconds ?: 120
                startRestTimer(rest, exercise?.persianName ?: exercise?.name ?: "استراحت بین ست")
            }
        }
    }

    fun finishWorkoutSession(notes: String = "") {
        viewModelScope.launch {
            val today = PersianDateUtil.todayIso()
            val day = selectedDayNumber.value
            repository.finishSession(today, day, notes)
            _toastEvent.emit("جلسه تمرین با موفقیت ذخیره شد!")
            currentScreen.value = AppScreen.PROGRESS
        }
    }

    // --- Rest Timer Control ---

    fun startRestTimer(seconds: Int, exerciseName: String = "استراحت") {
        timerJob?.cancel()
        _timerState.value = TimerState(
            remainingSeconds = seconds,
            totalSeconds = seconds,
            isRunning = true,
            isVisible = true,
            exerciseName = exerciseName
        )

        timerJob = viewModelScope.launch {
            while (_timerState.value.remainingSeconds > 0) {
                delay(1000)
                if (_timerState.value.isRunning) {
                    val next = _timerState.value.remainingSeconds - 1
                    _timerState.value = _timerState.value.copy(remainingSeconds = next)
                    if (next == 0) {
                        triggerVibration()
                        _toastEvent.emit("⏱ زمان استراحت تمام شد! آماده ست بعدی شو.")
                    }
                }
            }
            _timerState.value = _timerState.value.copy(isRunning = false)
        }
    }

    fun pauseResumeTimer() {
        val cur = _timerState.value
        _timerState.value = cur.copy(isRunning = !cur.isRunning)
    }

    fun adjustTimer(deltaSeconds: Int) {
        val cur = _timerState.value
        val newRem = (cur.remainingSeconds + deltaSeconds).coerceAtLeast(0)
        _timerState.value = cur.copy(remainingSeconds = newRem)
    }

    fun dismissTimer() {
        timerJob?.cancel()
        _timerState.value = _timerState.value.copy(isVisible = false, isRunning = false)
    }

    private fun triggerVibration() {
        val context = getApplication<Application>()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 200, 100, 300), -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 200, 100, 300), -1)
            }
        } catch (e: Exception) {
            // Non-fatal if device doesn't have vibrator
        }
    }

    // --- Postponement / Day Shift Feature ---

    fun postponeTodayWorkout() {
        viewModelScope.launch {
            val today = PersianDateUtil.todayIso()
            repository.postponeScheduleByOneDay(today)
            refreshCurrentDaySelection()
            loadTodayLoggedSets()
            _toastEvent.emit("برنامه تمرین با موفقیت به فردا منتقل شد!")
        }
    }

    fun resetScheduleShifts() {
        viewModelScope.launch {
            repository.resetDayShift()
            refreshCurrentDaySelection()
            loadTodayLoggedSets()
            _toastEvent.emit("جابجایی‌های تقویم به حالت اولیه بازگشت.")
        }
    }

    // --- Weight & Progress ---

    fun recordWeight(weight: Float) {
        viewModelScope.launch {
            repository.recordBodyWeight(weight)
            _toastEvent.emit("وزن جدید ثبت شد: ${PersianDateUtil.toPersianDigits(weight)} ${schedule.value?.weightUnit ?: "kg"}")
        }
    }

    fun deleteWeight(id: Long) {
        viewModelScope.launch {
            repository.deleteBodyWeight(id)
            _toastEvent.emit("رکورد وزن حذف شد.")
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
            _toastEvent.emit("جلسه تمرین حذف شد.")
        }
    }

    // --- Settings & Export ---

    fun updateProfile(
        userName: String,
        startDateIso: String,
        weightUnit: String,
        fitnessGoal: String,
        experienceLevel: String
    ) {
        viewModelScope.launch {
            val cur = schedule.value ?: ProgramScheduleEntity()
            val updated = cur.copy(
                userName = userName,
                startDateIso = startDateIso,
                weightUnit = weightUnit,
                fitnessGoal = fitnessGoal,
                experienceLevel = experienceLevel
            )
            repository.updateSchedule(updated)
            refreshCurrentDaySelection()
            _toastEvent.emit("تنظیمات با موفقیت ذخیره شد!")
        }
    }

    suspend fun getExportJson(): String {
        return repository.exportAllDataToJson()
    }

    fun importJsonBackup(jsonString: String) {
        viewModelScope.launch {
            val ok = repository.importDataFromJson(jsonString)
            if (ok) {
                refreshCurrentDaySelection()
                loadTodayLoggedSets()
                _toastEvent.emit("داده‌ها با موفقیت بازیابی شدند!")
            } else {
                _toastEvent.emit("خطا در خواندن فایل پشتیبان JSON.")
            }
        }
    }

    suspend fun getShareableTextSummary(): String {
        return repository.generateShareableReport()
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            refreshCurrentDaySelection()
            loadTodayLoggedSets()
            _toastEvent.emit("همه داده‌ها پاک شدند.")
        }
    }

    // --- Coach Chat Logic ---

    fun sendCoachQuery(query: String) {
        val q = query.trim()
        if (q.isBlank()) return

        val userMsg = ChatMessage(id = System.currentTimeMillis().toString(), text = q, isUser = true)
        chatMessages.value = chatMessages.value + userMsg

        val botReplyText = generateOfflineCoachResponse(q)
        val botMsg = ChatMessage(id = (System.currentTimeMillis() + 1).toString(), text = botReplyText, isUser = false)
        chatMessages.value = chatMessages.value + botMsg
    }

    private fun generateOfflineCoachResponse(query: String): String {
        val lower = query.lowercase()
        val curDay = selectedDayNumber.value
        val dayPlan = PlanData.getDay(curDay)

        return when {
            lower.contains("امروز") || lower.contains("تمرین") -> {
                "امروز طبق برنامه، نوبت ${dayPlan.title} (${dayPlan.subtitle}) است. حرکات این جلسه: " +
                        dayPlan.exercises.joinToString("، ") { it.persianName } +
                        ". اگر وقت نداری یا شرایط مساعد نیست، با دکمه «انتقال به فردا» برنامه را به روز بعد منتقل کن!"
            }
            lower.contains("وزنه") || lower.contains("افزایش") || lower.contains("سنگین") -> {
                "منطق پیشرفت برنامه بر پایه Double Progression است: ابتدا در یک وزنه مشخص، تلاش کن به بالاترین تکرار رنج هدف برسی. وقتی تمام ست‌ها با فرم تمیز به سقف رنج رسیدند، در جلسه بعد ۲.۵ کیلوگرم به وزنه اضافه کن."
            }
            lower.contains("هفته اول") || lower.contains("هفته دوم") || lower.contains("شروع") || lower.contains("سازگاری") -> {
                "طبق دستورالعمل PDF کاربر، در دو هفته اول برنامه در فاز سازگاری هستی. ست‌ها را به ناتوانی نزدیک نکن و وزنه سبک‌تری انتخاب کن تا عضلات و تاندون‌ها بدون کوفتگی شدید به ساختار ۵ روزه عادت کنند."
            }
            lower.contains("جابجایی") || lower.contains("فردا") || lower.contains("نرسیدم") || lower.contains("عقب") -> {
                "اگر یک روز به تمرین نرسیدی، اصلاً نگران نباش! از قابلیت «انتقال به فردا» استفاده کن تا تقویم برنامه به شکل هوشمند یک روز به جلو شیفت پیدا کند و هیچ جلسه‌ای را نسوزانی."
            }
            lower.contains("استراحت") || lower.contains("تایمر") -> {
                "زمان استراحت برای حرکات ترکیبی سنگین (اسکوات، پرس سینه، ددلیفت) ۲ تا ۳ دقیقه است و برای حرکات تک‌مفصلی (جلو بازو، نشر جانب) ۱ تا ۱.۵ دقیقه. با تیک زدن هر ست، تایمر شناور پایین به صورت خودکار فعال می‌شود."
            }
            lower.contains("اشغال") || lower.contains("جایگزین") -> {
                "برای هر یک از ۲۷ حرکت این برنامه، جایگزین‌های رسمی صفحه ۴۳ تا ۴۷ PDF در بخش «حرکات و جایگزین‌ها» قرار دارد. مثلاً اگر دستگاه پرس پا اشغال بود، اسکوات با هالتر یا اسمیت را جایگزین کن."
            }
            else -> {
                "من تمام ۲۷ حرکت برنامه ۵ روزه Full Body، زمان‌های استراحت، راهنمای تکنیک، جابجایی روزها و فاز سازگاری را می‌دانم. نام حرکت یا مبحث مدنظرت را بگو تا راهنمایی‌ات کنم."
            }
        }
    }
}
