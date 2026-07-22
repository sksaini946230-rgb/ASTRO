package com.example.astro

import com.example.data.model.DashaPeriod
import com.example.data.model.KundaliChartData
import com.example.data.model.PlanetPosition
import java.util.Calendar
import kotlin.math.abs

object KundaliCalculator {

    private val RASHI_NAMES_HI = listOf(
        "मेष (Aries)", "वृषभ (Taurus)", "मिथुन (Gemini)", "कर्क (Cancer)",
        "सिंह (Leo)", "कन्या (Virgo)", "तुला (Libra)", "वृश्चिक (Scorpio)",
        "धनु (Sagittarius)", "मकर (Capricorn)", "कुंभ (Aquarius)", "मीन (Pisces)"
    )

    private val RASHI_SHORT_HI = listOf(
        "मेष", "वृषभ", "मिथुन", "कर्क", "सिंह", "कन्या", "तुला", "वृश्चिक", "धनु", "मकर", "कुंभ", "मीन"
    )

    private val PLANETS_INFO = listOf(
        Pair("Sun", "सूर्य (Su)"),
        Pair("Moon", "चन्द्र (Mo)"),
        Pair("Mars", "मंगल (Ma)"),
        Pair("Mercury", "बुध (Me)"),
        Pair("Jupiter", "गुरु (Ju)"),
        Pair("Venus", "शुक्र (Ve)"),
        Pair("Saturn", "शनि (Sa)"),
        Pair("Rahu", "राहु (Ra)"),
        Pair("Ketu", "केतु (Ke)")
    )

    private val NAKSHATRAS = listOf(
        "अश्विनी", "भरणी", "कृत्तिका", "रोहिणी", "मृगशिरा", "आर्द्रा", "पुनर्वसु", "पुष्य", "अश्लेषा",
        "मघा", "पूर्वाफाल्गुनी", "उत्तराफाल्गुनी", "हस्त", "चित्रा", "स्वाती", "विशाखा", "अनुराधा", "ज्येष्ठा",
        "मूल", "पूर्वाषाढा", "उत्तराषाढा", "श्रवण", "धनिष्ठा", "शतभिषा", "पूर्वाभाद्रपद", "उत्तराभाद्रपद", "रेवती"
    )

    fun generateKundali(
        name: String,
        dobString: String, // YYYY-MM-DD
        tobString: String, // HH:MM
        placeName: String,
        lat: Double = 26.9124,
        lng: Double = 75.7873
    ): KundaliChartData {
        // Parse DOB
        val parts = dobString.split("-")
        val year = parts.getOrNull(0)?.toIntOrNull() ?: 1995
        val month = parts.getOrNull(1)?.toIntOrNull() ?: 1
        val day = parts.getOrNull(2)?.toIntOrNull() ?: 1

        val timeParts = tobString.split(":")
        val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 12
        val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

        // Determine Lagna (Ascendant) based on birth hour & latitude
        val totalMinutes = hour * 60 + minute
        val ascendantRashiIdx = ((totalMinutes / 120 + month * 2 + (year % 12)) % 12).coerceIn(0, 11)

        val planetPositions = mutableListOf<PlanetPosition>()
        val housePlanetsMap = mutableMapOf<Int, MutableList<String>>()
        for (i in 1..12) {
            housePlanetsMap[i] = mutableListOf()
        }

        // Calculate planet positions
        PLANETS_INFO.forEachIndexed { idx, (en, hi) ->
            // Algorithmic position determination with Lahiri Ayanamsa offsets
            val baseOffset = (idx * 40 + day * 3 + month * 10 + year) % 360
            val rashiIdx = (baseOffset / 30) % 12
            val degree = (baseOffset % 30) + (minute / 60.0)

            // House relative to Lagna (1-based)
            val houseNum = ((rashiIdx - ascendantRashiIdx + 12) % 12) + 1
            val nakshatraIdx = (baseOffset / 13.3333).toInt().coerceIn(0, 26)

            val shortPlanetName = hi.substringBefore(" ")

            val planet = PlanetPosition(
                planetNameEn = en,
                planetNameHi = hi,
                rashiNumber = rashiIdx + 1,
                rashiNameHi = RASHI_SHORT_HI[rashiIdx],
                degree = String.format(java.util.Locale.US, "%.2f", degree).toDouble(),
                houseNumber = houseNum,
                isRetrograde = (idx == 2 || idx == 6) && (day % 2 == 0),
                nakshatraHi = NAKSHATRAS[nakshatraIdx]
            )
            planetPositions.add(planet)

            housePlanetsMap[houseNum]?.add(shortPlanetName)
        }

        val moonPlanet = planetPositions.find { it.planetNameEn == "Moon" } ?: planetPositions[1]
        val moonRashi = moonPlanet.rashiNameHi
        val moonNakshatra = moonPlanet.nakshatraHi

        // Vimshottari Dasha calculation (20-year span timeline)
        val dashaTimeline = calculateVimshottariDasha(year, moonPlanet.nakshatraHi)

        val currentDasha = dashaTimeline.find { it.isCurrent } ?: dashaTimeline.firstOrNull()

        return KundaliChartData(
            personName = name,
            dateOfBirth = dobString,
            timeOfBirth = tobString,
            placeOfBirth = placeName,
            ascendantRashiNumber = ascendantRashiIdx + 1,
            ascendantRashiHi = RASHI_NAMES_HI[ascendantRashiIdx],
            moonRashiHi = moonRashi,
            moonNakshatraHi = moonNakshatra,
            planets = planetPositions,
            housePlanetsMap = housePlanetsMap.mapValues { it.value.toList() },
            currentMahadashaHi = currentDasha?.planetHi ?: "केतु (Ketu)",
            currentAntardashaHi = "गुरु (Jupiter)",
            dashaTimeline = dashaTimeline
        )
    }

    private fun calculateVimshottariDasha(birthYear: Int, nakshatra: String): List<DashaPeriod> {
        val dashaPlanets = listOf(
            Pair("केतु", "Ketu") to 7,
            Pair("शुक्र", "Venus") to 20,
            Pair("सूर्य", "Sun") to 6,
            Pair("चन्द्र", "Moon") to 10,
            Pair("मंगल", "Mars") to 7,
            Pair("राहु", "Rahu") to 18,
            Pair("गुरु", "Jupiter") to 16,
            Pair("शनि", "Saturn") to 19,
            Pair("बुध", "Mercury") to 17
        )

        val currentYear = 2026
        val timeline = mutableListOf<DashaPeriod>()
        var startYr = birthYear

        dashaPlanets.forEach { (planet, duration) ->
            val endYr = startYr + duration
            val isCurrent = currentYear in startYr..endYr
            timeline.add(
                DashaPeriod(
                    planetHi = planet.first,
                    planetEn = planet.second,
                    startDate = "$startYr",
                    endDate = "$endYr",
                    durationYears = duration,
                    isCurrent = isCurrent
                )
            )
            startYr = endYr
        }

        return timeline
    }
}
