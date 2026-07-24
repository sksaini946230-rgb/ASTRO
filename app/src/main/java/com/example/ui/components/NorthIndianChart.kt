package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KundaliChartData
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.PremiumGold
import com.example.ui.theme.DateOrange
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun NorthIndianChart(
    chartData: KundaliChartData,
    modifier: Modifier = Modifier,
    onHouseClick: (houseNum: Int, rashiNum: Int, planets: List<String>) -> Unit = { _, _, _ -> }
) {
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.95f) }
    
    LaunchedEffect(chartData) {
        alphaAnim.animateTo(1f, animationSpec = tween(600))
        scaleAnim.animateTo(1f, animationSpec = tween(600))
    }

    var selectedHouse by remember { mutableStateOf(1) }
    val textMeasurer = rememberTextMeasurer()

    val lagnaRashi = chartData.ascendantRashiNumber

    // House to Rashi calculation (Counter-clockwise: House H -> Rashi = (lagnaRashi + H - 2) % 12 + 1)
    fun getRashiForHouse(h: Int): Int {
        return (lagnaRashi + h - 2) % 12 + 1
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
            .testTag("north_indian_chart_canvas"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val strokeWidth = 2.5f

            val chartLineColor = PremiumGold
            val gridBorderColor = PremiumGold.copy(alpha = 0.35f)

            // Outer Square
            drawRect(
                color = gridBorderColor,
                style = Stroke(width = strokeWidth)
            )

            // Main Diagonals
            drawLine(chartLineColor, Offset(0f, 0f), Offset(w, h), strokeWidth = strokeWidth)
            drawLine(chartLineColor, Offset(w, 0f), Offset(0f, h), strokeWidth = strokeWidth)

            // Inner Diamond
            val pTop = Offset(w / 2f, 0f)
            val pRight = Offset(w, h / 2f)
            val pBottom = Offset(w / 2f, h)
            val pLeft = Offset(0f, h / 2f)

            val diamondPath = Path().apply {
                moveTo(pTop.x, pTop.y)
                lineTo(pRight.x, pRight.y)
                lineTo(pBottom.x, pBottom.y)
                lineTo(pLeft.x, pLeft.y)
                close()
            }
            drawPath(diamondPath, color = chartLineColor, style = Stroke(width = strokeWidth))

            // Approximate center positions for 12 Houses
            val housePositions = mapOf(
                1 to Offset(w * 0.5f, h * 0.25f),  // House 1 (Top Center Diamond)
                2 to Offset(w * 0.25f, h * 0.12f), // House 2 (Top Left Triangle)
                3 to Offset(w * 0.12f, h * 0.25f), // House 3
                4 to Offset(w * 0.25f, h * 0.5f),  // House 4 (Left Center Diamond)
                5 to Offset(w * 0.12f, h * 0.75f), // House 5
                6 to Offset(w * 0.25f, h * 0.88f), // House 6
                7 to Offset(w * 0.5f, h * 0.75f),  // House 7 (Bottom Center Diamond)
                8 to Offset(w * 0.75f, h * 0.88f), // House 8
                9 to Offset(w * 0.88f, h * 0.75f), // House 9
                10 to Offset(w * 0.75f, h * 0.5f), // House 10 (Right Center Diamond)
                11 to Offset(w * 0.88f, h * 0.25f),// House 11
                12 to Offset(w * 0.75f, h * 0.12f) // House 12
            )

            for (houseNum in 1..12) {
                val pos = housePositions[houseNum] ?: Offset(0f, 0f)
                val rashiNum = getRashiForHouse(houseNum)
                val planets = chartData.housePlanetsMap[houseNum] ?: emptyList()

                val rashiText = "$rashiNum"
                val planetsText = if (planets.isNotEmpty()) planets.joinToString(" ") else ""

                // Draw Rashi Number in Gold
                drawText(
                    textMeasurer = textMeasurer,
                    text = rashiText,
                    topLeft = Offset(pos.x - 12f, pos.y - 20f),
                    style = TextStyle(
                        color = TextGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                // Draw Planet names in DateOrange
                if (planetsText.isNotEmpty()) {
                    drawText(
                        textMeasurer = textMeasurer,
                        text = planetsText,
                        topLeft = Offset(pos.x - 24f, pos.y + 2f),
                        style = TextStyle(
                            color = DateOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

        // Center Legend / Lagna Indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "उत्तर भारतीय लग्न कुण्डली",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = PremiumGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )
            Text(
                text = "लग्न: ${chartData.ascendantRashiHi}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondaryDark,
                    fontSize = 9.sp
                )
            )
        }
    }
}
