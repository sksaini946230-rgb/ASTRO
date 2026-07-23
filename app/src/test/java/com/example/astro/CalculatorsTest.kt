package com.example.astro

import com.example.data.model.CityLocation
import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs

class CalculatorsTest {

    @Test
    fun testNumerologyCalculator() {
        val result = NumerologyCalculator.calculateNumerology("Rahul", "1995-05-20")
        assertNotNull(result)
        // Check if Psychic number and Destiny number are calculated correctly
        // DOB = 20 -> 2+0 = 2 (Psychic / Moolank)
        // 20+05+1995 -> 2+0+0+5+1+9+9+5 = 31 -> 3+1 = 4 (Destiny / Bhagyank)
        assertEquals(2, result.moolank)
        assertEquals(4, result.bhagyank)
    }

    @Test
    fun testKundaliCalculatorAccuracy() {
        // Generate a Kundali for a standard date, time, and location
        val name = "Astro Tester"
        val dob = "1995-05-20"
        val tob = "08:15"
        val place = "Jaipur"
        val lat = 26.9124
        val lng = 75.7873

        val kundali = KundaliCalculator.generateKundali(name, dob, tob, place, lat, lng)
        
        assertNotNull(kundali)
        assertEquals(name, kundali.personName)
        assertEquals(dob, kundali.dateOfBirth)
        assertEquals(tob, kundali.timeOfBirth)
        
        // Establish core Vedic astrological constants check:
        // 1. There must be exactly 9 astrological bodies/planets
        assertEquals(9, kundali.planets.size)

        // 2. All planet degrees must be in the range [0.0, 30.0) degrees within their Rashi
        for (planet in kundali.planets) {
            assertTrue(
                "Planet ${planet.planetNameEn} degree ${planet.degree} must be within [0.0, 30.0)",
                planet.degree in 0.0..30.0
            )
            assertTrue(
                "Planet ${planet.planetNameEn} rashiNumber ${planet.rashiNumber} must be in range [1, 12]",
                planet.rashiNumber in 1..12
            )
            assertTrue(
                "Planet ${planet.planetNameEn} houseNumber ${planet.houseNumber} must be in range [1, 12]",
                planet.houseNumber in 1..12
            )
        }

        // 3. Verify the fundamental Vedic astronomical constant:
        // Rahu and Ketu are opposite shadow nodes, hence they are exactly 180 degrees apart.
        val rahu = kundali.planets.first { it.planetNameEn == "Rahu" }
        val ketu = kundali.planets.first { it.planetNameEn == "Ketu" }
        
        val rahuAbsLon = (rahu.rashiNumber - 1) * 30.0 + rahu.degree
        val ketuAbsLon = (ketu.rashiNumber - 1) * 30.0 + ketu.degree
        
        val longitudeDiff = abs(rahuAbsLon - ketuAbsLon)
        val modDiff = (longitudeDiff % 360.0 + 360.0) % 360.0
        
        // Assert that Rahu and Ketu are exactly 180 degrees apart in longitude
        assertEquals(180.0, modDiff, 0.01)
    }

    @Test
    fun testPanchangCalculatorAccuracy() {
        // Date: 2026-07-22 (July 22, 2026)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val testDate = sdf.parse("2026-07-22")!!
        val delhi = CityLocation("New Delhi", "नई दिल्ली", "Delhi", 28.6139, 77.2090)

        val panchang = PanchangCalculator.calculatePanchang(testDate, delhi)
        assertNotNull(panchang)

        // 1. Verify Vikram Samvat (Vedic constant offset: ~57 years from Gregorian)
        // For July 2026, Vikram Samvat is 2083
        assertEquals(2083, panchang.vikramSamvat)

        // 2. Verify Saka Samvat (Vedic constant offset: ~78 years from Gregorian)
        // For July 2026, Saka Samvat is 1948
        assertEquals(1948, panchang.sakaSamvat)

        // 3. Verify core Panchang strings are populated and non-empty
        assertFalse(panchang.tithiHindi.isEmpty())
        assertFalse(panchang.nakshatraHindi.isEmpty())
        assertFalse(panchang.yogaHindi.isEmpty())
        assertFalse(panchang.karanHindi.isEmpty())
        assertFalse(panchang.dayOfWeekHindi.isEmpty())

        // 4. Verify Sun/Moon rise and set times are computed
        assertTrue(panchang.sunrise.contains(":"))
        assertTrue(panchang.sunset.contains(":"))
        assertTrue(panchang.moonrise.contains(":"))
        assertTrue(panchang.moonset.contains(":"))
    }

