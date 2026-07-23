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
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

import com.example.ui.components.SubTabHeader
import com.example.ui.AppTab

@Composable
fun PanchangScreen(viewModel: MainViewModel) {
    val panchang by viewModel.panchangState.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val isChoghadiyaDaytime by viewModel.choghadiyaDaytime.collectAsState()
    val choghadiyaSlots = viewModel.choghadiyaSlots

    val currentSubTab by viewModel.panchangSubTab.collectAsState()

    var showCityDropdown by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val locationPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            viewModel.detectGPSLocation(context) { success ->
                if (!success) {
                    Toast.makeText(context, "स्थिति प्राप्त करने में असमर्थ (Unable to fetch location)", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "अनुमति अस्वीकार कर दी गई (Permission Denied)", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            SubTabHeader(
                selectedTab = currentSubTab,
                tabs = listOf(
                    LanguageManager.getString("दैनिक पंचांग", "Daily Panchang"),
                    LanguageManager.getString("मासिक कैलेंडर", "Monthly Calendar")
                ),
                onTabSelected = { viewModel.setPanchangSubTab(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            if (currentSubTab == 1) {
                CalendarScreen(viewModel)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 8.dp,
                        end = 16.dp,
                        bottom = paddingValues.calculateBottomPadding() + 16.dp
                    ),
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
                                DropdownMenuItem(
                                    text = { Text("📍 वर्तमान स्थान (Current GPS)") },
                                    onClick = {
                                        showCityDropdown = false
                                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                                            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                            viewModel.detectGPSLocation(context) { success ->
                                                if (!success) {
                                                    Toast.makeText(context, "स्थिति प्राप्त करने में असमर्थ (Unable to fetch location)", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } else {
                                            locationPermissionsLauncher.launch(
                                                arrayOf(
                                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                                )
                                            )
                                        }
                                    }
                                )

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
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Tithi Section
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "तिथि (Tithi)",
                                style = MaterialTheme.typography.labelMedium.copy(color = TextSecondaryDark, fontSize = 13.sp)
                            )
                            GlassBadge(text = panchang.pakshaHindi.substringBefore(" "))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = panchang.tithiHindi,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextGold,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = panchang.tithiEndTime,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark, fontSize = 13.sp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (panchang.tithiProgressPercent / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = GoldPrimary,
                            trackColor = GlassWhite
                        )
                    }

                    androidx.compose.material3.HorizontalDivider(color = GlassWhite.copy(alpha = 0.1f), thickness = 1.dp)

                    // Nakshatra Section
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "नक्षत्र (Nakshatra)",
                                style = MaterialTheme.typography.labelMedium.copy(color = TextSecondaryDark, fontSize = 13.sp)
                            )
                            GlassBadge(text = "चंद्र नक्षत्र: ${panchang.moonSign}")
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${panchang.nakshatraHindi} (चरण ${panchang.nakshatraPada})",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextGold,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = panchang.nakshatraEndTime,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark, fontSize = 13.sp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { 0.65f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = GoldPrimary,
                            trackColor = GlassWhite
                        )
                    }

                    androidx.compose.material3.HorizontalDivider(color = GlassWhite.copy(alpha = 0.1f), thickness = 1.dp)

                    // Yoga & Karan Section
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "योग (Yoga)",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark, fontSize = 12.sp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = panchang.yogaHindi,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextGold,
                                    fontSize = 16.sp
                                )
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "करण (Karan)",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark, fontSize = 12.sp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = panchang.karanHindi,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextGold,
                                    fontSize = 16.sp
                                )
                            )
                        }
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
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    val timings = listOf(
                        Triple("अभिजित मुहूर्त (Abhijit)", panchang.abhijitMuhurat, Pair("अति शुभ (Best)", AuspiciousGreen)),
                        Triple("राहुकाल (Rahu Kaal)", panchang.rahuKaal, Pair("अशुभ (Avoid)", InauspiciousRed)),
                        Triple("गुलिक काल (Gulika)", panchang.gulikaKaal, Pair("मध्यम (Neutral)", NeutralOrange)),
                        Triple("यमगण्ड (Yamaganda)", panchang.yamaganda, Pair("अशुभ (Avoid)", InauspiciousRed))
                    )

                    timings.forEachIndexed { index, timing ->
                        val (title, time, status) = timing
                        val (statusText, color) = status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleSmall.copy(color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = time,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimaryDark, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                )
                            }

                            GlassBadge(
                                text = statusText,
                                backgroundColor = color.copy(alpha = 0.15f),
                                textColor = color,
                                borderColor = color.copy(alpha = 0.4f)
                            )
                        }

                        if (index < timings.lastIndex) {
                            androidx.compose.material3.HorizontalDivider(color = GlassWhite.copy(alpha = 0.1f), thickness = 1.dp)
                        }
                    }
                }
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
            PlanetaryPositionsCard(planets = panchang.planets)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
    }
}
}

@Composable
fun InfoPill(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark, fontSize = 13.sp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(color = TextGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                    style = MaterialTheme.typography.labelMedium.copy(color = TextSecondaryDark, fontSize = 13.sp)
                )
                GlassBadge(text = badgeText)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = valueHi,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextGold,
                    fontSize = 18.sp
                )
            )

            Text(
                text = subValueHi,
                style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark, fontSize = 13.sp)
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
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark, fontSize = 12.sp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = valueHi,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextGold,
                    fontSize = 15.sp
                )
            )
        }
    }
}

@Composable
fun TimingColumn(title: String, time: String, icon: ImageVector, iconColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark, fontSize = 12.sp))
        Text(text = time, style = MaterialTheme.typography.labelMedium.copy(color = TextPrimaryDark, fontWeight = FontWeight.Bold, fontSize = 14.sp))
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
                    style = MaterialTheme.typography.titleSmall.copy(color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimaryDark, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
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
@Composable
fun PlanetaryPositionsCard(planets: List<com.example.data.model.PlanetPosition>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        SectionHeader(
            titleHi = "ग्रह स्थिति (Planetary Positions)",
            titleEn = "Current Astrological Positions"
        )
        Spacer(modifier = Modifier.height(12.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                planets.forEach { planet ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(GoldPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = planet.planetNameHi.substring(0, 1),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = TextGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${planet.planetNameHi} (${planet.planetNameEn})",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextGold,
                                        fontSize = 15.sp
                                    )
                                )
                                Text(
                                    text = "नक्षत्र: ${planet.nakshatraHi} | राशि: ${planet.rashiNameHi}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark, fontSize = 13.sp)
                                )
                            }
                        }
                        
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${planet.degree}°",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = TextSecondaryDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                            if (planet.isRetrograde) {
                                Text(
                                    text = "Retrograde (वक्री)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = InauspiciousRed,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
