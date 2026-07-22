package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AuspiciousGreen
import com.example.ui.theme.CosmicCardSurface
import com.example.ui.theme.CosmicDeepNavy
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SacredOrange
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun PremiumDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CosmicDeepNavy,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, GoldPrimary, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_premium_dialog")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
                    }
                }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(GoldPrimary, SacredOrange)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = CosmicDeepNavy, modifier = Modifier.size(32.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "AstroVeda PRO Gold",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextGold,
                        fontSize = 22.sp
                    )
                )

                Text(
                    text = "Unlock Complete Vedic Wisdom & AI Features",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark, fontSize = 12.sp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FeatureRow("100% Ad-Free Glassmorphism Experience")
                    FeatureRow("Unlimited Gemini 1.5 AI Astrologer Queries")
                    FeatureRow("HD PDF Kundali Export & Printing")
                    FeatureRow("Swiss Ephemeris Micro-Second Precision")
                    FeatureRow("Unlimited Saved Kundali Profiles in Room DB")
                }

                Spacer(modifier = Modifier.height(20.dp))

                GoldGlowButton(
                    text = "प्रारंभ करें ₹199/वर्ष (Get PRO Gold)",
                    onClick = { onDismiss() },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "subscribe_pro_gold_button"
                )
            }
        }
    }
}

@Composable
fun FeatureRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = AuspiciousGreen, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextPrimaryDark,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        )
    }
}
