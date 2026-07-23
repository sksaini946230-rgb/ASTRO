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

    val RASHI_SHORT_HI = listOf(
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

    val NAKSHATRAS = listOf(
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

        val hourDecimal = hour + minute / 60.0

        // Astronomically accurate Sidereal Ascendant (Lagna) with Lahiri Ayanamsa
        val y = if (month <= 2) year - 1 else year
        val m = if (month <= 2) month + 12 else month
        val aVal = y / 100
        val b = 2 - aVal + aVal / 4
        val jd = kotlin.math.floor(365.25 * (y + 4716)) + kotlin.math.floor(30.6001 * (m + 1)) + day + hourDecimal / 24.0 + b - 1524.5

        // Lahiri Ayanamsa
        val t1900 = (jd - 2415020.0) / 36525.0
        val ayanamsa = 22.460148 + 1.396042 * t1900 + 0.000308 * t1900 * t1900

        // Local Sidereal Time (LST) in degrees
        val gmst = (18.697374558 + 24.06570982441908 * (jd - 2451545.0)) % 24.0
        val gmstDeg = if (gmst < 0) (gmst + 24.0) * 15.0 else gmst * 15.0
        val lstDeg = (gmstDeg + lng) % 360.0
        val lst = if (lstDeg < 0) lstDeg + 360.0 else lstDeg

        // Obliquity of Ecliptic
        val t2000 = (jd - 2451545.0) / 36525.0
        val obliquity = 23.4392911 - (46.8150 * t2000) / 3600.0

        // Ascendant (Lagna) Ecliptic Longitude
        val lstRad = Math.toRadians(lst)
        val epsRad = Math.toRadians(obliquity)
        val latRad = Math.toRadians(lat)

        val num = kotlin.math.cos(lstRad)
        val den = -kotlin.math.sin(lstRad) * kotlin.math.cos(epsRad) - kotlin.math.tan(latRad) * kotlin.math.sin(epsRad)
        var ascendantDeg = Math.toDegrees(kotlin.math.atan2(num, den))
        if (ascendantDeg < 0) ascendantDeg += 360.0

        // Sidereal Ascendant (Lagna)
        var siderealAscendant = (ascendantDeg - ayanamsa) % 360.0
        if (siderealAscendant < 0) siderealAscendant += 360.0

        val ascendantRashiIdx = (siderealAscendant / 30.0).toInt().coerceIn(0, 11)

        val planetPositions = mutableListOf<PlanetPosition>()
        val housePlanetsMap = mutableMapOf<Int, MutableList<String>>()
        for (i in 1..12) {
            housePlanetsMap[i] = mutableListOf()
        }

        // Calculate planet positions
        val planetDegrees = AstroMath.calculatePlanets(year, month, day, hourDecimal)

        PLANETS_INFO.forEach { (en, hi) ->
            val deg = planetDegrees[en] ?: 0.0
            val rashiIdx = (deg / 30.0).toInt().coerceIn(0, 11)
            val degreeInRashi = deg % 30.0

            // House relative to Lagna (1-based)
            val houseNum = ((rashiIdx - ascendantRashiIdx + 12) % 12) + 1
            val nakshatraIdx = (deg / 13.333333).toInt().coerceIn(0, 26)

            val shortPlanetName = hi.substringBefore(" ")

            val planet = PlanetPosition(
                planetNameEn = en,
                planetNameHi = hi,
                rashiNumber = rashiIdx + 1,
                rashiNameHi = RASHI_SHORT_HI[rashiIdx],
                degree = String.format(java.util.Locale.US, "%.2f", degreeInRashi).toDouble(),
                houseNumber = houseNum,
                isRetrograde = false, // Simplified
                nakshatraHi = NAKSHATRAS[nakshatraIdx]
            )
            planetPositions.add(planet)
            housePlanetsMap[houseNum]?.add(shortPlanetName)
        }

        val moonPlanet = planetPositions.find { it.planetNameEn == "Moon" } ?: planetPositions[1]
        val moonRashi = moonPlanet.rashiNameHi
        val moonNakshatra = moonPlanet.nakshatraHi

        // Vimshottari Dasha calculation (20-year span timeline)
        val absoluteMoonDegree = (moonPlanet.rashiNumber - 1) * 30.0 + moonPlanet.degree
        val dashaTimeline = calculateVimshottariDasha(year, absoluteMoonDegree)

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
    private fun calculateVimshottariDasha(birthYear: Int, moonDegree: Double): List<DashaPeriod> {
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

        val nakshatraLength = 360.0 / 27.0 // 13.333333 deg
        val elapsedInNakshatra = moonDegree % nakshatraLength
        val remainingFraction = 1.0 - (elapsedInNakshatra / nakshatraLength)
        
        val nakshatraIdx = (moonDegree / nakshatraLength).toInt().coerceIn(0, 26)
        val startDashaIdx = nakshatraIdx % 9

        val currentYear = 2026
        val timeline = mutableListOf<DashaPeriod>()
        
        // Reorder the dasha planets starting from the calculated index
        val orderedDashas = mutableListOf<Pair<Pair<String, String>, Int>>()
        for (i in 0 until 9) {
            orderedDashas.add(dashaPlanets[(startDashaIdx + i) % 9])
        }

        var currentStartYear = birthYear
        orderedDashas.forEachIndexed { i, (planet, duration) ->
            val actualDuration = if (i == 0) (duration * remainingFraction).toInt().coerceAtLeast(1) else duration
            val endYr = currentStartYear + actualDuration
            val isCurrent = currentYear in currentStartYear..endYr
            
            timeline.add(
                DashaPeriod(
                    planetHi = planet.first,
                    planetEn = planet.second,
                    startDate = "$currentStartYear",
                    endDate = "$endYr",
                    durationYears = actualDuration,
                    isCurrent = isCurrent
                )
            )
            currentStartYear = endYr
        }

        return timeline
    }
}
