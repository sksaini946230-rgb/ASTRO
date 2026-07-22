package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.astro.PanchangCalculator
import com.example.data.model.CityLocation
import com.example.data.model.PanchangData
import com.example.ui.MainViewModel
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.AuspiciousGreen
import com.example.ui.theme.CosmicCardSurface
import com.example.ui.theme.CosmicDeepNavy
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.InauspiciousRed
import com.example.ui.theme.NeutralOrange
import com.example.ui.theme.SacredOrange
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.util.LanguageManager

@Composable
fun PanchangScreen(viewModel: MainViewModel) {
    val panchang by viewModel.panchangState.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val isChoghadiyaDaytime by viewModel.choghadiyaDaytime.collectAsState()
    val choghadiyaSlots = viewModel.choghadiyaSlots

    var showCityDropdown by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Cosmic Banner Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.2.dp, GlassBorder, RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_cosmic_hero_1784710301045),
                    contentDescription = "Cosmic Sky",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, CosmicDeepNavy.copy(alpha = 0.85f))
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = panchang.dateString,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = TextGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = "${panchang.dayOfWeekHindi} | ${panchang.pakshaHindi}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextPrimaryDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        // City Location Picker Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(CosmicCardSurface.copy(alpha = 0.9f))
                                .border(1.dp, GoldPrimary, RoundedCornerShape(20.dp))
                                .clickable { showCityDropdown = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("city_picker_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = "Location",
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = panchang.locationName,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = TextGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            DropdownMenu(
                                expanded = showCityDropdown,
                                onDismissRequest = { showCityDropdown = false }
                            ) {
                                PanchangCalculator.popularCities.forEach { city ->
                                    DropdownMenuItem(
                                        text = { Text("${city.cityNameHindi} (${city.cityName})") },
                                        onClick = {
                                            viewModel.setCity(city)
                                            showCityDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Vikram Samvat & Masa Info Bar
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoPill("विक्रम संवत", "${panchang.vikramSamvat}")
                    InfoPill("शक संवत", "${panchang.sakaSamvat}")
                    InfoPill("मास (Month)", panchang.masaNameHindi.substringBefore(" "))
                }
            }
        }

        // 5 Core Panchang Elements Section
        item {
            SectionHeader(
                titleHi = "पंचांग के 5 मुख्य अंग (Panchang Elements)",
                titleEn = "Core 5 Panchang Elements",
                subtitleHi = "तिथि, नक्षत्र, योग, करण एवं वार",
                subtitleEn = "Tithi, Nakshatra, Yoga, Karan & Var"
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PanchangElementCard(
                    titleHi = "तिथि (Tithi)",
                    titleEn = "Tithi",
                    valueHi = panchang.tithiHindi,
                    subValueHi = panchang.tithiEndTime,
                    progress = panchang.tithiProgressPercent / 100f,
                    badgeText = panchang.pakshaHindi.substringBefore(" ")
                )

                PanchangElementCard(
                    titleHi = "नक्षत्र (Nakshatra)",
                    titleEn = "Nakshatra",
                    valueHi = "${panchang.nakshatraHindi} (चरण ${panchang.nakshatraPada})",
                    subValueHi = panchang.nakshatraEndTime,
                    progress = 0.65f,
                    badgeText = "चंद्र नक्षत्र: ${panchang.moonSign}"
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        SmallElementCard(
                            titleHi = "योग (Yoga)",
                            valueHi = panchang.yogaHindi
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SmallElementCard(
                            titleHi = "करण (Karan)",
                            valueHi = panchang.karanHindi
                        )
                    }
                }
            }
        }

        // Sun & Moon Timings
        item {
            SectionHeader(
                titleHi = "सूर्य एवं चन्द्र समय (Sun & Moon Timings)",
                titleEn = "Sun & Moon Timings"
            )
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TimingColumn("सूर्योदय", panchang.sunrise, Icons.Default.WbSunny, GoldPrimary)
                    TimingColumn("सूर्यास्त", panchang.sunset, Icons.Default.WbSunny, SacredOrange)
                    TimingColumn("चन्द्रास्त", panchang.moonset, Icons.Default.NightsStay, TextSecondaryDark)
                    TimingColumn("चन्द्रोदय", panchang.moonrise, Icons.Default.NightsStay, GoldGlow)
                }
            }
        }

        // Auspicious / Inauspicious Muhurats
        item {
            SectionHeader(
                titleHi = "शुभ एवं अशुभ मुहूर्त (Auspicious & Rahu Timings)",
                titleEn = "Auspicious & Inauspicious Times"
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                MuhuratTimeRow(
                    titleHi = "अभिजित मुहूर्त (Abhijit)",
                    timeStr = panchang.abhijitMuhurat,
                    statusText = "अति शुभ (Best)",
                    color = AuspiciousGreen
                )
                MuhuratTimeRow(
                    titleHi = "राहुकाल (Rahu Kaal)",
                    timeStr = panchang.rahuKaal,
                    statusText = "अशुभ (Avoid)",
                    color = InauspiciousRed
                )
                MuhuratTimeRow(
                    titleHi = "गुलिक काल (Gulika)",
                    timeStr = panchang.gulikaKaal,
                    statusText = "मध्यम (Neutral)",
                    color = NeutralOrange
                )
                MuhuratTimeRow(
                    titleHi = "यमगण्ड (Yamaganda)",
                    timeStr = panchang.yamaganda,
                    statusText = "अशुभ (Avoid)",
                    color = InauspiciousRed
                )
            }
        }

        // Choghadiya Strip Section
        item {
            SectionHeader(
                titleHi = "आज का चौघड़िया (Today's Choghadiya)",
                titleEn = "Choghadiya Time Strip",
                subtitleHi = "शुभ, अमृत, लाभ, चर, उद्वेग, काल व रोग",
                subtitleEn = "Real-time Choghadiya calculations for ${panchang.locationName}"
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isChoghadiyaDaytime) "दिन का चौघड़िया (Day)" else "रात्रि चौघड़िया (Night)",
                    style = MaterialTheme.typography.titleSmall.copy(color = TextGold, fontWeight = FontWeight.Bold)
                )

                Row {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isChoghadiyaDaytime) GoldPrimary else GlassWhite)
                            .clickable { viewModel.toggleChoghadiyaDayNight(true) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "दिन (Day)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isChoghadiyaDaytime) CosmicCardSurface else TextGold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (!isChoghadiyaDaytime) GoldPrimary else GlassWhite)
                            .clickable { viewModel.toggleChoghadiyaDayNight(false) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "रात (Night)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (!isChoghadiyaDaytime) CosmicCardSurface else TextGold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Horizontal Choghadiya Strip Cards
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(choghadiyaSlots) { slot ->
                    val statusColor = when (slot.type.name) {
                        "AMRIT", "SHUBH", "LABH" -> AuspiciousGreen
                        "CHAR" -> NeutralOrange
                        else -> InauspiciousRed
                    }

                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CosmicCardSurface)
                            .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = slot.type.nameHi,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextGold,
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = slot.type.natureHi,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = slot.timeSlotString,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimaryDark,
                                    fontSize = 11.sp
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

@Composable
fun InfoPill(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark, fontSize = 11.sp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(color = TextGold, fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun PanchangElementCard(
    titleHi: String,
    titleEn: String,
    valueHi: String,
    subValueHi: String,
    progress: Float,
    badgeText: String
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = titleHi,
                    style = MaterialTheme.typography.labelMedium.copy(color = TextSecondaryDark, fontSize = 12.sp)
                )
                GlassBadge(text = badgeText)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = valueHi,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextGold,
                    fontSize = 17.sp
                )
            )

            Text(
                text = subValueHi,
                style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark, fontSize = 12.sp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = GoldPrimary,
                trackColor = GlassWhite
            )
        }
    }
}

@Composable
fun SmallElementCard(titleHi: String, valueHi: String) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Text(
                text = titleHi,
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark, fontSize = 11.sp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = valueHi,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextGold,
                    fontSize = 14.sp
                )
            )
        }
    }
}

@Composable
fun TimingColumn(title: String, time: String, icon: ImageVector, iconColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = title, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark, fontSize = 10.sp))
        Text(text = time, style = MaterialTheme.typography.labelMedium.copy(color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 12.sp))
    }
}

@Composable
fun MuhuratTimeRow(titleHi: String, timeStr: String, statusText: String, color: Color) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = color.copy(alpha = 0.25f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = titleHi,
                    style = MaterialTheme.typography.titleSmall.copy(color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimaryDark, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                )
            }

            GlassBadge(
                text = statusText,
                backgroundColor = color.copy(alpha = 0.15f),
                textColor = color,
                borderColor = color.copy(alpha = 0.4f)
            )
        }
    }
}
