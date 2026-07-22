package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GunaKootDetail
import com.example.service.MatchingPdfReportService
import com.example.ui.MainViewModel
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.GoldGlowButton
import com.example.ui.components.SectionHeader
import com.example.ui.theme.AuspiciousGreen
import com.example.ui.theme.CosmicCardSurface
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassWhite
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.InauspiciousRed
import com.example.ui.theme.SacredOrange
import com.example.ui.theme.TextGold
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.util.LanguageManager

@Composable
fun MatchingScreen(viewModel: MainViewModel) {
    val haptic = LocalHapticFeedback.current
    val gunaResult by viewModel.gunaResult.collectAsState()

    var boyName by remember { mutableStateOf(viewModel.matchBoyName.value) }
    var boyDob by remember { mutableStateOf(viewModel.matchBoyDob.value) }

    var girlName by remember { mutableStateOf(viewModel.matchGirlName.value) }
    var girlDob by remember { mutableStateOf(viewModel.matchGirlDob.value) }

    var showForm by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                titleHi = "गुण मिलान (36 Guna Kundali Matching)",
                titleEn = "Kundali Matching (Ashtakoot)"
            )
        }

        // Boy & Girl Details Form Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val tfColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = TextPrimaryDark,
                        unfocusedTextColor = TextPrimaryDark,
                        focusedLabelColor = TextGold,
                        unfocusedLabelColor = TextSecondaryDark
                    )

                    // Boy Details
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Male, contentDescription = "Boy", tint = GoldPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "वर का विवरण (Boy's Details):", style = MaterialTheme.typography.titleSmall.copy(color = TextGold, fontWeight = FontWeight.Bold))
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = boyName,
                            onValueChange = {
                                boyName = it
                                viewModel.matchBoyName.value = it
                            },
                            label = { Text("वर का नाम (Boy Name)") },
                            colors = tfColors,
                            modifier = Modifier.weight(1.2f).testTag("input_boy_name")
                        )

                        OutlinedTextField(
                            value = boyDob,
                            onValueChange = {
                                boyDob = it
                                viewModel.matchBoyDob.value = it
                            },
                            label = { Text("जन्म तिथि") },
                            colors = tfColors,
                            modifier = Modifier.weight(1f).testTag("input_boy_dob")
                        )
                    }

                    // Girl Details
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Female, contentDescription = "Girl", tint = SacredOrange)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "कन्या का विवरण (Girl's Details):", style = MaterialTheme.typography.titleSmall.copy(color = TextGold, fontWeight = FontWeight.Bold))
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = girlName,
                            onValueChange = {
                                girlName = it
                                viewModel.matchGirlName.value = it
                            },
                            label = { Text("कन्या का नाम (Girl Name)") },
                            colors = tfColors,
                            modifier = Modifier.weight(1.2f).testTag("input_girl_name")
                        )

                        OutlinedTextField(
                            value = girlDob,
                            onValueChange = {
                                girlDob = it
                                viewModel.matchGirlDob.value = it
                            },
                            label = { Text("जन्म तिथि") },
                            colors = tfColors,
                            modifier = Modifier.weight(1f).testTag("input_girl_dob")
                        )
                    }

                    GoldGlowButton(
                        text = "गुण मिलान करें (Calculate 36 Guna)",
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.calculateGunaMatching()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "calculate_guna_button"
                    )
                }
            }
        }

        // Score Card Summary
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${gunaResult.boyName} ♥ ${gunaResult.girlName}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TextGold,
                            fontSize = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${gunaResult.totalObtainedGuna} / 36.0",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = if (gunaResult.totalObtainedGuna >= 18) AuspiciousGreen else InauspiciousRed
                        )
                    )

                    Text(
                        text = "कुल प्राप्त गुण (Obtained Guna Score)",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark, fontSize = 11.sp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { (gunaResult.totalObtainedGuna / 36.0).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (gunaResult.totalObtainedGuna >= 18) AuspiciousGreen else InauspiciousRed,
                        trackColor = GlassWhite
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassBadge(
                        text = gunaResult.compatibilityVerdictHi,
                        backgroundColor = (if (gunaResult.totalObtainedGuna >= 18) AuspiciousGreen else InauspiciousRed).copy(alpha = 0.2f),
                        textColor = if (gunaResult.totalObtainedGuna >= 18) AuspiciousGreen else InauspiciousRed,
                        borderColor = if (gunaResult.totalObtainedGuna >= 18) AuspiciousGreen else InauspiciousRed
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val context = LocalContext.current
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlassWhite)
                            .border(1.dp, GoldPrimary, RoundedCornerShape(12.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                val pdfFile = MatchingPdfReportService.generatePdfReport(context, gunaResult)
                                if (pdfFile != null) {
                                    MatchingPdfReportService.sharePdfReport(context, pdfFile)
                                }
                            }
                            .padding(vertical = 10.dp)
                            .testTag("share_pdf_report_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = "PDF Report",
                                tint = GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PDF रिपोर्ट शेयर / प्रिंट करें (Export PDF Report)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Mangal Dosha Compatibility Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "मंगल दोष विचार (Mangal Dosha Analysis)",
                        style = MaterialTheme.typography.titleSmall.copy(color = TextGold, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = gunaResult.mangalDoshaStatusHi,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark, fontSize = 13.sp)
                    )
                }
            }
        }

        // Ashtakoot 8 Breakdown Table Header
        item {
            SectionHeader(
                titleHi = "अष्टकूट विवरण (8 Koota Breakdown Table)",
                titleEn = "Ashtakoot Score Table"
            )
        }

        // Koota Details Table
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "कूट (Koota)", style = MaterialTheme.typography.labelSmall.copy(color = TextGold, fontWeight = FontWeight.Bold), modifier = Modifier.weight(1.5f))
                        Text(text = "अधिकतम", style = MaterialTheme.typography.labelSmall.copy(color = TextGold, fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f))
                        Text(text = "प्राप्त", style = MaterialTheme.typography.labelSmall.copy(color = TextGold, fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f))
                    }

                    gunaResult.kootDetails.forEach { koot ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = koot.kootNameHi, style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark, fontSize = 12.sp), modifier = Modifier.weight(1.5f))
                            Text(text = "${koot.maxPoints}", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark, fontSize = 12.sp), modifier = Modifier.weight(1f))
                            Text(text = "${koot.obtainedPoints}", style = MaterialTheme.typography.bodySmall.copy(color = AuspiciousGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp), modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Summary Reading Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "विवाह निष्कर्ष रिपोर्ट (Summary Report):",
                        style = MaterialTheme.typography.titleSmall.copy(color = TextGold, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = gunaResult.summaryReadingHi,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark, fontSize = 13.sp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
