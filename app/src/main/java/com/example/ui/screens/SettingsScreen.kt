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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PersianDateUtil
import com.example.data.model.ProgramScheduleEntity
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    schedule: ProgramScheduleEntity?,
    onSaveProfile: (name: String, startDateIso: String, unit: String, goal: String, level: String) -> Unit,
    onPostponeToday: () -> Unit,
    onResetShifts: () -> Unit,
    onExportJson: suspend () -> String,
    onImportJson: (String) -> Unit,
    onGetShareableReport: suspend () -> String,
    onClearAllData: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sched = schedule ?: ProgramScheduleEntity()

    var userName by remember(sched.userName) { mutableStateOf(sched.userName) }
    var startDateIso by remember(sched.startDateIso) { mutableStateOf(sched.startDateIso) }
    var weightUnit by remember(sched.weightUnit) { mutableStateOf(sched.weightUnit) }
    var fitnessGoal by remember(sched.fitnessGoal) { mutableStateOf(sched.fitnessGoal) }
    var experienceLevel by remember(sched.experienceLevel) { mutableStateOf(sched.experienceLevel) }

    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var showImportJsonDialog by remember { mutableStateOf(false) }
    var jsonInputText by remember { mutableStateOf("") }

    val goalsList = listOf("عضله‌سازی", "قدرت", "تناسب عمومی")
    val levelsList = listOf("مبتدی", "متوسط", "پیشرفته")

    var goalExpanded by remember { mutableStateOf(false) }
    var levelExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("settings_screen")
    ) {
        // Title
        Text(
            text = "تنظیمات برنامه و داده‌ها",
            style = MaterialTheme.typography.displaySmall.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Text(
            text = "تنظیم تاریخ شروع شمسی، مدیریت جابجایی روزها و خروجی کامل نسخه پشتیبان.",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- PROFILE SETTINGS CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = PanelBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, PanelBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = BrandBlueLight, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "پروفایل ورزشکار",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Name input
                Text("نام ورزشکار", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    placeholder = { Text("نام شما", style = MaterialTheme.typography.bodySmall, color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("profile_name_input"),
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

                Spacer(modifier = Modifier.height(10.dp))

                // Start Date (Defaults to 28 Shahrivar 1405)
                Text(
                    text = "تاریخ شروع برنامه (شمسی: ${PersianDateUtil.toPersianDisplayDate(startDateIso)})",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = startDateIso,
                    onValueChange = { startDateIso = it },
                    placeholder = { Text("2026-09-19", style = MaterialTheme.typography.bodySmall, color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("profile_start_date_input"),
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

                Spacer(modifier = Modifier.height(10.dp))

                // Unit selection
                Text("واحد اندازه‌گیری وزنه", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isKg = weightUnit == "kg"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isKg) BrandBlue else PanelElevated)
                            .border(1.dp, if (isKg) BrandBlueLight else LineColor, RoundedCornerShape(12.dp))
                            .clickable { weightUnit = "kg" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "کیلوگرم (kg)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isKg) FontWeight.Bold else FontWeight.Normal,
                            color = if (isKg) Color.White else TextSecondary
                        )
                    }

                    val isLb = weightUnit == "lb"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isLb) BrandBlue else PanelElevated)
                            .border(1.dp, if (isLb) BrandBlueLight else LineColor, RoundedCornerShape(12.dp))
                            .clickable { weightUnit = "lb" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "پوند (lb)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isLb) FontWeight.Bold else FontWeight.Normal,
                            color = if (isLb) Color.White else TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Goal & Level Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Goal Box
                    Column(modifier = Modifier.weight(1f)) {
                        Text("هدف تمرین", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        ExposedDropdownMenuBox(
                            expanded = goalExpanded,
                            onExpandedChange = { goalExpanded = !goalExpanded }
                        ) {
                            OutlinedTextField(
                                value = fitnessGoal,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = goalExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
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
                            ExposedDropdownMenu(
                                expanded = goalExpanded,
                                onDismissRequest = { goalExpanded = false },
                                modifier = Modifier.background(PanelElevated)
                            ) {
                                goalsList.forEach { g ->
                                    DropdownMenuItem(
                                        text = { Text(g, style = MaterialTheme.typography.bodyMedium, color = TextPrimary) },
                                        onClick = {
                                            fitnessGoal = g
                                            goalExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Level Box
                    Column(modifier = Modifier.weight(1f)) {
                        Text("سطح سابقه", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        ExposedDropdownMenuBox(
                            expanded = levelExpanded,
                            onExpandedChange = { levelExpanded = !levelExpanded }
                        ) {
                            OutlinedTextField(
                                value = experienceLevel,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = levelExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
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
                            ExposedDropdownMenu(
                                expanded = levelExpanded,
                                onDismissRequest = { levelExpanded = false },
                                modifier = Modifier.background(PanelElevated)
                            ) {
                                levelsList.forEach { l ->
                                    DropdownMenuItem(
                                        text = { Text(l, style = MaterialTheme.typography.bodyMedium, color = TextPrimary) },
                                        onClick = {
                                            experienceLevel = l
                                            levelExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        onSaveProfile(userName, startDateIso, weightUnit, fitnessGoal, experienceLevel)
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp).testTag("save_profile_settings_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ذخیره تنظیمات پروفایل", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- DAY POSTPONEMENT & SHIFTING CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = PanelBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, PanelBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Update, contentDescription = null, tint = AccentGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "جابجایی و انتقال روزهای تمرین",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "وقتی یک روز وقت نمی‌کنی تمرین کنی، با زدن دکمه زیر جلسه را به فردا منتقل کن تا توالی و زنجیره ۵ جلسه حفظ شود.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(PanelElevated)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "مجموع روزهای جابجا شده:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = if (sched.dayShiftOffset > 0) {
                                "${PersianDateUtil.toPersianDigits(sched.dayShiftOffset)} روز تاخیر ثبت‌شده"
                            } else {
                                "بدون تاخیر (طبق برنامه)"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = if (sched.dayShiftOffset > 0) AccentGold else AccentGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onPostponeToday,
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGold)
                    ) {
                        Icon(imageVector = Icons.Default.Update, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("انتقال به فردا (+۱)", style = MaterialTheme.typography.labelMedium, color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    if (sched.dayShiftOffset > 0) {
                        OutlinedButton(
                            onClick = onResetShifts,
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LineColor),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                        ) {
                            Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("بازنشانی شیفت", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- FULL BACKUP, EXPORT & IMPORT CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = PanelBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, PanelBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "پشتیبان‌گیری و خروجی کامل داده‌ها",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "می‌توانی تمام داده‌های ثبت‌شده را در قالب فایل JSON استخراج کنی، به پیام‌رسان بفرستی، یا روی دستگاه دیگری بازیابی کنی.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                                context.startActivity(Intent.createChooser(sendIntent, "ارسال فایل پشتیبان JSON"))
                            }
                        },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                    ) {
                        Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("خروجی JSON", style = MaterialTheme.typography.labelSmall)
                    }

                    OutlinedButton(
                        onClick = {
                            showImportJsonDialog = true
                        },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LineColor),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("بازیابی JSON", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Share text summary button
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            val text = onGetShareableReport()
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, text)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "اشتراک‌گذاری گزارش وضعیت"))
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LineColor),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("اشتراک‌گذاری متن خلاصه وضعیت و پیشرفت", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- CLEAR DATA DANGER ZONE ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F0E12)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3F1B22))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "منطقه حساس: پاک‌سازی تمام اطلاعات",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = AccentRed
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "با انجام این کار، تمام تاریخچه جلسات تمرینی، وزن‌های بدن و رکوردهای ثبت‌شده حذف خواهند شد.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { showClearConfirmDialog = true },
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("clear_all_data_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentRed),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentRed.copy(alpha = 0.5f))
                ) {
                    Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("حذف دائم اطلاعات تمرین", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    // Clear Confirmation Dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("آیا مطمئن هستید؟", style = MaterialTheme.typography.titleLarge) },
            text = { Text("تمام سوابق جلسات و وزن‌ها حذف می‌شود و قابل بازگشت نخواهد بود مگر آنکه از قبل فایل JSON خروجی گرفته باشید.", style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                Button(
                    onClick = {
                        showClearConfirmDialog = false
                        onClearAllData()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("بله، پاک شود", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("انصراف", color = TextSecondary)
                }
            },
            containerColor = PanelBg
        )
    }

    // Import JSON Dialog
    if (showImportJsonDialog) {
        AlertDialog(
            onDismissRequest = { showImportJsonDialog = false },
            title = { Text("بازیابی نسخه پشتیبان JSON", style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    Text("متن فایل JSON خروجی گرفته شده را اینجا پیست کن:", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = jsonInputText,
                        onValueChange = { jsonInputText = it },
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = PanelElevated,
                            unfocusedContainerColor = PanelElevated,
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = LineColor,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showImportJsonDialog = false
                        if (jsonInputText.isNotBlank()) {
                            onImportJson(jsonInputText)
                            jsonInputText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Text("اعمال و بازیابی", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportJsonDialog = false }) {
                    Text("انصراف", color = TextSecondary)
                }
            },
            containerColor = PanelBg
        )
    }
}
