package com.example.astro

import com.example.data.model.GunaKootDetail
import com.example.data.model.GunaMatchingResult

object KundaliMatchingCalculator {

    fun matchKundali(
        boyName: String,
        boyDob: String,
        boyTob: String,
        girlName: String,
        girlDob: String,
        girlTob: String
    ): GunaMatchingResult {
        // Generate actual charts to get planetary positions
        val boyChart = KundaliCalculator.generateKundali(boyName, boyDob, boyTob, "Default")
        val girlChart = KundaliCalculator.generateKundali(girlName, girlDob, girlTob, "Default")

        val boyMoonRashiIdx = boyChart.moonRashiHi.let { KundaliCalculator.RASHI_SHORT_HI.indexOf(it) }.coerceAtLeast(0)
        val girlMoonRashiIdx = girlChart.moonRashiHi.let { KundaliCalculator.RASHI_SHORT_HI.indexOf(it) }.coerceAtLeast(0)
        
        val boyNakshatraIdx = boyChart.moonNakshatraHi.let { KundaliCalculator.NAKSHATRAS.indexOf(it) }.coerceAtLeast(0)
        val girlNakshatraIdx = girlChart.moonNakshatraHi.let { KundaliCalculator.NAKSHATRAS.indexOf(it) }.coerceAtLeast(0)

        // 1. Varna (1 pt)
        val varnaPoints = calculateVarna(boyMoonRashiIdx, girlMoonRashiIdx)
        // 2. Vashya (2 pts)
        val vashyaPoints = calculateVashya(boyMoonRashiIdx, girlMoonRashiIdx)
        // 3. Tara (3 pts)
        val taraPoints = calculateTara(boyNakshatraIdx, girlNakshatraIdx)
        // 4. Yoni (4 pts)
        val yoniPoints = calculateYoni(boyNakshatraIdx, girlNakshatraIdx)
        // 5. Graha Maitri (5 pts)
        val grahaMaitriPoints = calculateGrahaMaitri(boyMoonRashiIdx, girlMoonRashiIdx)
        // 6. Gana (6 pts)
        val ganaPoints = calculateGana(boyNakshatraIdx, girlNakshatraIdx)
        // 7. Bhakoot (7 pts)
        val bhakootPoints = calculateBhakoot(boyMoonRashiIdx, girlMoonRashiIdx)
        // 8. Nadi (8 pts)
        val nadiPoints = calculateNadi(boyNakshatraIdx, girlNakshatraIdx)

        val totalGuna = varnaPoints + vashyaPoints + taraPoints + yoniPoints + grahaMaitriPoints + ganaPoints + bhakootPoints + nadiPoints

        // Manglik Dosha
        val boyMarsHouse = boyChart.planets.find { it.planetNameEn == "Mars" }?.houseNumber ?: 0
        val girlMarsHouse = girlChart.planets.find { it.planetNameEn == "Mars" }?.houseNumber ?: 0
        val manglikHouses = listOf(1, 4, 7, 8, 12)
        val isBoyManglik = boyMarsHouse in manglikHouses
        val isGirlManglik = girlMarsHouse in manglikHouses

        val mangalStatus = when {
            isBoyManglik && isGirlManglik -> "दोनों मांगलिक हैं (मंगल दोष निरस्त/मांगलिक सामंजस्य)"
            isBoyManglik -> "वर मांगलिक हैं, कन्या मांगलिक नहीं हैं"
            isGirlManglik -> "कन्या मांगलिक हैं, वर मांगलिक नहीं हैं"
            else -> "दोनों अंश-मांगलिक नहीं हैं (कोई मंगल दोष नहीं)"
        }

        val mangalStatusEn = when {
            isBoyManglik && isGirlManglik -> "Both are Manglik (Mangal Dosha canceled / Manglik compatibility)"
            isBoyManglik -> "Boy is Manglik, Girl is not Manglik"
            isGirlManglik -> "Girl is Manglik, Boy is not Manglik"
            else -> "Both are non-Manglik (No Mangal Dosha)"
        }

        val kootDetails = listOf(
            GunaKootDetail("वर्ण (Varna)", "Varna", 1.0, varnaPoints, "आध्यात्मिक एवं मानसिक दृष्टिकोण का मिलान।"),
            GunaKootDetail("वश्य (Vashya)", "Vashya", 2.0, vashyaPoints, "पारस्परिक आकर्षण एवं अधिकार क्षेत्र।"),
            GunaKootDetail("तारा (Tara)", "Tara", 3.0, taraPoints, "भाग्य, दीर्घायु एवं स्वास्थ्य अनुकूलता।"),
            GunaKootDetail("योनि (Yoni)", "Yoni", 4.0, yoniPoints, "शारीरिक एवं दाम्पत्य सामंजस्य।"),
            GunaKootDetail("ग्रह मैत्री (Graha Maitri)", "Graha Maitri", 5.0, grahaMaitriPoints, "मानसिक विचार एवं बौद्धिक मित्रता।"),
            GunaKootDetail("गण (Gana)", "Gana", 6.0, ganaPoints, "स्वभाव, व्यवहार एवं चरित्र सामंजस्य।"),
            GunaKootDetail("भकूट (Bhakoot)", "Bhakoot", 7.0, bhakootPoints, "पारिवारिक समृद्धि एवं वंश वृद्धि।"),
            GunaKootDetail("नाडी (Nadi)", "Nadi", 8.0, nadiPoints, "आनुवंशिक स्वास्थ्य एवं संतान सुख।")
        )

        val verdictHi: String
        val verdictEn: String
        val summaryHi: String
        val summaryEn: String

        when {
            totalGuna >= 28.0 -> {
                verdictHi = "अति उत्तम मिलान (Excellent Match)"
                verdictEn = "Excellent Match"
                summaryHi = "$boyName एवं $girlName की कुंडली में $totalGuna / 36 गुण प्राप्त हुए हैं। यह विवाह अत्यंत शुभ एवं सुखद वैवाहिक जीवन का संकेत देता है।"
                summaryEn = "Guna score of $totalGuna / 36 obtained between $boyName and $girlName. This indicates an exceptionally auspicious and happy married life."
            }
            totalGuna >= 18.0 -> {
                verdictHi = "शुभ एवं अनुकूल मिलान (Good Match)"
                verdictEn = "Good Match"
                summaryHi = "$boyName एवं $girlName की कुंडली में $totalGuna / 36 गुण मिल रहे हैं। सामान्य पूजा-अनुष्ठान के उपरांत विवाह सम्पन्न किया जा सकता है।"
                summaryEn = "$totalGuna / 36 gunas are matching. The wedding can be safely performed after simple standard rituals."
            }
            else -> {
                verdictHi = "औसत मिलान (Average Match - Remedy Required)"
                verdictEn = "Average Match"
                summaryHi = "$boyName एवं $girlName की कुंडली में $totalGuna / 36 गुण प्राप्त हुए हैं। विवाह पूर्व नाडी अथवा भकूट दोष निवारण उपाय परामर्श योग्य हैं।"
                summaryEn = "Only $totalGuna / 36 gunas match. Prior remedies/prayers for Nadi or Bhakoot Dosha are strongly recommended before marriage."
            }
        }

        return GunaMatchingResult(
            boyName = boyName,
            girlName = girlName,
            totalObtainedGuna = totalGuna,
            maxGuna = 36.0,
            isManglikBoy = isBoyManglik,
            isManglikGirl = isGirlManglik,
            mangalDoshaStatusHi = mangalStatus,
            mangalDoshaStatusEn = mangalStatusEn,
            kootDetails = kootDetails,
            compatibilityVerdictHi = verdictHi,
            compatibilityVerdictEn = verdictEn,
            summaryReadingHi = summaryHi,
            summaryReadingEn = summaryEn
        )
    }

