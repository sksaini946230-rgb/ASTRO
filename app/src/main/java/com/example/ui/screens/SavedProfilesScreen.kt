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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.KundaliEntity
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CosmicCardSurface
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.InauspiciousRed
import com.example.ui.theme.SacredOrange
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark

@Composable
fun SavedProfilesScreen(viewModel: MainViewModel) {
    val profiles by viewModel.savedProfiles.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                titleHi = "सहेजी गई कुण्डलियां (Saved Profiles)",
                titleEn = "Saved Birth Charts",
                subtitleHi = "Room Database ऑफलाइन संग्रहण",
                subtitleEn = "Offline Local Room Database"
            )
        }

        if (profiles.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.Bookmark, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "कोई कुण्डली प्रोफाइल सहेजी नहीं गई है।",
                            style = MaterialTheme.typography.titleSmall.copy(color = TextGold, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "'कुण्डली' टैब में जाएं और अपनी कुण्डली सहेजें।",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark, fontSize = 12.sp)
                        )
                    }
                }
            }
        } else {
            items(profiles) { profile ->
                SavedProfileCard(
                    profile = profile,
                    onOpen = {
                        viewModel.generateKundaliChart(
                            profile.name, profile.dateOfBirth, profile.timeOfBirth, profile.placeOfBirth
                        )
                        viewModel.selectTab(AppTab.KUNDALI)
                    },
                    onDelete = { viewModel.deleteProfile(profile) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SavedProfileCard(
    profile: KundaliEntity,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GlassWhite)
                        .border(1.dp, GlassBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(22.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = profile.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextGold, fontSize = 16.sp)
                    )
                    Text(
                        text = "${profile.dateOfBirth} | ${profile.timeOfBirth}",
                        style = MaterialTheme.typography.bodySmall.copy(color = SacredOrange, fontSize = 12.sp)
                    )
                    Text(
                        text = profile.placeOfBirth,
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark, fontSize = 11.sp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onOpen, modifier = Modifier.testTag("open_profile_${profile.id}")) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = "Open", tint = GoldPrimary)
                }

                IconButton(onClick = onDelete, modifier = Modifier.testTag("delete_profile_${profile.id}")) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = InauspiciousRed)
                }
            }
        }
    }
}
