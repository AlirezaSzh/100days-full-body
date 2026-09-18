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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PersianDateUtil
import com.example.data.PlanData
import com.example.data.model.LoggedSetEntity
import com.example.data.model.ProgramScheduleEntity
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlueLight
import com.example.ui.theme.LineColor
import com.example.ui.theme.PanelBg
import com.example.ui.theme.PanelBorder
import com.example.ui.theme.PanelElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun WorkoutScreen(
    selectedDay: Int,
    schedule: ProgramScheduleEntity?,
    loggedSets: Map<String, LoggedSetEntity>,
    onSelectDay: (Int) -> Unit,
    onSetUpdated: (exerciseId: String, setNumber: Int, weight: Float, reps: Int, rpe: Float?, isCompleted: Boolean, autoTimer: Boolean) -> Unit,
    onStartTimer: (Int, String) -> Unit,
    onOpenExerciseDetail: (PlanData.Exercise) -> Unit,
    onOpenShiftDialog: () -> Unit,
    onFinishSession: (notes: String) -> Unit
) {
    val dayPlan = PlanData.getDay(selectedDay)
    val sched = schedule ?: ProgramScheduleEntity()
    var sessionNotes by remember { mutableStateOf("") }
    val todayIso = PersianDateUtil.todayIso()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("workout_screen")
    ) {
        // --- DAY SELECTOR TABS ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (d in 1..5) {
                val isSelected = d == selectedDay
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) BrandBlue else PanelElevated)
                        .border(1.dp, if (isSelected) BrandBlueLight else PanelBorder, RoundedCornerShape(12.dp))
                        .clickable { onSelectDay(d) }
                        .padding(vertical = 10.dp)
                        .testTag("day_tab_$d"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "روز ${PersianDateUtil.toPersianDigits(d)}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.White else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- SECTION HEADER & DAY POSTPONEMENT BUTTON ---
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${dayPlan.title}: ${dayPlan.subtitle}",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 17.sp, fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "وزن و تکرارها را وارد کن. با تیک زدن هر ست، تایمر به طور خودکار شروع می‌شود.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }

                    // Postpone button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(AccentGold.copy(alpha = 0.12f))
                            .border(1.dp, AccentGold.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .clickable { onOpenShiftDialog() }
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                            .testTag("postpone_workout_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Update,
                                contentDescription = null,
                                tint = AccentGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "انتقال به فردا",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentGold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Summary chips: Exercises, Total sets, Unit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val totalSets = dayPlan.exercises.sumOf { it.defaultSets }
                    Text(
                        text = "🏋️ ${PersianDateUtil.toPersianDigits(dayPlan.exercises.size)} حرکت",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(text = "•", color = TextMuted)
                    Text(
                        text = "🔢 ${PersianDateUtil.toPersianDigits(totalSets)} ست کاری",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(text = "•", color = TextMuted)
                    Text(
                        text = "واحد: ${sched.weightUnit}",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandBlueLight
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- EXERCISE CARDS LIST ---
        dayPlan.exercises.forEachIndexed { exIndex, exercise ->
            ExerciseWorkoutCard(
                exercise = exercise,
                exerciseIndex = exIndex,
                dateIso = todayIso,
                dayNumber = selectedDay,
                weightUnit = sched.weightUnit,
                loggedSets = loggedSets,
                onSetUpdated = onSetUpdated,
                onStartTimer = onStartTimer,
                onOpenDetail = { onOpenExerciseDetail(exercise) }
            )

            Spacer(modifier = Modifier.height(14.dp))
        }

        // --- FINISH SESSION SECTION ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = PanelBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, PanelBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "یادداشت و ثبت نهایی جلسه",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "می‌توانی احساس کلی، ریکاوری یا نکات این جلسه را یادداشت کنی.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = sessionNotes,
                    onValueChange = { sessionNotes = it },
                    placeholder = { Text("مثلاً: اسکوات بسیار روان بود، پرس سینه نیاز به تمرکز بیشتر...", style = MaterialTheme.typography.bodySmall, color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("workout_notes_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PanelElevated,
                        unfocusedContainerColor = PanelElevated,
                        focusedBorderColor = BrandBlue,
                        unfocusedBorderColor = LineColor,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { onFinishSession(sessionNotes) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("finish_and_save_workout_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ثبت و پایان تمرین امروز",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp)) // Padding for bottom timer dock
    }
}

@Composable
fun ExerciseWorkoutCard(
    exercise: PlanData.Exercise,
    exerciseIndex: Int,
    dateIso: String,
    dayNumber: Int,
    weightUnit: String,
    loggedSets: Map<String, LoggedSetEntity>,
    onSetUpdated: (exerciseId: String, setNumber: Int, weight: Float, reps: Int, rpe: Float?, isCompleted: Boolean, autoTimer: Boolean) -> Unit,
    onStartTimer: (Int, String) -> Unit,
    onOpenDetail: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("workout_card_${exercise.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PanelBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, PanelBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Title & Action buttons (Detail, Timer)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(BrandBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = PersianDateUtil.toPersianDigits(exerciseIndex + 1),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = BrandBlueLight
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = exercise.persianName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                // Action chips
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Help / Guide button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PanelElevated)
                            .border(1.dp, LineColor, RoundedCornerShape(8.dp))
                            .clickable { onOpenDetail() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = "راهنما",
                                tint = TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "راهنما",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }

                    // Direct Timer button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PanelElevated)
                            .border(1.dp, LineColor, RoundedCornerShape(8.dp))
                            .clickable { onStartTimer(exercise.restSeconds, exercise.persianName) }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "تایمر",
                                tint = AccentGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = PersianDateUtil.formatTimer(exercise.restSeconds),
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentGold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Exercise target params line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🎯 هدف: ${PersianDateUtil.toPersianDigits(exercise.defaultSets)} ست × ${PersianDateUtil.toPersianDigits(exercise.reps)} تکرار",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(text = "•", color = TextMuted)
                Text(
                    text = "عضله: ${exercise.muscle}",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrandBlueLight
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = LineColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Set Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "ست", style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.width(36.dp), textAlign = TextAlign.Center)
                Text(text = "وزن ($weightUnit)", style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(text = "تکرار", style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(text = "RPE", style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(text = "ثبت", style = MaterialTheme.typography.labelSmall, color = TextMuted, modifier = Modifier.width(46.dp), textAlign = TextAlign.Center)
            }

            // Set Rows
            for (setNum in 1..exercise.defaultSets) {
                val key = "$dateIso|$dayNumber|${exercise.id}|$setNum"
                val existingSet = loggedSets[key]

                SetInputRow(
                    setNumber = setNum,
                    initialWeight = existingSet?.weight,
                    initialReps = existingSet?.reps,
                    initialRpe = existingSet?.rpe,
                    isCompleted = existingSet?.isCompleted ?: false,
                    targetRepsHint = exercise.reps,
                    onSave = { w, r, rpe, done, autoTimer ->
                        onSetUpdated(exercise.id, setNum, w, r, rpe, done, autoTimer)
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Technique reminder
            Text(
                text = "نکته: ${exercise.note}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = TextMuted
            )
        }
    }
}

@Composable
fun SetInputRow(
    setNumber: Int,
    initialWeight: Float?,
    initialReps: Int?,
    initialRpe: Float?,
    isCompleted: Boolean,
    targetRepsHint: String,
    onSave: (weight: Float, reps: Int, rpe: Float?, isCompleted: Boolean, autoTimer: Boolean) -> Unit
) {
    var weightStr by remember(initialWeight) { mutableStateOf(initialWeight?.let { if (it > 0) it.toString() else "" } ?: "") }
    var repsStr by remember(initialReps) { mutableStateOf(initialReps?.let { if (it > 0) it.toString() else "" } ?: "") }
    var rpeStr by remember(initialRpe) { mutableStateOf(initialRpe?.toString() ?: "") }
    var done by remember(isCompleted) { mutableStateOf(isCompleted) }

    fun commit(isDoneToggle: Boolean) {
        val w = weightStr.toFloatOrNull() ?: 0f
        val r = repsStr.toIntOrNull() ?: 0
        val rpe = rpeStr.toFloatOrNull()
        if (isDoneToggle) {
            done = !done
            onSave(w, r, rpe, done, done) // auto trigger timer if newly completed
        } else {
            onSave(w, r, rpe, done, false)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set #
        Text(
            text = PersianDateUtil.toPersianDigits(setNumber),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = if (done) AccentGreen else TextPrimary,
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.Center
        )

        // Weight Input
        Box(modifier = Modifier.weight(1f).padding(horizontal = 3.dp)) {
            CompactNumberField(
                value = weightStr,
                onValueChange = {
                    weightStr = it
                    commit(false)
                },
                placeholder = "—"
            )
        }

        // Reps Input
        Box(modifier = Modifier.weight(1f).padding(horizontal = 3.dp)) {
            CompactNumberField(
                value = repsStr,
                onValueChange = {
                    repsStr = it
                    commit(false)
                },
                placeholder = targetRepsHint
            )
        }

        // RPE Input
        Box(modifier = Modifier.weight(1f).padding(horizontal = 3.dp)) {
            CompactNumberField(
                value = rpeStr,
                onValueChange = {
                    rpeStr = it
                    commit(false)
                },
                placeholder = "8"
            )
        }

        // Checkbox Button
        Box(
            modifier = Modifier
                .width(46.dp)
                .height(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (done) AccentGreen.copy(alpha = 0.2f) else LineColor)
                .border(1.dp, if (done) AccentGreen else PanelBorder, RoundedCornerShape(8.dp))
                .clickable { commit(true) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "ثبت ست",
                tint = if (done) AccentGreen else TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun CompactNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        ),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = PanelElevated,
            unfocusedContainerColor = PanelElevated,
            focusedBorderColor = BrandBlue,
            unfocusedBorderColor = LineColor,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        ),
        modifier = Modifier.fillMaxWidth().height(42.dp)
    )
}