    private fun calculateVarna(b: Int, g: Int): Double {
        val v = listOf(3, 2, 1, 0, 3, 2, 1, 0, 3, 2, 1, 0)
        return if (v[b] >= v[g]) 1.0 else 0.0
    }
    private fun calculateVashya(b: Int, g: Int): Double = if (b == g) 2.0 else 1.0
    private fun calculateTara(b: Int, g: Int): Double {
        val tbg = (g - b + 27) % 27
        val tgb = (b - g + 27) % 27
        val r1 = (tbg % 9)
        val r2 = (tgb % 9)
        val score1 = if (r1 in listOf(3, 5, 7)) 0.0 else 1.5
        val score2 = if (r2 in listOf(3, 5, 7)) 0.0 else 1.5
        return score1 + score2
    }
    private fun calculateYoni(b: Int, g: Int): Double = if (b % 4 == g % 4) 4.0 else 2.0
    private fun calculateGrahaMaitri(b: Int, g: Int): Double = if (b % 3 == g % 3) 5.0 else 3.0
    private fun calculateGana(b: Int, g: Int): Double {
        val bGana = b % 3
        val gGana = g % 3
        return if (bGana == gGana) 6.0 else if (bGana == 0 || gGana == 0) 4.0 else 0.0
    }
    private fun calculateBhakoot(b: Int, g: Int): Double {
        val dist = (g - b + 12) % 12
        return if (dist in listOf(1, 5, 7)) 0.0 else 7.0
    }
    private fun calculateNadi(b: Int, g: Int): Double {
        val bNadi = b % 3
        val gNadi = g % 3
        return if (bNadi != gNadi) 8.0 else 0.0
    }
}
