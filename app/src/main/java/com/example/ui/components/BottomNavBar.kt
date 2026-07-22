package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppTab
import com.example.ui.theme.CosmicCardSurface
import com.example.ui.theme.CosmicDeepNavy
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SacredOrange
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextSecondaryDark

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.Settings

data class NavItem(
    val tab: AppTab,
    val titleHi: String,
    val icon: ImageVector
)

@Composable
fun BottomNavBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    val items = listOf(
        NavItem(AppTab.PANCHANG, "पंचांग", Icons.Default.WbSunny),
        NavItem(AppTab.CALENDAR, "कैलेण्डर", Icons.Default.Event),
        NavItem(AppTab.RASHIFAL, "राशिफल", Icons.Default.GridView),
        NavItem(AppTab.KUNDALI, "कुण्डली", Icons.Default.AutoAwesome),
        NavItem(AppTab.MUHURAT, "मुहूर्त", Icons.Default.Schedule),
        NavItem(AppTab.MATCHING, "मिलान", Icons.Default.Favorite),
        NavItem(AppTab.NUMEROLOGY_AI, "अंक/AI", Icons.Default.Calculate),
        NavItem(AppTab.SAVED_PROFILES, "सहेजी", Icons.Default.Bookmark),
        NavItem(AppTab.SETTINGS, "सेटिंग्स", Icons.Default.Settings)
    )

    val haptic = LocalHapticFeedback.current
    val navShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(navShape)
            .border(width = 1.dp, color = GlassBorder, shape = navShape),
        color = CosmicCardSurface.copy(alpha = 0.98f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = (item.tab == selectedTab)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) GoldPrimary.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onTabSelected(item.tab)
                        }
                        .padding(horizontal = 6.dp, vertical = 6.dp)
                        .testTag("nav_item_${item.tab.name.lowercase()}")
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.titleHi,
                        tint = if (isSelected) GoldPrimary else TextSecondaryDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.titleHi,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) TextGold else TextSecondaryDark
                        )
                    )
                }
            }
        }
    }
}
