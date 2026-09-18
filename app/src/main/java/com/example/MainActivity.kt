package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.PersianDateUtil
import com.example.data.PlanData
import com.example.ui.components.AppBottomNavigationBar
import com.example.ui.components.AppTopBar
import com.example.ui.components.DayShiftDialog
import com.example.ui.components.ExerciseDetailDialog
import com.example.ui.components.RestTimerDock
import com.example.ui.screens.CoachScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.PlanScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WorkoutScreen
import com.example.ui.theme.BWSTrainingTheme
import com.example.ui.theme.CanvasBg
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BWSTrainingTheme {
                // Persian language right-to-left layout direction
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    BWSTrainingApp()
                }
            }
        }
    }
}

@Composable
fun BWSTrainingApp(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedDay by viewModel.selectedDayNumber.collectAsState()
    val schedule by viewModel.schedule.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val bodyWeights by viewModel.bodyWeights.collectAsState()
    val loggedSets by viewModel.loggedSetsMap.collectAsState()
    val timerState by viewModel.timerState.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()

    var showDayShiftDialog by remember { mutableStateOf(false) }
    var detailExercise by remember { mutableStateOf<PlanData.Exercise?>(null) }

    // Listen to toasts
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    val todayIso = remember { PersianDateUtil.todayIso() }
    val todayDisplayDate = remember(todayIso) { PersianDateUtil.toPersianDisplayDate(todayIso) }
    val todayWeekday = remember(todayIso) { PersianDateUtil.getPersianWeekdayName(todayIso) }
    val greeting = "$todayWeekday $todayDisplayDate"

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(CanvasBg),
        topBar = {
            AppTopBar(
                currentScreen = currentScreen,
                greeting = greeting,
                userName = schedule?.userName ?: "ورزشکار",
                onOpenShiftDialog = { showDayShiftDialog = true },
                onOpenSettings = { viewModel.setScreen(AppScreen.SETTINGS) },
                onOpenSearch = { viewModel.setScreen(AppScreen.LIBRARY) }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Floating/Docked Rest Timer
                RestTimerDock(
                    state = timerState,
                    onPauseResume = { viewModel.pauseResumeTimer() },
                    onAdjust = { delta -> viewModel.adjustTimer(delta) },
                    onDismiss = { viewModel.dismissTimer() }
                )

                // Navigation bar
                AppBottomNavigationBar(
                    currentScreen = currentScreen,
                    onSelectScreen = { screen -> viewModel.setScreen(screen) }
                )
            }
        },
        containerColor = CanvasBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CanvasBg)
        ) {
            when (currentScreen) {
                AppScreen.DASHBOARD -> {
                    DashboardScreen(
                        schedule = schedule,
                        sessions = sessions,
                        selectedDay = selectedDay,
                        onSelectDay = { day ->
                            viewModel.selectDay(day)
                            viewModel.setScreen(AppScreen.WORKOUT)
                        },
                        onStartWorkout = { day ->
                            viewModel.selectDay(day)
                            viewModel.setScreen(AppScreen.WORKOUT)
                        },
                        onOpenShiftDialog = { showDayShiftDialog = true },
                        onOpenExerciseDetail = { ex -> detailExercise = ex },
                        onNavigateToPlan = { viewModel.setScreen(AppScreen.PLAN) },
                        onNavigateToCoach = { viewModel.setScreen(AppScreen.COACH) }
                    )
                }

                AppScreen.WORKOUT -> {
                    WorkoutScreen(
                        selectedDay = selectedDay,
                        schedule = schedule,
                        loggedSets = loggedSets,
                        onSelectDay = { day -> viewModel.selectDay(day) },
                        onSetUpdated = { exId, setNum, w, reps, rpe, done, autoTimer ->
                            viewModel.onSetUpdated(exId, setNum, w, reps, rpe, done, autoTimer)
                        },
                        onStartTimer = { seconds, name ->
                            viewModel.startRestTimer(seconds, name)
                        },
                        onOpenExerciseDetail = { ex -> detailExercise = ex },
                        onOpenShiftDialog = { showDayShiftDialog = true },
                        onFinishSession = { notes ->
                            viewModel.finishWorkoutSession(notes)
                        }
                    )
                }

                AppScreen.PLAN -> {
                    PlanScreen(
                        selectedDay = selectedDay,
                        onSelectDay = { day -> viewModel.selectDay(day) },
                        onStartWorkout = { day ->
                            viewModel.selectDay(day)
                            viewModel.setScreen(AppScreen.WORKOUT)
                        },
                        onOpenExerciseDetail = { ex -> detailExercise = ex }
                    )
                }

                AppScreen.LIBRARY -> {
                    LibraryScreen(
                        onOpenExerciseDetail = { ex -> detailExercise = ex }
                    )
                }

                AppScreen.PROGRESS -> {
                    ProgressScreen(
                        schedule = schedule,
                        sessions = sessions,
                        bodyWeights = bodyWeights,
                        onRecordWeight = { w -> viewModel.recordWeight(w) },
                        onDeleteWeight = { id -> viewModel.deleteWeight(id) },
                        onDeleteSession = { id -> viewModel.deleteSession(id) },
                        onExportJson = { viewModel.getExportJson() },
                        onGetShareableReport = { viewModel.getShareableTextSummary() }
                    )
                }

                AppScreen.COACH -> {
                    CoachScreen(
                        messages = chatMessages,
                        onSendMessage = { q -> viewModel.sendCoachQuery(q) }
                    )
                }

                AppScreen.SETTINGS -> {
                    SettingsScreen(
                        schedule = schedule,
                        onSaveProfile = { name, start, unit, goal, level ->
                            viewModel.updateProfile(name, start, unit, goal, level)
                        },
                        onPostponeToday = { viewModel.postponeTodayWorkout() },
                        onResetShifts = { viewModel.resetScheduleShifts() },
                        onExportJson = { viewModel.getExportJson() },
                        onImportJson = { json -> viewModel.importJsonBackup(json) },
                        onGetShareableReport = { viewModel.getShareableTextSummary() },
                        onClearAllData = { viewModel.clearAllData() }
                    )
                }
            }
        }
    }

    // Modal: Exercise Details & Alternatives
    detailExercise?.let { ex ->
        ExerciseDetailDialog(
            exercise = ex,
            onDismiss = { detailExercise = null },
            onStartTimer = { seconds ->
                viewModel.startRestTimer(seconds, ex.persianName)
            }
        )
    }

    // Modal: Day Postponement & Shifting
    if (showDayShiftDialog) {
        DayShiftDialog(
            currentShiftDays = schedule?.dayShiftOffset ?: 0,
            onPostponeToTomorrow = { viewModel.postponeTodayWorkout() },
            onResetShift = { viewModel.resetScheduleShifts() },
            onDismiss = { showDayShiftDialog = false }
        )
    }
}
