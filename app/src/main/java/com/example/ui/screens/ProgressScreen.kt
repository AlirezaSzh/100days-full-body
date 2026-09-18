package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PersianDateUtil
import com.example.data.PlanData
import com.example.data.model.BodyWeightEntity
import com.example.data.model.ProgramScheduleEntity
import com.example.data.model.WorkoutSessionEntity
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
import kotlinx.coroutines.launch

@Composable
fun ProgressScreen(
    schedule: ProgramScheduleEntity?,
    sessions: List<WorkoutSessionEntity>,
    bodyWeights: List<BodyWeightEntity>,
    onRecordWeight: (Float) -> Unit,
    onDeleteWeight: (Long) -> Unit,
    onDeleteSession: (Long) -> Unit,
    onExportJson: suspend () -> String,
    onGetShareableReport: suspend () -> String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sched = schedule ?: ProgramScheduleEntity()

    var weightInput by remember { mutableStateOf("") }

    val totalVolume = remember(sessions) { sessions.sumOf { it.volume } }
    val latestWeight = remember(bodyWeights) { bodyWeights.firstOrNull() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("progress_screen")
    ) {
        // Section Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "پیشرفت و تاریخچه تمرین",
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = "ثبت حجم کل جلسات، تغییرات وزن بدن و خروجی کامل پشتیبان.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            // Quick Share Button
            IconButton(
                onClick = {
                    scope.launch {
                        val report = onGetShareableReport()
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, report)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "اشتراک‌گذاری گزارش وضعیت"))
                    }
                },
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PanelElevated)
                    .border(1.dp, PanelBorder, RoundedCornerShape(10.dp))
                    .testTag("share_report_btn")
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "اشتراک گزارش", tint = BrandBlueLight, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCardItem(
                title = "مجموع حجم تمرین",
                value = "${PersianDateUtil.toPersianDigits(totalVolume.toInt())} ${sched.weightUnit}",
                subtitle = "کل وزنه جابجاشده",
                icon = Icons.Default.TrendingUp,
                iconTint = BrandBlue,
                modifier = Modifier.weight(1f)
            )

            StatCardItem(
                title = "تعداد کل جلسات",
                value = PersianDateUtil.toPersianDigits(sessions.size),
                subtitle = "جلسه پایان‌یافته",
                icon = Icons.Default.FitnessCenter,
                iconTint = AccentGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- BODY WEIGHT TRACKER CARD ---
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Scale, contentDescription = null, tint = AccentGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ثبت و روند وزن بدن",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }

                    if (latestWeight != null) {
                        Text(
                            text = "آخرین: ${PersianDateUtil.toPersianDigits(latestWeight.weight)} ${sched.weightUnit}",
                            style = MaterialTheme.typography.labelLarge,
                            color = AccentGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Weight Input Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        placeholder = { Text("مثلاً 78.5", style = MaterialTheme.typography.bodySmall, color = TextMuted) },
                        modifier = Modifier.weight(1f).height(48.dp).testTag("weight_input_field"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = PanelElevated,
                            unfocusedContainerColor = PanelElevated,
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = LineColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Button(
                        onClick = {
                            val w = weightInput.toFloatOrNull()
                            if (w != null && w in 20f..400f) {
                                onRecordWeight(w)
                                weightInput = ""
                            }
                        },
                        modifier = Modifier.height(48.dp).testTag("save_weight_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ثبت وزن", style = MaterialTheme.typography.labelLarge)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Weight Entries History (Last 5)
                if (bodyWeights.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        bodyWeights.take(5).forEach { bw ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PanelElevated)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = PersianDateUtil.toPersianDisplayDate(bw.date),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${PersianDateUtil.toPersianDigits(bw.weight)} ${sched.weightUnit}",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                    IconButton(
                                        onClick = { onDeleteWeight(bw.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "حذف", tint = TextMuted, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "هنوز وزنی ثبت نشده است. وزن ناشتا را به طور هفتگی ثبت کن.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- SESSION HISTORY CARD ---
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
                        text = "تاریخچه جلسات انجام‌شده",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )

                    Text(
                        text = "${PersianDateUtil.toPersianDigits(sessions.size)} جلسه",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandBlueLight
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (sessions.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        sessions.forEach { s ->
                            SessionHistoryItem(
                                session = s,
                                weightUnit = sched.weightUnit,
                                onDelete = { onDeleteSession(s.id) }
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PanelElevated)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "هنوز جلسه‌ای ثبت نکرده‌ای. پس از اولین جلسه تمرین، سوابق اینجا نمایش داده می‌شوند.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- BACKUP & EXPORT JSON ACTION ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = PanelElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, LineColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "پشتیبان‌گیری کامل (JSON Export)",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "خروجی گرفتن از تمام ست‌ها، وزن‌ها و تنظیمات برنامه برای نگهداری امن.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Button(
                    onClick = {
                        scope.launch {
                            val json = onExportJson()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("BWS Training Backup", json))

                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, json)
                                type = "application/json"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "ارسال یا ذخیره فایل پشتیبان JSON"))
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    modifier = Modifier.testTag("export_json_button")
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("خروجی JSON", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun SessionHistoryItem(
    session: WorkoutSessionEntity,
    weightUnit: String,
    onDelete: () -> Unit
) {
    val dayPlan = PlanData.getDay(session.dayNumber)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PanelElevated)
            .border(1.dp, LineColor, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${dayPlan.title} (${PersianDateUtil.toPersianDisplayDate(session.date)})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "حجم: ${PersianDateUtil.toPersianDigits(session.volume.toInt())} $weightUnit • ست‌ها: ${PersianDateUtil.toPersianDigits(session.setsCompleted)}",
                style = MaterialTheme.typography.bodySmall,
                color = BrandBlueLight
            )

            if (session.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "یادداشت: ${session.notes}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextMuted
                )
            }
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "حذف جلسه",
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
