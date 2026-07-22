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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChoghadiyaSlot
import com.example.data.model.ChoghadiyaType
import com.example.data.model.MuhuratItem
import com.example.ui.MainViewModel
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.AuspiciousGreen
import com.example.ui.theme.CosmicCardSurface
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.InauspiciousRed
import com.example.ui.theme.NeutralOrange
import com.example.ui.theme.SacredOrange
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun MuhuratScreen(viewModel: MainViewModel) {
    val isDaytime by viewModel.choghadiyaDaytime.collectAsState()
    val slots = viewModel.choghadiyaSlots
    val muhurats = viewModel.upcomingMuhurats

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                titleHi = "आज का चौघड़िया (Today's Choghadiya)",
                titleEn = "Choghadiya Timings"
            )
        }

        // Day / Night Toggle Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isDaytime) GoldPrimary else GlassWhite)
                        .clickable { viewModel.toggleChoghadiyaDayNight(true) }
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .testTag("choghadiya_day_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = "Day",
                            tint = if (isDaytime) CosmicCardSurface else GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "दिन का चौघड़िया (Day)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isDaytime) CosmicCardSurface else TextGold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (!isDaytime) GoldPrimary else GlassWhite)
                        .clickable { viewModel.toggleChoghadiyaDayNight(false) }
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .testTag("choghadiya_night_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NightsStay,
                            contentDescription = "Night",
                            tint = if (!isDaytime) CosmicCardSurface else GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "रात का चौघड़िया (Night)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (!isDaytime) CosmicCardSurface else TextGold
                            )
                        )
                    }
                }
            }
        }

        // Choghadiya Slots List
        items(slots) { slot ->
            ChoghadiyaRow(slot)
        }

        // Event-Based Muhurat Finder Header
        item {
            SectionHeader(
                titleHi = "कार्यानुसार शुभ मुहूर्त (Event Muhurat Finder)",
                titleEn = "Event Muhurat Finder",
                subtitleHi = "विवाह, गृह प्रवेश, व्यापार, वाहन व यात्रा मुहूर्त",
                subtitleEn = "Wedding, Housewarming, Business & Vehicle"
            )
        }

        // Muhurat List
        items(muhurats) { item ->
            EventMuhuratCard(item)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ChoghadiyaRow(slot: ChoghadiyaSlot) {
    val statusColor = when (slot.type) {
        ChoghadiyaType.AMRIT, ChoghadiyaType.SHUBH, ChoghadiyaType.LABH -> AuspiciousGreen
        ChoghadiyaType.CHAR -> NeutralOrange
        else -> InauspiciousRed
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = statusColor.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = slot.type.nameHi,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextGold,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(${slot.type.natureHi})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Text(
                    text = "समय: ${slot.timeSlotString}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextPrimaryDark,
                        fontSize = 13.sp
                    )
                )
            }

            GlassBadge(
                text = "स्वामी: ${slot.rulerPlanetHi}",
                backgroundColor = statusColor.copy(alpha = 0.15f),
                textColor = statusColor,
                borderColor = statusColor
            )
        }
    }
}

@Composable
fun EventMuhuratCard(item: MuhuratItem) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.categoryHi,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextGold,
                        fontSize = 16.sp
                    )
                )

                GlassBadge(
                    text = item.qualityHi,
                    backgroundColor = AuspiciousGreen.copy(alpha = 0.2f),
                    textColor = AuspiciousGreen,
                    borderColor = AuspiciousGreen
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${item.dateString} (${item.dayOfWeekHi}) | ${item.startTime} - ${item.endTime}",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = SacredOrange,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = "तिथि: ${item.tithiHi} | नक्षत्र: ${item.nakshatraHi}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondaryDark,
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.descriptionHi,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextPrimaryDark,
                    fontSize = 13.sp
                )
            )
        }
    }
}
