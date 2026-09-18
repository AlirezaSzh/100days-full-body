package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PersianDateUtil
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.LineColor
import com.example.ui.theme.PanelBorder
import com.example.ui.theme.PanelElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.TimerState

@Composable
fun RestTimerDock(
    state: TimerState,
    onPauseResume: () -> Unit,
    onAdjust: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = state.isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        val progress = if (state.totalSeconds > 0) {
            (state.remainingSeconds.toFloat() / state.totalSeconds.toFloat()).coerceIn(0f, 1f)
        } else 0f

        val isFinished = state.remainingSeconds == 0

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(PanelElevated)
                .border(1.dp, if (isFinished) AccentGreen else PanelBorder, RoundedCornerShape(20.dp))
                .testTag("rest_timer_dock")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Linear timer progress indicator at the very top of dock
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isFinished) AccentGreen else BrandBlue,
                    trackColor = LineColor
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Timer Icon & Display
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isFinished) AccentGreen.copy(alpha = 0.2f) else BrandBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "تایمر",
                                tint = if (isFinished) AccentGreen else BrandBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = if (isFinished) "پایان استراحت!" else "استراحت بین ست",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isFinished) AccentGreen else TextMuted
                            )
                            Text(
                                text = PersianDateUtil.formatTimer(state.remainingSeconds),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (isFinished) AccentGreen else TextPrimary
                            )
                        }
                    }

                    // Timer controls: -15, pause/play, +15, dismiss
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Subtract 15s button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(LineColor)
                                .border(1.dp, PanelBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            IconButton(
                                onClick = { onAdjust(-15) },
                                modifier = Modifier.size(24.dp).testTag("timer_minus_15")
                            ) {
                                Text(
                                    text = "-${PersianDateUtil.toPersianDigits(15)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextPrimary
                                )
                            }
                        }

                        // Play/Pause button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (state.isRunning) BrandBlue else LineColor)
                                .border(1.dp, PanelBorder, RoundedCornerShape(10.dp))
                        ) {
                            IconButton(
                                onClick = onPauseResume,
                                modifier = Modifier.size(36.dp).testTag("timer_play_pause")
                            ) {
                                Icon(
                                    imageVector = if (state.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (state.isRunning) "توقف تایمر" else "ادامه تایمر",
                                    tint = if (state.isRunning) Color.White else TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Add 15s button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(LineColor)
                                .border(1.dp, PanelBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            IconButton(
                                onClick = { onAdjust(15) },
                                modifier = Modifier.size(24.dp).testTag("timer_plus_15")
                            ) {
                                Text(
                                    text = "+${PersianDateUtil.toPersianDigits(15)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextPrimary
                                )
                            }
                        }

                        // Close button
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp).testTag("timer_dismiss")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "بستن تایمر",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
