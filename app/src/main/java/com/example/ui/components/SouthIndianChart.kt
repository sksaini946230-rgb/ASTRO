package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.model.KundaliChartData
import com.example.ui.theme.DateOrange
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun SouthIndianChart(
    chartData: KundaliChartData,
    modifier: Modifier = Modifier
) {
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.95f) }
    
    LaunchedEffect(chartData) {
        alphaAnim.animateTo(1f, animationSpec = tween(600))
        scaleAnim.animateTo(1f, animationSpec = tween(600))
    }

    // Fixed South Indian Rashi Grid (12 boxes around a 4x4 perimeter)
    // Row 1: 12 (Pisces), 1 (Aries), 2 (Taurus), 3 (Gemini)
    // Row 2: 11 (Aquarius), CENTER 2x2, 4 (Cancer)
    // Row 3: 10 (Capricorn), CENTER 2x2, 5 (Leo)
    // Row 4: 9 (Sagittarius), 8 (Scorpio), 7 (Libra), 6 (Virgo)

    val gridLayout = listOf(
        listOf(12, 1, 2, 3),
        listOf(11, 0, 0, 4),
        listOf(10, 0, 0, 5),
        listOf(9, 8, 7, 6)
    )

    val rashiShortHi = listOf("", "मेष", "वृष", "मिथुन", "कर्क", "सिंह", "कन्या", "तुला", "वृश्चिक", "धनु", "मकर", "कुंभ", "मीन")

    // Map rashi to planets
    val rashiPlanetsMap = mutableMapOf<Int, MutableList<String>>()
    for (r in 1..12) rashiPlanetsMap[r] = mutableListOf()

    chartData.planets.forEach { p ->
        val shortName = p.planetNameHi.substringBefore(" ")
        rashiPlanetsMap[p.rashiNumber]?.add(shortName)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .graphicsLayer {
                alpha = alphaAnim.value
                scaleX = scaleAnim.value
                scaleY = scaleAnim.value
            }
            .clip(RoundedCornerShape(20.dp))
            .background(GlassWhite)
            .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            .padding(4.dp)
            .testTag("south_indian_chart"),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            for (r in 0..3) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    for (c in 0..3) {
                        val rashiNum = gridLayout[r][c]
                        if (rashiNum == 0) {
                            // Center Empty Cell (Occupies 2x2)
                            if (r == 1 && c == 1) {
                                Box(
                                    modifier = Modifier
                                        .weight(2f)
                                        .fillMaxHeight()
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "दक्षिण भारतीय कुण्डली",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = PremiumGold,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        )
                                        Text(
                                            text = "लग्न: ${chartData.ascendantRashiHi}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = TextSecondaryDark,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                            }
                        } else {
                            val isLagna = (rashiNum == chartData.ascendantRashiNumber)
                            val planetsInRashi = rashiPlanetsMap[rashiNum] ?: emptyList()

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .border(1.dp, GlassBorder)
                                    .padding(4.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${rashiShortHi.getOrElse(rashiNum) { "" }} ($rashiNum)",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = TextGold,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        if (isLagna) {
                                            Text(
                                                text = " [ल]",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = DateOrange,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 9.sp
                                                )
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = planetsInRashi.joinToString(" "),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextPrimaryDark,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold
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
}
