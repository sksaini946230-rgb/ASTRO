package com.example.ui.screens

import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlanetPosition
import com.example.ui.MainViewModel
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.GoldGlowButton
import com.example.ui.components.NorthIndianChart
import com.example.ui.components.SectionHeader
import com.example.ui.components.SouthIndianChart
import com.example.ui.theme.CosmicCardSurface
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SacredOrange
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.util.LanguageManager

@Composable
fun KundaliScreen(viewModel: MainViewModel) {
    val kundali by viewModel.generatedKundali.collectAsState()

    var isNorthStyle by remember { mutableStateOf(true) }
    var showForm by remember { mutableStateOf(false) }

    var nameInput by remember { mutableStateOf(kundali.personName) }
    var dobInput by remember { mutableStateOf(kundali.dateOfBirth) }
    var tobInput by remember { mutableStateOf(kundali.timeOfBirth) }
    var placeInput by remember { mutableStateOf(kundali.placeOfBirth) }

    var isSaved by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                titleHi = "जन्म कुण्डली (Vedic Birth Chart D1)",
                titleEn = "Birth Chart Generator",
                actionButtonText = if (showForm) "कुण्डली देखें" else "जन्म विवरण बदलें",
                onActionClick = { showForm = !showForm }
            )
        }

        // Input Form Card (When expanding form)
        if (showForm) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "जन्म विवरण दर्ज करें (Enter Birth Details):",
                            style = MaterialTheme.typography.titleSmall.copy(color = TextGold, fontWeight = FontWeight.Bold)
                        )

                        val tfColors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = TextPrimaryDark,
                            unfocusedTextColor = TextPrimaryDark,
                            focusedLabelColor = TextGold,
                            unfocusedLabelColor = TextSecondaryDark
                        )

                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("पूरा नाम (Name)") },
                            colors = tfColors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_kundali_name")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = dobInput,
                                onValueChange = { dobInput = it },
                                label = { Text("जन्म तिथि (YYYY-MM-DD)") },
                                colors = tfColors,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_kundali_dob")
                            )

                            OutlinedTextField(
                                value = tobInput,
                                onValueChange = { tobInput = it },
                                label = { Text("जन्म समय (HH:MM)") },
                                colors = tfColors,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_kundali_tob")
                            )
                        }

                        OutlinedTextField(
                            value = placeInput,
                            onValueChange = { placeInput = it },
                            label = { Text("जन्म स्थान (Place of Birth)") },
                            colors = tfColors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_kundali_place")
                        )

                        GoldGlowButton(
                            text = "कुण्डली बनाएं (Generate Chart)",
                            onClick = {
                                viewModel.generateKundaliChart(nameInput, dobInput, tobInput, placeInput)
                                showForm = false
                                isSaved = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "generate_chart_submit_button"
                        )
                    }
                }
            }
        }

        // Person Summary Header Bar
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = kundali.personName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextGold,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = "जन्म: ${kundali.dateOfBirth} | ${kundali.timeOfBirth} | ${kundali.placeOfBirth}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark, fontSize = 12.sp)
                        )
                    }

                    // Save Profile to Room Button
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSaved) GoldPrimary else GlassWhite)
                            .border(1.dp, GlassBorder, CircleShape)
                            .clickable {
                                viewModel.saveCurrentKundaliProfile()
                                isSaved = true
                            }
                            .padding(10.dp)
                            .testTag("save_profile_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Check else Icons.Default.Bookmark,
                            contentDescription = "Save",
                            tint = if (isSaved) CosmicCardSurface else GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Chart Style Switcher Pill Bar (North vs South Style)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isNorthStyle) GoldPrimary else GlassWhite)
                        .clickable { isNorthStyle = true }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("north_style_toggle")
                ) {
                    Text(
                        text = "उत्तर भारतीय (North Style)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isNorthStyle) CosmicCardSurface else TextGold
                        )
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (!isNorthStyle) GoldPrimary else GlassWhite)
                        .clickable { isNorthStyle = false }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("south_style_toggle")
                ) {
                    Text(
                        text = "दक्षिण भारतीय (South Style)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (!isNorthStyle) CosmicCardSurface else TextGold
                        )
                    )
                }
            }
        }

        // Interactive D1 Birth Chart Render
        item {
            Crossfade(targetState = isNorthStyle, label = "ChartTransition") { north ->
                if (north) {
                    NorthIndianChart(chartData = kundali)
                } else {
                    SouthIndianChart(chartData = kundali)
                }
            }
        }

        // Planetary Details Table Header
        item {
            SectionHeader(
                titleHi = "ग्रह स्थिति एवं नक्षत्र (Planetary Degrees)",
                titleEn = "Planetary Positions"
            )
        }

        // Planets Positions List Table
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "ग्रह", style = MaterialTheme.typography.labelSmall.copy(color = TextGold, fontWeight = FontWeight.Bold, fontSize = 11.sp), modifier = Modifier.weight(1f))
                        Text(text = "राशि", style = MaterialTheme.typography.labelSmall.copy(color = TextGold, fontWeight = FontWeight.Bold, fontSize = 11.sp), modifier = Modifier.weight(1.2f))
                        Text(text = "अंश (Deg)", style = MaterialTheme.typography.labelSmall.copy(color = TextGold, fontWeight = FontWeight.Bold, fontSize = 11.sp), modifier = Modifier.weight(1f))
                        Text(text = "भाव (House)", style = MaterialTheme.typography.labelSmall.copy(color = TextGold, fontWeight = FontWeight.Bold, fontSize = 11.sp), modifier = Modifier.weight(1f))
                    }

                    kundali.planets.forEach { planet ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = planet.planetNameHi.substringBefore(" "), style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark, fontSize = 12.sp), modifier = Modifier.weight(1f))
                            Text(text = planet.rashiNameHi, style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark, fontSize = 12.sp), modifier = Modifier.weight(1.2f))
                            Text(text = "${planet.degree}°", style = MaterialTheme.typography.bodySmall.copy(color = TextGold, fontSize = 12.sp), modifier = Modifier.weight(1f))
                            Text(text = "${planet.houseNumber} भाव", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark, fontSize = 12.sp), modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Vimshottari Dasha Timeline Section
        item {
            SectionHeader(
                titleHi = "विंशोत्तरी महादशा चक्र (Vimshottari Dasha)",
                titleEn = "Dasha Timeline"
            )
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "वर्तमान महादशा: ${kundali.currentMahadashaHi} | अंतर्दशा: ${kundali.currentAntardashaHi}",
                        style = MaterialTheme.typography.titleSmall.copy(color = SacredOrange, fontWeight = FontWeight.Bold)
                    )

                    kundali.dashaTimeline.forEach { dasha ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (dasha.isCurrent) GoldPrimary.copy(alpha = 0.2f) else GlassWhite)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${dasha.planetHi} (${dasha.planetEn})",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (dasha.isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    color = if (dasha.isCurrent) TextGold else TextPrimaryDark
                                )
                            )

                            Text(
                                text = "${dasha.startDate} - ${dasha.endDate} (${dasha.durationYears} वर्ष)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (dasha.isCurrent) GoldPrimary else TextSecondaryDark
                                )
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
