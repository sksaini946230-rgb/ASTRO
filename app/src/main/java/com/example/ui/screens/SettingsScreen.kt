package com.example.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.astro.PanchangCalculator
import com.example.astro.RashifalProvider
import com.example.data.model.CityLocation
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
import com.example.ui.theme.SacredOrange
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.util.AppLanguage
import com.example.util.LanguageManager

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onShowPremiumDialog: () -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val selectedCity by viewModel.selectedCity.collectAsState()
    val selectedRashiId by viewModel.selectedRashiId.collectAsState()
    val dailyRahuKaalAlert by viewModel.dailyRahuKaalAlert.collectAsState()
    val festivalRemindersAlert by viewModel.festivalRemindersAlert.collectAsState()

    var showRashiDialog by remember { mutableStateOf(false) }
    var showLocationModal by remember { mutableStateOf(false) }
    var isRefreshingLocation by remember { mutableStateOf(false) }

    var showAboutDialog by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var webViewUrlToOpen by remember { mutableStateOf<String?>(null) }
    var webViewTitle by remember { mutableStateOf("") }

    val horoscopes = remember { RashifalProvider.getDailyHoroscope() }
    val currentRashi = horoscopes.find { it.rashiId == selectedRashiId } ?: horoscopes.first()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                titleHi = "ऐप सेटिंग्स (App Settings)",
                titleEn = "Settings",
                subtitleHi = "आपकी पसंद एवं प्राथमिकताओं का अनुकूलन करें",
                subtitleEn = "Customize your preferences & notifications"
            )
        }

        // 7. Upgrade to PRO Premium Banner
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onShowPremiumDialog()
                    }
                    .testTag("settings_pro_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(GoldPrimary, SacredOrange)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "PRO",
                                    tint = CosmicDeepNavy,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "AstroVeda PRO (अपग्रेड करें)",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = TextGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    )
                                )
                                Text(
                                    text = "प्रीमियम वैदिक अनुभव अनलॉक करें",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondaryDark,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        GlassBadge(
                            text = "PRO ₹99/माह",
                            textColor = GoldPrimary,
                            borderColor = GoldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        ProBenefitRow(text = "🚫 100% विज्ञापन रहित अनुभव (No Ads)")
                        ProBenefitRow(text = "📜 विस्तृत 120 वर्ष महादशा एवं कुण्डली फलादेश")
                        ProBenefitRow(text = "💖 अष्टकूट 36 गुण मिलान रिपोर्ट PDF")
                        ProBenefitRow(text = "🤖 एआई अस्ट्रोलॉजर अनलिमिटेड परामर्श")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(GoldPrimary, SacredOrange)
                                )
                            )
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "अभी PRO अपग्रेड करें • ₹99/माह",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = CosmicDeepNavy,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }
        }

        // 1. Language Toggle (ENG / हिं Switch)
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "भाषा (App Language)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = TextGold,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = if (LanguageManager.currentLanguage == AppLanguage.HINDI) "वर्तमान: हिन्दी (Hindi)" else "Current: English",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondaryDark,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(GlassWhite)
                            .border(1.dp, GoldPrimary, RoundedCornerShape(20.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.toggleLanguage()
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("settings_language_toggle")
                    ) {
                        Text(
                            text = if (LanguageManager.currentLanguage == AppLanguage.HINDI) "English ⇄" else "हिन्दी ⇄",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        // 2. Default Rashi Selector Section
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
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
                                .background(GoldPrimary.copy(alpha = 0.15f))
                                .border(1.dp, GoldPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = currentRashi.symbol, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "मुख्य राशि (Default Rashi)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = TextGold,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "${currentRashi.rashiNameHi} • स्वामी: ${currentRashi.rulerHi}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondaryDark,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(GlassWhite)
                            .border(1.dp, SacredOrange, RoundedCornerShape(16.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showRashiDialog = true
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("settings_change_rashi_button")
                    ) {
                        Text(
                            text = "बदलें (Change)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SacredOrange,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // 3. Location Settings (Saved City + Refresh GPS + Manual Fallback)
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = SacredOrange,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "स्थान सेटिंग्स (Location)",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = TextGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "${selectedCity.cityNameHindi} (${selectedCity.state})",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondaryDark,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Refresh Location Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(GlassWhite)
                                    .border(1.dp, GoldPrimary, RoundedCornerShape(16.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        isRefreshingLocation = true
                                        // Simulate location refresh
                                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                            isRefreshingLocation = false
                                            Toast.makeText(context, "GPS स्थान रीफ्रेश: ${selectedCity.cityNameHindi}", Toast.LENGTH_SHORT).show()
                                        }, 1000)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("settings_refresh_location")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isRefreshingLocation) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(12.dp),
                                            color = GoldPrimary,
                                            strokeWidth = 1.5.dp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            tint = GoldPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "GPS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GoldPrimary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            // Manual City Search Fallback Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(GlassWhite)
                                    .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        showLocationModal = true
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("settings_manual_city_button")
                            ) {
                                Text(
                                    text = "शहर खोजें",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextGold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Notifications Toggles (Daily Rahu Kaal Alert & Festival Reminders)
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "अधिसूचनाएं एवं अलर्ट (Notifications)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = TextGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    // Rahu Kaal Alert Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "दैनिक राहुकाल अलर्ट (Daily Rahu Kaal Alert)",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextPrimaryDark,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = "राहुकाल प्रारंभ होने से 15 मिनट पूर्व चेतावनी",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondaryDark,
                                    fontSize = 11.sp
                                )
                            )
                        }
                        Switch(
                            checked = dailyRahuKaalAlert,
                            onCheckedChange = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.toggleRahuKaalAlert()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CosmicDeepNavy,
                                checkedTrackColor = GoldPrimary,
                                uncheckedThumbColor = TextSecondaryDark,
                                uncheckedTrackColor = GlassWhite
                            ),
                            modifier = Modifier.testTag("settings_toggle_rahu_kaal")
                        )
                    }

                    // Festival Reminders Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "त्योहार व व्रत रिमाइंडर (Festival Reminders)",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextPrimaryDark,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = "प्रमुख एकादशी, पूर्णिमा व पर्व की पूर्व सूचना",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondaryDark,
                                    fontSize = 11.sp
                                )
                            )
                        }
                        Switch(
                            checked = festivalRemindersAlert,
                            onCheckedChange = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.toggleFestivalAlert()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CosmicDeepNavy,
                                checkedTrackColor = SacredOrange,
                                uncheckedThumbColor = TextSecondaryDark,
                                uncheckedTrackColor = GlassWhite
                            ),
                            modifier = Modifier.testTag("settings_toggle_festivals")
                        )
                    }
                }
            }
        }

        // 4.5. Live Vedic Astrological & Astronomical News (Grounded via Google Search)
        item {
            val astroNews by viewModel.astroNews.collectAsState()
            val isNewsLoading by viewModel.isNewsLoading.collectAsState()

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = GoldGlow,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "ताज़ा खगोलीय व ज्योतिष समाचार",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = TextGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                                Text(
                                    text = "Vedic Astro & Celestial News",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondaryDark,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        GlassBadge(
                            text = "🔍 Grounded by Google",
                            textColor = AuspiciousGreen,
                            borderColor = AuspiciousGreen
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlassWhite)
                            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        if (isNewsLoading) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = GoldPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "गूगल सर्च द्वारा ताज़ा खगोलीय घटनाएँ खोजी जा रही हैं...",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondaryDark,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        } else {
                            Text(
                                text = astroNews.ifBlank { "खगोलीय व ज्योतिषीय समाचार उपलब्ध हैं।" },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextPrimaryDark,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(GlassWhite)
                                .border(1.dp, GoldPrimary, RoundedCornerShape(12.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.fetchAstroNews()
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("settings_refresh_astro_news"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh News",
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "रीफ्रेश समाचार (Live Search)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GoldPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. About Section (Version, About App, Rate on Play Store)
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAboutDialog = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = GoldGlow,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "AstroVeda के बारे में (About App)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = TextGold,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "संस्करण 2026.1.0 (Build 108) • स्विस् एपिफेमरीस परिशुद्धता",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondaryDark,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Rate Us Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlassWhite)
                            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showRatingDialog = true
                            }
                            .padding(vertical = 10.dp, horizontal = 12.dp)
                            .testTag("settings_rate_us_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "प्ले स्टोर पर 5★ रेटिंग दें (Rate Us on Play Store)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = TextGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // 6. Legal Section (Privacy Policy & Terms of Service)
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "कानूनी एवं गोपनीयता (Legal & Terms)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = TextGold,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Privacy Policy
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(GlassWhite)
                                .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    webViewTitle = "गोपनीयता नीति (Privacy Policy)"
                                    webViewUrlToOpen = "https://astroveda.app/privacy"
                                }
                                .padding(vertical = 10.dp, horizontal = 8.dp)
                                .testTag("settings_privacy_policy_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PrivacyTip,
                                    contentDescription = null,
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "गोपनीयता नीति",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = TextGold,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }

                        // Terms of Service
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(GlassWhite)
                                .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    webViewTitle = "सेवा की शर्तें (Terms of Service)"
                                    webViewUrlToOpen = "https://astroveda.app/terms"
                                }
                                .padding(vertical = 10.dp, horizontal = 8.dp)
                                .testTag("settings_terms_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = SacredOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "सेवा शर्तें (Terms)",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = SacredOrange,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // 2. Rashi Selector Grid Dialog
    if (showRashiDialog) {
        AlertDialog(
            onDismissRequest = { showRashiDialog = false },
            title = {
                Text(
                    text = "अपनी मुख्य राशि चुनें (Select Default Rashi)",
                    color = TextGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(modifier = Modifier.height(320.dp)) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(horoscopes) { rashi ->
                            val isSelected = (rashi.rashiId == selectedRashiId)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) GoldPrimary else GlassWhite)
                                    .border(1.dp, if (isSelected) GoldPrimary else GlassBorder, RoundedCornerShape(12.dp))
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.selectRashi(rashi.rashiId)
                                        showRashiDialog = false
                                    }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = rashi.symbol, fontSize = 22.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = rashi.rashiNameHi.substringBefore(" "),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) CosmicCardSurface else TextGold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRashiDialog = false }) {
                    Text("बंद करें (Close)", color = GoldPrimary)
                }
            },
            containerColor = CosmicCardSurface
        )
    }

    // 3. Manual Location Search Fallback Dialog
    if (showLocationModal) {
        var searchQuery by remember { mutableStateOf("") }
        val filteredCities = remember(searchQuery) {
            PanchangCalculator.popularCities.filter {
                it.cityName.contains(searchQuery, ignoreCase = true) ||
                        it.cityNameHindi.contains(searchQuery) ||
                        it.state.contains(searchQuery, ignoreCase = true)
            }
        }

        AlertDialog(
            onDismissRequest = { showLocationModal = false },
            title = {
                Text(
                    text = "शहर खोजें एवं चुनें (Search City)",
                    color = TextGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(modifier = Modifier.height(340.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("शहर का नाम लिखें (e.g. Jaipur, Varanasi)", color = TextSecondaryDark, fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = TextGold,
                            unfocusedTextColor = TextGold
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(filteredCities.size) { idx ->
                            val city = filteredCities[idx]
                            val isSelected = (city.cityName == selectedCity.cityName)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) GoldPrimary.copy(alpha = 0.2f) else GlassWhite)
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.setCity(city)
                                        showLocationModal = false
                                        Toast.makeText(context, "स्थान सेट किया: ${city.cityNameHindi}", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "${city.cityNameHindi} (${city.cityName})",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = TextGold,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    )
                                    Text(
                                        text = "${city.state} • Lat: ${city.latitude}, Lon: ${city.longitude}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSecondaryDark,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLocationModal = false }) {
                    Text("रद्द करें (Cancel)", color = TextSecondaryDark)
                }
            },
            containerColor = CosmicCardSurface
        )
    }

    // 5. About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text(text = "AstroVeda 2026", color = TextGold, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "AstroVeda (वैदिक पंचांग एवं कुण्डली 2026) भारत का सबसे भरोसेमंद एवं सटीक पंचांग ऐप है। इसमें स्विस् एपिफेमरीस आधारित ग्रहों की उच्च परिशुद्धता गणना, 12 राशियां, चौघड़िया, राहुकाल, व्रत-त्योहार व एआई ज्योतिष परामर्श शामिल हैं।",
                    color = TextPrimaryDark,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("ठीक है (OK)", color = GoldPrimary)
                }
            },
            containerColor = CosmicCardSurface
        )
    }

    // Rating Dialog
    if (showRatingDialog) {
        AlertDialog(
            onDismissRequest = { showRatingDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = GoldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "AstroVeda रेटिंग दें", color = TextGold, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = "AstroVeda ऐप को 5-स्टार रेटिंग देकर हमारा समर्थन करें! गूगल प्ले स्टोर लिंक शीघ्र ही सक्रिय हो जाएगा।",
                    color = TextPrimaryDark,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRatingDialog = false
                        Toast.makeText(context, "धन्यवाद! आपका समर्थन हमारे लिए अनमोल है।", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("5★ रेटिंग दें", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRatingDialog = false }) {
                    Text("बाद में", color = TextSecondaryDark)
                }
            },
            containerColor = CosmicCardSurface
        )
    }

    // 6. Legal In-App WebView Dialog
    if (webViewUrlToOpen != null) {
        AlertDialog(
            onDismissRequest = { webViewUrlToOpen = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = webViewTitle,
                        style = MaterialTheme.typography.titleMedium.copy(color = TextGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    )
                    IconButton(onClick = { webViewUrlToOpen = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GoldPrimary)
                    }
                }
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            WebView(ctx).apply {
                                webViewClient = WebViewClient()
                                settings.javaScriptEnabled = true
                                loadUrl(webViewUrlToOpen ?: "https://astroveda.app/privacy")
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { webViewUrlToOpen = null }) {
                    Text("बंद करें (Close)", color = GoldPrimary)
                }
            },
            containerColor = CosmicCardSurface
        )
    }
}

@Composable
private fun ProBenefitRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextPrimaryDark,
                fontSize = 12.sp
            )
        )
    }
}
