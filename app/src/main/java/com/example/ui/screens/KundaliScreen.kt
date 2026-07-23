package com.example.ui.screens

import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.PaddingValues
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

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = paddingValues.calculateTopPadding() + 8.dp,
                end = 16.dp,
                bottom = paddingValues.calculateBottomPadding() + 16.dp
            ),
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
                            text = LanguageManager.getString("जन्म विवरण दर्ज करें", "Enter Birth Details"),
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
                            label = { Text(LanguageManager.getString("पूरा नाम", "Full Name")) },
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
                                label = { Text(LanguageManager.getString("जन्म तिथि (YYYY-MM-DD)", "DOB (YYYY-MM-DD)")) },
                                colors = tfColors,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_kundali_dob")
                            )

                            OutlinedTextField(
                                value = tobInput,
                                onValueChange = { tobInput = it },
                                label = { Text(LanguageManager.getString("जन्म समय (HH:MM)", "Time (HH:MM)")) },
                                colors = tfColors,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_kundali_tob")
                            )
                        }

                        OutlinedTextField(
                            value = placeInput,
                            onValueChange = { placeInput = it },
                            label = { Text(LanguageManager.getString("जन्म स्थान", "Place of Birth")) },
                            colors = tfColors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_kundali_place")
                        )

                        val isCalculating by viewModel.isCalculating.collectAsState()
                        if (isCalculating) {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                androidx.compose.material3.CircularProgressIndicator(color = GoldPrimary)
                            }
                        } else {
                            GoldGlowButton(
                                text = "कुण्डली बनाएं (Generate Chart)",
                                onClick = {
                                    viewModel.generateKundaliChart(viewModel.kundaliName.value, viewModel.kundaliDob.value, viewModel.kundaliTob.value, viewModel.kundaliPlace.value)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "generate_chart_submit_button"
                            )
                        }
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = kundali.personName,
                            style = MaterialTheme.typography.titleMedium.copy(color = TextGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        )
                        Text(
                            text = "जन्म: ${kundali.dateOfBirth} | ${kundali.timeOfBirth} | ${kundali.placeOfBirth}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark, fontSize = 12.sp)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = GoldPrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { }
                    )
                }
            }
        }

        // Chart View Section
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "जन्म कुण्डली (Birth Chart)",
                            style = MaterialTheme.typography.titleSmall.copy(color = TextGold, fontWeight = FontWeight.Bold)
                        )
                        // Style Toggle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(CosmicCardSurface)
                                .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .clickable { isNorthStyle = true }
                                    .background(if (isNorthStyle) GoldPrimary else Color.Transparent)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = LanguageManager.getString("उत्तर भारतीय (North)", "North Indian"),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isNorthStyle) CosmicCardSurface else TextSecondaryDark,
                                        fontWeight = if (isNorthStyle) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clickable { isNorthStyle = false }
                                    .background(if (!isNorthStyle) GoldPrimary else Color.Transparent)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = LanguageManager.getString("दक्षिण भारतीय (South)", "South Indian"),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (!isNorthStyle) CosmicCardSurface else TextSecondaryDark,
                                        fontWeight = if (!isNorthStyle) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    if (isNorthStyle) {
                        NorthIndianChart(kundali)
                    } else {
                        SouthIndianChart(kundali)
                    }
                }
            }
        }

        // Planetary Positions
        item {
            SectionHeader(
                titleHi = "ग्रह स्थिति (Planetary Positions)",
                titleEn = "Planetary Positions"
            )
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
                        Text(text = LanguageManager.getString("ग्रह", "Planet"), style = MaterialTheme.typography.labelSmall.copy(color = TextGold, fontWeight = FontWeight.Bold, fontSize = 13.sp), modifier = Modifier.weight(1f))
                        Text(text = LanguageManager.getString("राशि", "Zodiac"), style = MaterialTheme.typography.labelSmall.copy(color = TextGold, fontWeight = FontWeight.Bold, fontSize = 13.sp), modifier = Modifier.weight(1.2f))
                        Text(text = LanguageManager.getString("अंश", "Deg"), style = MaterialTheme.typography.labelSmall.copy(color = TextGold, fontWeight = FontWeight.Bold, fontSize = 13.sp), modifier = Modifier.weight(1f))
                        Text(text = LanguageManager.getString("भाव", "House"), style = MaterialTheme.typography.labelSmall.copy(color = TextGold, fontWeight = FontWeight.Bold, fontSize = 13.sp), modifier = Modifier.weight(1f))
                    }
                    
                    kundali.planets.forEach { planet ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = LanguageManager.getString(planet.planetNameHi.substringBefore(" "), planet.planetNameEn), style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark, fontSize = 14.sp), modifier = Modifier.weight(1f))
                            Text(text = planet.rashiNameHi, style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark, fontSize = 14.sp), modifier = Modifier.weight(1.2f))
                            Text(text = "${planet.degree}°", style = MaterialTheme.typography.bodySmall.copy(color = TextGold, fontSize = 14.sp, fontWeight = FontWeight.SemiBold), modifier = Modifier.weight(1f))
                            Text(text = "${planet.houseNumber} भाव", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark, fontSize = 14.sp), modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Dasha Table
        item {
            SectionHeader(
                titleHi = "विंशोत्तरी दशा (Vimshottari Dasha)",
                titleEn = "Vimshottari Dasha"
            )
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "वर्तमान महादशा: ${kundali.currentMahadashaHi} | अंतर्दशा: ${kundali.currentAntardashaHi}",
                        style = MaterialTheme.typography.titleMedium.copy(color = SacredOrange, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    kundali.dashaTimeline.forEach { dasha ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${dasha.planetHi} (${dasha.planetEn})",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimaryDark, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            )
                            Text(
                                text = "${dasha.startDate} - ${dasha.endDate} (${dasha.durationYears} वर्ष)",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark, fontSize = 13.sp)
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
}
