        // Person Summary Header Bar
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = kundali.personName,
                            style = MaterialTheme.typography.titleMedium.copy(color = TextGold, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        )
                        Text(
                            text = "जन्म: ${kundali.dateOfBirth} | ${kundali.timeOfBirth} | ${kundali.placeOfBirth}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark, fontSize = 12.sp)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = GoldPrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { }
                    )
                }
            }
        }

        // Chart View Section
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "जन्म कुण्डली (Birth Chart)",
                            style = MaterialTheme.typography.titleSmall.copy(color = TextGold, fontWeight = FontWeight.Bold)
                        )
                        // Style Toggle
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(CosmicCardSurface)
                                .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .clickable { isNorthStyle = true }
                                    .background(if (isNorthStyle) GoldPrimary else Color.Transparent)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = LanguageManager.getString("उत्तर भारतीय (North)", "North Indian"),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isNorthStyle) CosmicCardSurface else TextSecondaryDark,
                                        fontWeight = if (isNorthStyle) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clickable { isNorthStyle = false }
                                    .background(if (!isNorthStyle) GoldPrimary else Color.Transparent)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = LanguageManager.getString("दक्षिण भारतीय (South)", "South Indian"),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (!isNorthStyle) CosmicCardSurface else TextSecondaryDark,
                                        fontWeight = if (!isNorthStyle) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    if (isNorthStyle) {
                        NorthIndianChart(kundali)
                    } else {
                        SouthIndianChart(kundali)
                    }
                }
            }
        }

        // Planetary Positions
        item {
            SectionHeader(
                titleHi = "ग्रह स्थिति (Planetary Positions)",
                titleEn = "Planetary Positions"
            )
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                        Text(text = LanguageManager.getString("ग्रह", "Planet"), style = MaterialTheme.typography.labelSmall.copy(color = TextGold, fontWeight = FontWeight.Bold, fontSize = 11.sp), modifier = Modifier.weight(1f))
                        Text(text = LanguageManager.getString("राशि", "Zodiac"), style = MaterialTheme.typography.labelSmall.copy(color = TextGold, fontWeight = FontWeight.Bold, fontSize = 11.sp), modifier = Modifier.weight(1.2f))
                        Text(text = LanguageManager.getString("अंश", "Deg"), style = MaterialTheme.typography.labelSmall.copy(color = TextGold, fontWeight = FontWeight.Bold, fontSize = 11.sp), modifier = Modifier.weight(1f))
                        Text(text = LanguageManager.getString("भाव", "House"), style = MaterialTheme.typography.labelSmall.copy(color = TextGold, fontWeight = FontWeight.Bold, fontSize = 11.sp), modifier = Modifier.weight(1f))
                    }
                    
                    kundali.planets.forEach { planet ->
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = LanguageManager.getString(planet.planetNameHi.substringBefore(" "), planet.planetNameEn), style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark, fontSize = 12.sp), modifier = Modifier.weight(1f))
                            Text(text = planet.rashiNameHi, style = MaterialTheme.typography.bodySmall.copy(color = TextPrimaryDark, fontSize = 12.sp), modifier = Modifier.weight(1.2f))
                            Text(text = "${planet.degree}°", style = MaterialTheme.typography.bodySmall.copy(color = TextGold, fontSize = 12.sp), modifier = Modifier.weight(1f))
                            Text(text = "${planet.houseNumber} भाव", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryDark, fontSize = 12.sp), modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Dasha Table
        item {
            SectionHeader(
                titleHi = "विंशोत्तरी दशा (Vimshottari Dasha)",
                titleEn = "Vimshottari Dasha"
            )
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "वर्तमान महादशा: ${kundali.currentMahadashaHi} | अंतर्दशा: ${kundali.currentAntardashaHi}",
                        style = MaterialTheme.typography.titleSmall.copy(color = SacredOrange, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    kundali.vimshottariDasha.forEach { dasha ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${dasha.planetHi} (${dasha.planetEn})",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimaryDark, fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "${dasha.startDate} - ${dasha.endDate} (${dasha.durationYears} वर्ष)",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondaryDark)
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
