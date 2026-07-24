package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RashifalData
import com.example.ui.MainViewModel
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.PersonalizedInsightCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.GlassBorder
import com.example.util.LanguageManager

import com.example.ui.components.OfflineStatusChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RashifalScreen(viewModel: MainViewModel) {
    val haptic = LocalHapticFeedback.current
    val horoscopes by viewModel.dailyHoroscopesState.collectAsState()
    val selectedRashiId by viewModel.selectedRashiId.collectAsState()

    val currentHoroscope = horoscopes.find { it.rashiId == selectedRashiId } ?: horoscopes.firstOrNull() ?: com.example.data.model.RashifalData(
        rashiId = 1,
        rashiNameEn = "Aries",
        rashiNameHi = "मेष",
        symbol = "♈",
        elementHi = "अग्नि",
        rulerHi = "मंगल",
        ratingStars = 4,
        luckyNumber = 9,
        luckyColorEn = "Red",
        luckyColorHi = "लाल",
        luckyStoneHi = "मूंगा",
        generalReadingHi = "आज का दिन अच्छा रहेगा।",
        generalReadingEn = "Today will be a good day.",
        careerReadingHi = "",
        careerReadingEn = "",
        healthReadingHi = "",
        healthReadingEn = "",
        loveReadingHi = "",
        loveReadingEn = "",
        financeReadingHi = "",
        financeReadingEn = ""
    )

    var selectedPeriod by remember { mutableStateOf("TODAY") } // "TODAY", "WEEK", "MONTH"

    val dateRangeText = when (selectedPeriod) {
        "TODAY" -> "आज का दैनिक राशिफल (Today)"
        "WEEK" -> "इस सप्ताह का राशिफल (This Week)"
        else -> "इस महीने का राशिफल (This Month)"
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        val isHoroscopeLoading by viewModel.isHoroscopeLoading.collectAsState()
        PullToRefreshBox(
            isRefreshing = isHoroscopeLoading,
            onRefresh = { viewModel.refreshHoroscopes() },
            modifier = Modifier.fillMaxSize().testTag("rashifal_swipe_refresh")
        ) {
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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                OfflineStatusChip("Offline Mode: Showing cached daily horoscope")
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                titleHi = "12 राशियां (Zodiac Signs)",
                titleEn = "Select Zodiac Sign",
                subtitleHi = "अपनी राशि चुनें व संपूर्ण भविष्यफल देखें",
                subtitleEn = "Tap any zodiac sign for detailed horoscope"
            )
        }

        // 1: Horizontal scrollable selector for all 12 rashis with zodiac icons
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(horoscopes) { item ->
                    val isSelected = (item.rashiId == selectedRashiId)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else GlassBorder, RoundedCornerShape(16.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.selectRashi(item.rashiId)
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("rashi_selector_${item.rashiId}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.symbol,
                                fontSize = 18.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = LanguageManager.getString(item.rashiNameHi.substringBefore(" "), item.rashiNameEn),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // 2: Today / This Week / This Month Period Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                listOf(
                    Pair("TODAY", "आज का राशिफल"),
                    Pair("WEEK", "इस सप्ताह"),
                    Pair("MONTH", "इस महीने")
                ).forEach { (code, label) ->
                    val isSelected = (selectedPeriod == code)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, if (isSelected) MaterialTheme.colorScheme.secondary else GlassBorder, RoundedCornerShape(20.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                selectedPeriod = code
                            }
                            .padding(horizontal = 18.dp, vertical = 8.dp)
                            .testTag("period_toggle_$code")
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
        }

        // 6: Smooth Flip/Fade Transition Animation when Rashi changes
        item {
            AnimatedContent(
                targetState = currentHoroscope,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.95f, animationSpec = tween(300))) togetherWith
                            fadeOut(animationSpec = tween(200))
                },
                label = "RashiCardFlip"
            ) { horoscope ->
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Hero Rashi Card with 5-Star Rating & Lucky Chips
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                            .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = horoscope.symbol, fontSize = 28.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = LanguageManager.getString(horoscope.rashiNameHi, horoscope.rashiNameEn),
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontSize = 20.sp
                                            )
                                        )
                                        Text(
                                            text = "${LanguageManager.getString("स्वामी", "Lord")}: ${horoscope.rulerHi} | ${LanguageManager.getString("तत्व", "Element")}: ${horoscope.elementHi}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }

                                // 4: 5-Star Score Display with animated gold fill
                                AnimatedStarScoreDisplay(rating = horoscope.ratingStars)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Date Range Banner
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "📅 $dateRangeText",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // 5: Lucky Number, Lucky Color, Lucky Stone Chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                GlassBadge(
                                    text = "शुभ अंक: ${horoscope.luckyNumber}",
                                    textColor = MaterialTheme.colorScheme.primary,
                                    borderColor = MaterialTheme.colorScheme.primary
                                )
                                GlassBadge(
                                    text = "शुभ रंग: ${horoscope.luckyColorHi}",
                                    textColor = MaterialTheme.colorScheme.primary,
                                    borderColor = MaterialTheme.colorScheme.primary
                                )
                                GlassBadge(
                                    text = "शुभ रत्न: ${horoscope.luckyStoneHi}",
                                    textColor = MaterialTheme.colorScheme.secondary,
                                    borderColor = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }

                    // 3: General Overview in Hindi
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = LanguageManager.getString("सामान्य भविष्यफल", "General Overview"),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = LanguageManager.getString(
                                    horoscope.generalReadingHi,
                                    horoscope.generalReadingEn
                                ),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp
                                )
                            )
                        }
                    }

                    // 3: Domain Categories (Career, Health, Love, Finance) in Hindi
                    SectionHeader(
                        titleHi = "क्षेत्रवार फलादेश (Category Readings)",
                        titleEn = "Category Breakdown"
                    )

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            val domains = listOf(
                                DomainItem(
                                    "करियर व व्यवसाय (Career & Business)",
                                    horoscope.careerReadingHi,
                                    horoscope.careerReadingEn,
                                    Icons.Default.Work,
                                    MaterialTheme.colorScheme.primary
                                ),
                                DomainItem(
                                    "स्वास्थ्य एवं ऊर्जा (Health & Fitness)",
                                    horoscope.healthReadingHi,
                                    horoscope.healthReadingEn,
                                    Icons.Default.FitnessCenter,
                                    MaterialTheme.colorScheme.tertiary // Assuming AuspiciousGreen is tertiary
                                ),
                                DomainItem(
                                    "प्रेम व संबंध (Love & Marriage)",
                                    horoscope.loveReadingHi,
                                    horoscope.loveReadingEn,
                                    Icons.Default.Favorite,
                                    MaterialTheme.colorScheme.secondary
                                ),
                                DomainItem(
                                    "वित्त व धन लाभ (Finance & Money)",
                                    horoscope.financeReadingHi,
                                    horoscope.financeReadingEn,
                                    Icons.Default.MonetizationOn,
                                    MaterialTheme.colorScheme.primary // Assuming GoldGlow is primary
                                )
                            )

                            domains.forEachIndexed { index, item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(item.color.copy(alpha = 0.15f))
                                            .border(1.dp, item.color.copy(alpha = 0.5f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = null,
                                            tint = item.color,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = LanguageManager.getString(item.readingHi, item.readingEn),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontSize = 14.sp,
                                                lineHeight = 20.sp
                                            )
                                        )
                                    }
                                }

                                if (index < domains.lastIndex) {
                                    androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), thickness = 1.dp)
                                }
                            }
                        }
                    }

                    // New: AI Personalized Insight
                    val aiInsight by viewModel.aiRashifalInsight.collectAsState()
                    val isAiLoading by viewModel.isRashifalAiLoading.collectAsState()

                    PersonalizedInsightCard(
                        insight = aiInsight,
                        isLoading = isAiLoading,
                        onFetchInsight = {
                            viewModel.fetchPersonalizedInsight(currentHoroscope.rashiNameEn)
                        }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
}
}

data class DomainItem(
    val title: String,
    val readingHi: String,
    val readingEn: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun AnimatedStarScoreDisplay(rating: Int) {
    val progressAnim = remember { Animatable(0f) }

    LaunchedEffect(rating) {
        progressAnim.snapTo(0f)
        progressAnim.animateTo(
            targetValue = rating.toFloat(),
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
    }

    Column(horizontalAlignment = Alignment.End) {
        Text(
            text = "$rating / 5",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 11.sp
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row {
            repeat(5) { index ->
                val starFill = (progressAnim.value - index).coerceIn(0f, 1f)
                val scale = if (starFill > 0f) 1f + (starFill * 0.15f) else 1f

                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (starFill > 0.5f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .size(18.dp)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            alpha = if (starFill > 0f) 1f else 0.4f
                        )
                )
            }
        }
    }
}

@Composable
fun HoroscopeDomainCard(
    titleHi: String,
    readingHi: String,
    readingEn: String,
    icon: ImageVector,
    accentColor: Color
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = accentColor.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f))
                    .border(1.dp, accentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titleHi,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = LanguageManager.getString(readingHi, readingEn),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                )
            }
        }
    }
}