    @Test
    fun testKundaliMatchingCalculatorGunaScores() {
        val boyName = "Rahul"
        val boyDob = "1995-05-20"
        val boyTob = "08:15"
        
        val girlName = "Anjali"
        val girlDob = "1997-11-12"
        val girlTob = "14:30"

        val matchingResult = KundaliMatchingCalculator.matchKundali(
            boyName, boyDob, boyTob,
            girlName, girlDob, girlTob
        )

        assertNotNull(matchingResult)
        assertEquals(boyName, matchingResult.boyName)
        assertEquals(girlName, matchingResult.girlName)

        // 1. Verify that total obtained Guna score is between 0 and 36 (Vedic Constant limits)
        assertTrue(
            "Obtained Guna score ${matchingResult.totalObtainedGuna} must be between 0.0 and 36.0",
            matchingResult.totalObtainedGuna in 0.0..36.0
        )

        // 2. Verify Ashtakoota max points constant: sum of all Koota weights must equal 36.0
        val sumOfMaxPoints = matchingResult.kootDetails.sumOf { it.maxPoints }
        assertEquals(36.0, sumOfMaxPoints, 0.001)

        // 3. Verify individual Koota weight constants:
        // Varna (1), Vashya (2), Tara (3), Yoni (4), Graha Maitri (5), Gana (6), Bhakoot (7), Nadi (8)
        val varna = matchingResult.kootDetails.first { it.kootNameEn == "Varna" }
        val vashya = matchingResult.kootDetails.first { it.kootNameEn == "Vashya" }
        val tara = matchingResult.kootDetails.first { it.kootNameEn == "Tara" }
        val yoni = matchingResult.kootDetails.first { it.kootNameEn == "Yoni" }
        val grahaMaitri = matchingResult.kootDetails.first { it.kootNameEn == "Graha Maitri" }
        val gana = matchingResult.kootDetails.first { it.kootNameEn == "Gana" }
        val bhakoot = matchingResult.kootDetails.first { it.kootNameEn == "Bhakoot" }
        val nadi = matchingResult.kootDetails.first { it.kootNameEn == "Nadi" }

        assertEquals(1.0, varna.maxPoints, 0.01)
        assertEquals(2.0, vashya.maxPoints, 0.01)
        assertEquals(3.0, tara.maxPoints, 0.01)
        assertEquals(4.0, yoni.maxPoints, 0.01)
        assertEquals(5.0, grahaMaitri.maxPoints, 0.01)
        assertEquals(6.0, gana.maxPoints, 0.01)
        assertEquals(7.0, bhakoot.maxPoints, 0.01)
        assertEquals(8.0, nadi.maxPoints, 0.01)

        // 4. Verify Manglik status calculation logic matches actual planetary placements (Mars in house 1, 4, 7, 8, 12)
        val boyChart = KundaliCalculator.generateKundali(boyName, boyDob, boyTob, "Default")
        val girlChart = KundaliCalculator.generateKundali(girlName, girlDob, girlTob, "Default")

        val boyMarsHouse = boyChart.planets.find { it.planetNameEn == "Mars" }?.houseNumber ?: 0
        val girlMarsHouse = girlChart.planets.find { it.planetNameEn == "Mars" }?.houseNumber ?: 0

        val manglikHouses = listOf(1, 4, 7, 8, 12)
        val isBoyManglikExpected = boyMarsHouse in manglikHouses
        val isGirlManglikExpected = girlMarsHouse in manglikHouses

        assertEquals(isBoyManglikExpected, matchingResult.isManglikBoy)
        assertEquals(isGirlManglikExpected, matchingResult.isManglikGirl)
    }
}
