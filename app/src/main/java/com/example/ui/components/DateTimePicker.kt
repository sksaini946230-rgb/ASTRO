package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CosmicCardSurface
import com.example.ui.theme.CosmicDeepNavy
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SacredOrange
import com.example.ui.theme.TextSecondaryDark
import com.example.util.LanguageManager
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun M3DatePickerDialog(
    initialDateString: String = "",
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val initialMillis = try {
        if (initialDateString.isNotBlank() && initialDateString.contains("-")) {
            val parts = initialDateString.split("-")
            if (parts.size == 3) {
                val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                    set(Calendar.YEAR, parts[0].trim().toInt())
                    set(Calendar.MONTH, parts[1].trim().toInt() - 1)
                    set(Calendar.DAY_OF_MONTH, parts[2].trim().toInt())
                    set(Calendar.HOUR_OF_DAY, 12)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                cal.timeInMillis
            } else System.currentTimeMillis()
        } else System.currentTimeMillis()
    } catch (e: Throwable) {
        System.currentTimeMillis()
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                            timeInMillis = millis
                        }
                        val formatted = String.format(
                            Locale.US,
                            "%04d-%02d-%02d",
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH) + 1,
                            cal.get(Calendar.DAY_OF_MONTH)
                        )
                        onDateSelected(formatted)
                    }
                    onDismiss()
                }
            ) {
                Text(LanguageManager.getString("ठीक है", "OK"), color = GoldPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(LanguageManager.getString("रद्द करें", "Cancel"), color = TextSecondaryDark)
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = CosmicDeepNavy,
                titleContentColor = GoldPrimary,
                headlineContentColor = GoldPrimary,
                weekdayContentColor = TextSecondaryDark,
                subheadContentColor = GoldPrimary,
                yearContentColor = GoldPrimary,
                currentYearContentColor = SacredOrange,
                selectedYearContentColor = CosmicDeepNavy,
                selectedYearContainerColor = GoldPrimary,
                dayContentColor = GoldPrimary,
                selectedDayContainerColor = GoldPrimary,
                selectedDayContentColor = CosmicDeepNavy,
                todayDateBorderColor = SacredOrange,
                todayContentColor = SacredOrange
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun M3TimePickerDialog(
    initialTimeString: String = "",
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var initHour = 12
    var initMinute = 0
    try {
        if (initialTimeString.isNotBlank() && initialTimeString.contains(":")) {
            val parts = initialTimeString.split(":")
            if (parts.size >= 2) {
                initHour = parts[0].trim().toInt().coerceIn(0, 23)
                initMinute = parts[1].trim().toInt().coerceIn(0, 59)
            }
        }
    } catch (e: Throwable) {
        // use defaults
    }

    val timePickerState = rememberTimePickerState(
        initialHour = initHour,
        initialMinute = initMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val formatted = String.format(
                        Locale.US,
                        "%02d:%02d",
                        timePickerState.hour,
                        timePickerState.minute
                    )
                    onTimeSelected(formatted)
                    onDismiss()
                }
            ) {
                Text(LanguageManager.getString("ठीक है", "OK"), color = GoldPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(LanguageManager.getString("रद्द करें", "Cancel"), color = TextSecondaryDark)
            }
        },
        title = {
            Text(
                text = LanguageManager.getString("जन्म समय चुनें", "Select Birth Time"),
                color = GoldPrimary
            )
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = CosmicCardSurface,
                        clockDialSelectedContentColor = CosmicDeepNavy,
                        clockDialUnselectedContentColor = GoldPrimary,
                        selectorColor = GoldPrimary,
                        periodSelectorSelectedContainerColor = GoldPrimary,
                        periodSelectorSelectedContentColor = CosmicDeepNavy,
                        periodSelectorUnselectedContainerColor = CosmicCardSurface,
                        periodSelectorUnselectedContentColor = GoldPrimary,
                        timeSelectorSelectedContainerColor = GoldPrimary.copy(alpha = 0.3f),
                        timeSelectorSelectedContentColor = GoldPrimary,
                        timeSelectorUnselectedContainerColor = CosmicCardSurface,
                        timeSelectorUnselectedContentColor = TextSecondaryDark
                    )
                )
            }
        },
        containerColor = CosmicDeepNavy,
        tonalElevation = 6.dp
    )
}
