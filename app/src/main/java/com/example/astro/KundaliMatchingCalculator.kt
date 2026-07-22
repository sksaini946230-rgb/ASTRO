package com.example.astro

import com.example.data.model.GunaKootDetail
import com.example.data.model.GunaMatchingResult
import kotlin.random.Random

object KundaliMatchingCalculator {

    fun matchKundali(
        boyName: String,
        boyDob: String,
        boyTob: String,
        girlName: String,
        girlDob: String,
        girlTob: String
    ): GunaMatchingResult {
        // Calculate deterministic seed based on names and DOBS
        val seed = (boyName.hashCode() + girlName.hashCode() + boyDob.hashCode() + girlDob.hashCode()).toLong()
        val rnd = Random(seed)

        // 8 Koota Max Points: Varna 1, Vashya 2, Tara 3, Yoni 4, Graha Maitri 5, Gana 6, Bhakoot 7, Nadi 8 = 36 Total
        val varna = rnd.nextDouble(0.5, 1.0).let { Math.round(it * 10) / 10.0 }
        val vashya = rnd.nextDouble(1.0, 2.0).let { Math.round(it * 10) / 10.0 }
        val tara = rnd.nextDouble(1.5, 3.0).let { Math.round(it * 10) / 10.0 }
        val yoni = rnd.nextDouble(2.0, 4.0).let { Math.round(it * 10) / 10.0 }
        val grahaMaitri = rnd.nextDouble(3.0, 5.0).let { Math.round(it * 10) / 10.0 }
        val gana = rnd.nextDouble(4.0, 6.0).let { Math.round(it * 10) / 10.0 }
        val bhakoot = rnd.nextDouble(4.0, 7.0).let { Math.round(it * 10) / 10.0 }
        val nadi = rnd.nextDouble(4.0, 8.0).let { Math.round(it * 10) / 10.0 }

        val totalGuna = (varna + vashya + tara + yoni + grahaMaitri + gana + bhakoot + nadi).let {
            Math.round(it * 10) / 10.0
        }

        val isBoyManglik = (boyName.length % 3 == 0)
        val isGirlManglik = (girlName.length % 2 == 0)

        val mangalStatus = when {
            isBoyManglik && isGirlManglik -> "दोनों मांगलिक हैं (मंगल दोष निरस्त/मांगलिक सामंजस्य)"
            isBoyManglik -> "वर मांगलिक हैं, कन्या मांगलिक नहीं हैं"
            isGirlManglik -> "कन्या मांगलिक हैं, वर मांगलिक नहीं हैं"
            else -> "दोनों अंश-मांगलिक नहीं हैं (कोई मंगल दोष नहीं)"
        }

        val kootDetails = listOf(
            GunaKootDetail("वर्ण (Varna)", "Varna", 1.0, varna, "आध्यात्मिक एवं मानसिक दृष्टिकोण का मिलान।"),
            GunaKootDetail("वश्य (Vashya)", "Vashya", 2.0, vashya, "पारस्परिक आकर्षण एवं अधिकार क्षेत्र।"),
            GunaKootDetail("तारा (Tara)", "Tara", 3.0, tara, "भाग्य, दीर्घायु एवं स्वास्थ्य अनुकूलता।"),
            GunaKootDetail("योनि (Yoni)", "Yoni", 4.0, yoni, "शारीरिक एवं दाम्पत्य सामंजस्य।"),
            GunaKootDetail("ग्रह मैत्री (Graha Maitri)", "Graha Maitri", 5.0, grahaMaitri, "मानसिक विचार एवं बौद्धिक मित्रता।"),
            GunaKootDetail("गण (Gana)", "Gana", 6.0, gana, "स्वभाव, व्यवहार एवं चरित्र सामंजस्य।"),
            GunaKootDetail("भकूट (Bhakoot)", "Bhakoot", 7.0, bhakoot, "पारिवारिक समृद्धि एवं वंश वृद्धि।"),
            GunaKootDetail("नाडी (Nadi)", "Nadi", 8.0, nadi, "आनुवंशिक स्वास्थ्य एवं संतान सुख।")
        )

        val (verdictHi, verdictEn, summaryHi) = when {
            totalGuna >= 28.0 -> Triple(
                "अति उत्तम मिलान (Excellent Match)",
                "Excellent Match",
                "$boyName एवं $girlName की कुंडली में $totalGuna / 36 गुण प्राप्त हुए हैं। यह विवाह अत्यंत शुभ एवं सुखद वैवाहिक जीवन का संकेत देता है।"
            )
            totalGuna >= 18.0 -> Triple(
                "शुभ एवं अनुकूल मिलान (Good Match)",
                "Good Match",
                "$boyName एवं $girlName की कुंडली में $totalGuna / 36 गुण मिल रहे हैं। सामान्य पूजा-अनुष्ठान के उपरांत विवाह सम्पन्न किया जा सकता है।"
            )
            else -> Triple(
                "औसत मिलान (Average Match - Remedy Required)",
                "Average Match",
                "$boyName एवं $girlName की कुंडली में $totalGuna / 36 गुण प्राप्त हुए हैं। विवाह पूर्व नाडी अथवा भकूट दोष निवारण उपाय परामर्श योग्य हैं।"
            )
        }

        return GunaMatchingResult(
            boyName = boyName,
            girlName = girlName,
            totalObtainedGuna = totalGuna,
            maxGuna = 36.0,
            isManglikBoy = isBoyManglik,
            isManglikGirl = isGirlManglik,
            mangalDoshaStatusHi = mangalStatus,
            kootDetails = kootDetails,
            compatibilityVerdictHi = verdictHi,
            compatibilityVerdictEn = verdictEn,
            summaryReadingHi = summaryHi
        )
    }
}
