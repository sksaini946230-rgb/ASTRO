package com.example.astro

import com.example.data.model.MuhuratItem

object MuhuratCalculator {

    fun getUpcomingMuhurats(): List<MuhuratItem> {
        return listOf(
            MuhuratItem(
                id = "m1",
                categoryHi = "विवाह मुहूर्त (Wedding)",
                categoryEn = "Wedding Muhurat",
                dateString = "28 जुलाई 2026",
                dayOfWeekHi = "मंगलवार",
                startTime = "सुबह 06:15 AM",
                endTime = "दोपहर 01:40 PM",
                tithiHi = "शुक्ल पक्ष त्रयोदशी",
                nakshatraHi = "उत्तराफाल्गुनी",
                qualityHi = "अति शुभ (Best)",
                descriptionHi = "सर्वार्थ सिद्धि योग एवं अमृत सिद्धि योग के साथ उत्तम विवाह लगन।"
            ),
            MuhuratItem(
                id = "m2",
                categoryHi = "गृह प्रवेश (Griha Pravesh)",
                categoryEn = "Housewarming",
                dateString = "02 अगस्त 2026",
                dayOfWeekHi = "रविवार",
                startTime = "सुबह 07:30 AM",
                endTime = "दोपहर 12:10 PM",
                tithiHi = "कृष्ण पक्ष पंचमी",
                nakshatraHi = "रोहिणी",
                qualityHi = "शुभ (Good)",
                descriptionHi = "रोहिणी नक्षत्र एवं स्थिर वृषभ लग्न में नया गृह प्रवेश फलदायी।"
            ),
            MuhuratItem(
                id = "m3",
                categoryHi = "व्यापार शुभारम्भ (Business Launch)",
                categoryEn = "Business Launch",
                dateString = "05 अगस्त 2026",
                dayOfWeekHi = "बुधवार",
                startTime = "सुबह 09:15 AM",
                endTime = "सुबह 11:45 AM",
                tithiHi = "शुक्ल पक्ष द्वितीया",
                nakshatraHi = "हस्त",
                qualityHi = "अति शुभ (Best)",
                descriptionHi = "लाभ चौघड़िया में दुकान, शोरूम अथवा नए व्यापार की शुरुआत।"
            ),
            MuhuratItem(
                id = "m4",
                categoryHi = "वाहन खरीद (Vehicle Purchase)",
                categoryEn = "Vehicle Purchase",
                dateString = "10 अगस्त 2026",
                dayOfWeekHi = "सोमवार",
                startTime = "दोपहर 02:00 PM",
                endTime = "सायं 06:30 PM",
                tithiHi = "शुक्ल पक्ष सप्तमी",
                nakshatraHi = "चित्रा",
                qualityHi = "शुभ (Good)",
                descriptionHi = "अमृत चौघड़िया में नई कार, बाइक अथवा व्यावसायिक वाहन क्रय मुहूर्त।"
            ),
            MuhuratItem(
                id = "m5",
                categoryHi = "शुभ यात्रा (Auspicious Travel)",
                categoryEn = "Travel Muhurat",
                dateString = "14 अगस्त 2026",
                dayOfWeekHi = "शुक्रवार",
                startTime = "सुबह 06:00 AM",
                endTime = "सुबह 10:30 AM",
                tithiHi = "कृष्ण पक्ष दशमी",
                nakshatraHi = "स्वाती",
                qualityHi = "शुभ (Good)",
                descriptionHi = "उत्तर एवं पूर्व दिशा की यात्रा हेतु शुभ समय।"
            )
        )
    }
}
