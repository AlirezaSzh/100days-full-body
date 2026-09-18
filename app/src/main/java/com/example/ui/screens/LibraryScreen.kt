package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PersianDateUtil
import com.example.data.PlanData
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
import java.util.Locale

@Composable
fun LibraryScreen(
    onOpenExerciseDetail: (PlanData.Exercise) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedMuscle by remember { mutableStateOf<String?>(null) }

    val allMuscles = remember {
        listOf("همه") + PlanData.ALL_EXERCISES.map { it.muscle }.distinct()
    }

    val filteredExercises = remember(searchQuery, selectedMuscle) {
        val q = searchQuery.trim().lowercase()
        PlanData.ALL_EXERCISES.filter { ex ->
            val matchMuscle = selectedMuscle == null || selectedMuscle == "همه" || ex.muscle == selectedMuscle
            val matchQuery = q.isEmpty() ||
                    ex.name.lowercase().contains(q) ||
                    ex.persianName.lowercase().contains(q) ||
                    ex.muscle.lowercase().contains(q)
            matchMuscle && matchQuery
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("library_screen")
    ) {
        Text(
            text = "حرکات و جایگزین‌ها",
            style = MaterialTheme.typography.displaySmall.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Text(
            text = "راهنمای تکنیک و جایگزین‌های رسمی صفحه ۴۳ تا ۴۷ PDF در صورت اشغال دستگاه.",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("جستجو در حرکات (انگلیسی یا فارسی)...", style = MaterialTheme.typography.bodySmall, color = TextMuted) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "پاک کردن", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = PanelElevated,
                unfocusedContainerColor = PanelElevated,
                focusedBorderColor = BrandBlue,
                unfocusedBorderColor = LineColor,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("library_search_field")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Muscle Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            allMuscles.forEach { muscle ->
                val isSelected = (selectedMuscle == null && muscle == "همه") || (selectedMuscle == muscle)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) BrandBlue else PanelElevated)
                        .border(1.dp, if (isSelected) BrandBlueLight else PanelBorder, RoundedCornerShape(20.dp))
                        .clickable {
                            selectedMuscle = if (muscle == "همه") null else muscle
                        }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = muscle,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) Color.White else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "تعداد یافته‌ها: ${PersianDateUtil.toPersianDigits(filteredExercises.size)} حرکت",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Exercise List
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredExercises, key = { it.id }) { ex ->
                LibraryExerciseCard(
                    exercise = ex,
                    onClick = { onOpenExerciseDetail(ex) }
                )
            }
        }

        Spacer(modifier = Modifier.height(70.dp))
    }
}

@Composable
fun LibraryExerciseCard(
    exercise: PlanData.Exercise,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("lib_card_${exercise.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PanelBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, PanelBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.persianName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TagChipSmall(text = "صفحه ${PersianDateUtil.toPersianDigits(exercise.page)} PDF", color = BrandBlueLight)
                    TagChipSmall(text = exercise.muscle, color = AccentGold)
                    TagChipSmall(text = "${PersianDateUtil.toPersianDigits(exercise.defaultSets)} ست", color = AccentGreen)
                    TagChipSmall(text = "استراحت: ${PersianDateUtil.formatTimer(exercise.restSeconds)}", color = TextSecondary)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = exercise.note,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextSecondary,
                    maxLines = 2
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "مشاهده",
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun TagChipSmall(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(PanelElevated)
            .border(1.dp, LineColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = color
        )
    }
}
