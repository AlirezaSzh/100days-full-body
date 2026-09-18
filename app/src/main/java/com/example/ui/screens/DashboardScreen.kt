package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PersianDateUtil
import com.example.data.PlanData
import com.example.data.model.ProgramScheduleEntity
import com.example.data.model.WorkoutSessionEntity
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueDark
import com.example.ui.theme.BrandBlueLight
import com.example.ui.theme.CanvasBg
import com.example.ui.theme.LineColor
import com.example.ui.theme.PanelBg
import com.example.ui.theme.PanelBorder
import com.example.ui.theme.PanelElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Calendar

@Composable
fun DashboardScreen(
    schedule: ProgramScheduleEntity?,
    sessions: List<WorkoutSessionEntity>,
    selectedDay: Int,
    onSelectDay: (Int) -> Unit,
    onStartWorkout: (Int) -> Unit,
    onOpenShiftDialog: () -> Unit,
    onOpenExerciseDetail: (PlanData.Exercise) -> Unit,
    onNavigateToPlan: () -> Unit,
    onNavigateToCoach: () -> Unit
) {
    val sched = schedule ?: ProgramScheduleEntity()
    val persianStartDate = PersianDateUtil.toPersianDisplayDate(sched.startDateIso)
    val todayIso = PersianDateUtil.todayIso()
    val todayPersian = PersianDateUtil.toPersianDisplayDate(todayIso)
    val todayWeekday = PersianDateUtil.getPersianWeekdayName(todayIso)

    // Calculate week and day
    val (programWeek, cycleDay) = remember(sched.startDateIso, sched.dayShiftOffset) {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val cal = Calendar.getInstance()
        try {
            cal.time = sdf.parse(sched.startDateIso) ?: java.util.Date()
        } catch (e: Exception) {
            cal.time = java.util.Date()
        }
        cal.add(Calendar.DAY_OF_YEAR, sched.dayShiftOffset)
        val diffDays = ((System.currentTimeMillis() - cal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
        val w = if (diffDays >= 0) (diffDays / 7) + 1 else 1
        val d = if (diffDays >= 0) (diffDays % 7) + 1 else 1
        Pair(w, d)
    }

    val isAdaptationPhase = programWeek <= 2
    val isRestDay = cycleDay > 5 // In a 7-day cycle, day 6 and 7 are rest days

    // Unique completed dates this week
    val currentWeekStartMillis = remember {
        val c = Calendar.getInstance()
        val dow = c.get(Calendar.DAY_OF_WEEK)
        val daysFromSaturday = if (dow == Calendar.SATURDAY) 0 else (dow)
        c.add(Calendar.DAY_OF_YEAR, -daysFromSaturday)
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.timeInMillis
    }

    val thisWeekSessions = sessions.filter { it.timestamp >= currentWeekStartMillis }
    val thisWeekCompletedCount = thisWeekSessions.map { it.date }.distinct().size
    val thisWeekVolume = thisWeekSessions.sumOf { it.volume }

    // Streak calculation
    val streakDays = remember(sessions) {
        val dates = sessions.map { it.date }.toSet()
        var streak = 0
        val cal = Calendar.getInstance()
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        // Check if today is logged, else start from yesterday
        var checkDate = sdf.format(cal.time)
        if (!dates.contains(checkDate)) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
            checkDate = sdf.format(cal.time)
        }
        while (dates.contains(checkDate)) {
            streak++
            cal.add(Calendar.DAY_OF_YEAR, -1)
            checkDate = sdf.format(cal.time)
        }
        streak
    }

    val targetDayNumber = if (selectedDay in 1..5) selectedDay else (if (cycleDay in 1..5) cycleDay else 1)
    val todayPlan = PlanData.getDay(targetDayNumber)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("dashboard_screen")
    ) {
        // --- HERO BANNER ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = androidx.compose.foundation.BorderStroke(1.dp, PanelBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF131D2D), Color(0xFF0E1724), CanvasBg)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isRestDay) AccentGold else AccentGreen)
                        )
                        Text(
                            text = if (isRestDay) "روز ریکاوری و استراحت" else "جلسه فعال تمرینی",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isRestDay) AccentGold else BrandBlueLight
                        )
                        Text(
                            text = "•",
                            color = TextMuted
                        )
                        Text(
                            text = "هفته ${PersianDateUtil.toPersianDigits(programWeek)} برنامه",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${sched.userName} عزیز، آماده تمرین پرقدرتی؟",
                        style = MaterialTheme.typography.displaySmall.copy(fontSize = 20.sp),
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "شروع برنامه از $persianStartDate • امروز: $todayWeekday $todayPersian",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Phase Badge
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isAdaptationPhase) Color(0xFF261D05) else Color(0xFF0B2138))
                            .border(1.dp, if (isAdaptationPhase) AccentGold.copy(alpha = 0.4f) else BrandBlue.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isAdaptationPhase) "فاز سازگاری (هفته ۱ و ۲): " else "فاز پیشروی بار: ",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isAdaptationPhase) AccentGold else BrandBlueLight
                            )
                            Text(
                                text = if (isAdaptationPhase) "تمرکز روی تکنیک؛ به ناتوانی شدید نزدیک نشو." else "در صورت اجرای تمیز سقف تکرار، بار را افزایش بده.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Hero Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onStartWorkout(targetDayNumber) },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(44.dp)
                                .testTag("start_today_workout_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isRestDay) "شروع جلسه بعد" else "شروع ${todayPlan.title}",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White
                            )
                        }

                        OutlinedButton(
                            onClick = onOpenShiftDialog,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("shift_today_btn"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LineColor),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                        ) {
                            Icon(imageVector = Icons.Default.Update, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "انتقال به فردا",
                                style = MaterialTheme.typography.labelMedium,
                                color = AccentGold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- STATS CARDS ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCardItem(
                title = "جلسات کل",
                value = PersianDateUtil.toPersianDigits(sessions.size),
                subtitle = "جلسه ثبت‌شده",
                icon = Icons.Default.Check,
                iconTint = AccentGreen,
                modifier = Modifier.weight(1f)
            )

            StatCardItem(
                title = "هدف این هفته",
                value = "${PersianDateUtil.toPersianDigits(thisWeekCompletedCount)} از ۵",
                subtitle = "جلسات انجام‌شده",
                icon = Icons.Default.FitnessCenter,
                iconTint = BrandBlue,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCardItem(
                title = "حجم این هفته",
                value = "${PersianDateUtil.toPersianDigits(thisWeekVolume.toInt())} ${sched.weightUnit}",
                subtitle = "مجموع وزنه × تکرار",
                icon = Icons.Default.TrendingUp,
                iconTint = BrandBlueLight,
                modifier = Modifier.weight(1f)
            )

            StatCardItem(
                title = "استریک پیوسته",
                value = "${PersianDateUtil.toPersianDigits(streakDays)} روز",
                subtitle = "تداوم بدون وقفه",
                icon = Icons.Default.Bolt,
                iconTint = AccentGold,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // --- WEEK DAYS STRIP ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = PanelBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, PanelBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تقویم ۷ روزه هفته جاری",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "۵ جلسه + ۲ استراحت",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val daysLabels = listOf(
                        Triple("شنبه", 1, false),
                        Triple("یکشنبه", 2, false),
                        Triple("دوشنبه", 3, false),
                        Triple("سه‌شنبه", 4, false),
                        Triple("چهارشنبه", 5, false),
                        Triple("پنج‌شنبه", 0, true),
                        Triple("جمعه", 0, true)
                    )

                    daysLabels.forEachIndexed { idx, (dayName, dayNum, isRest) ->
                        val isTodayColumn = (idx + 1) == cycleDay
                        val isDone = !isRest && thisWeekSessions.any { it.dayNumber == dayNum }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isTodayColumn) BrandBlue else PanelElevated)
                                .border(
                                    1.dp,
                                    if (isDone) AccentGreen else if (isTodayColumn) BrandBlueLight else LineColor,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    if (!isRest) onSelectDay(dayNum)
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = if (isTodayColumn) Color.White else TextMuted
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isRest) "استراحت" else "روز ${PersianDateUtil.toPersianDigits(dayNum)}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (isDone) AccentGreen else if (isTodayColumn) Color.White else TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // --- TODAY'S EXERCISES PREVIEW ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = PanelBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, PanelBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${todayPlan.title} • ${todayPlan.subtitle}",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = "${PersianDateUtil.toPersianDigits(todayPlan.exercises.size)} حرکت انتخابی جلسه",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }

                    Text(
                        text = "مشاهده کل ←",
                        style = MaterialTheme.typography.labelMedium,
                        color = BrandBlueLight,
                        modifier = Modifier.clickable { onNavigateToPlan() }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    todayPlan.exercises.forEachIndexed { index, ex ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(PanelElevated)
                                .border(1.dp, LineColor, RoundedCornerShape(12.dp))
                                .clickable { onOpenExerciseDetail(ex) }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(LineColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = PersianDateUtil.toPersianDigits(index + 1),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = ex.persianName,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${ex.muscle} • ${PersianDateUtil.toPersianDigits(ex.defaultSets)} ست × ${PersianDateUtil.toPersianDigits(ex.reps)} • استراحت ${PersianDateUtil.formatTimer(ex.restSeconds)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "مشاهده جزئیات",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // --- COACH TIP BANNER ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3554))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToCoach() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AccentGold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = AccentGold,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "توصیه مربی روز:",
                        style = MaterialTheme.typography.titleSmall,
                        color = AccentGold,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isAdaptationPhase) {
                            "در هفته‌های آغازین هدف ساختن عادت و فرم اجرای درست است؛ رکوردگیری نکن."
                        } else {
                            "ست‌ها را در سقف تکرار با تمرکز کامل بزن تا در جلسه بعد افزایش بار منطقی باشد."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun StatCardItem(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PanelBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, PanelBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}
