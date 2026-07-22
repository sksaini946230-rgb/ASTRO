package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.astro.ChoghadiyaCalculator
import com.example.astro.FestivalProvider
import com.example.astro.KundaliCalculator
import com.example.astro.KundaliMatchingCalculator
import com.example.astro.MuhuratCalculator
import com.example.astro.NumerologyCalculator
import com.example.astro.PanchangCalculator
import com.example.astro.RashifalProvider
import com.example.data.ai.GeminiAstroService
import com.example.data.local.AppDatabase
import com.example.data.local.AstroCacheRepository
import com.example.data.local.KundaliEntity
import com.example.data.local.KundaliRepository
import com.example.data.model.ChoghadiyaSlot
import com.example.data.model.CityLocation
import com.example.data.model.FestivalData
import com.example.data.model.GunaMatchingResult
import com.example.data.model.KundaliChartData
import com.example.data.model.MuhuratItem
import com.example.data.model.NumerologyData
import com.example.data.model.PanchangData
import com.example.data.model.RashifalData
import com.example.util.LanguageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

enum class AppTab {
    PANCHANG,
    CALENDAR,
    RASHIFAL,
    KUNDALI,
    MUHURAT,
    MATCHING,
    NUMEROLOGY_AI,
    SAVED_PROFILES,
    SETTINGS
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: KundaliRepository
    private val cacheRepository: AstroCacheRepository

    // Onboarding State
    private val _isOnboardingCompleted = MutableStateFlow(true)
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    fun completeOnboarding() {
        _isOnboardingCompleted.value = true
    }

    fun resetOnboarding() {
        _isOnboardingCompleted.value = false
    }

    // Active Tab
    private val _selectedTab = MutableStateFlow(AppTab.PANCHANG)
    val selectedTab: StateFlow<AppTab> = _selectedTab.asStateFlow()

    fun selectTab(tab: AppTab) {
        _selectedTab.value = tab
    }

    // Language Toggle
    fun toggleLanguage() {
        LanguageManager.toggleLanguage()
    }

    // Selected City Location for Panchang
    private val _selectedCity = MutableStateFlow(PanchangCalculator.popularCities[0]) // Default Jaipur, Rajasthan
    val selectedCity: StateFlow<CityLocation> = _selectedCity.asStateFlow()

    fun setCity(city: CityLocation) {
        _selectedCity.value = city
        recalculatePanchang()
    }

    // Notification Toggles
    val dailyRahuKaalAlert = MutableStateFlow(true)
    val festivalRemindersAlert = MutableStateFlow(true)

    fun toggleRahuKaalAlert() {
        dailyRahuKaalAlert.value = !dailyRahuKaalAlert.value
    }

    fun toggleFestivalAlert() {
        festivalRemindersAlert.value = !festivalRemindersAlert.value
    }

    // Selected Date
    private val _selectedDate = MutableStateFlow(Date())
    val selectedDate: StateFlow<Date> = _selectedDate.asStateFlow()

    fun setDate(date: Date) {
        _selectedDate.value = date
        recalculatePanchang()
    }

    // Panchang Data State
    private val _panchangState = MutableStateFlow(PanchangCalculator.calculatePanchang(Date(), PanchangCalculator.popularCities[0]))
    val panchangState: StateFlow<PanchangData> = _panchangState.asStateFlow()

    private fun recalculatePanchang() {
        viewModelScope.launch {
            _panchangState.value = cacheRepository.getPanchangWith7DayCache(_selectedDate.value, _selectedCity.value)
        }
    }

    // Festivals
    val festivals: List<FestivalData> = FestivalProvider.getFestivals()

    // Rashifal
    private val _dailyHoroscopes = MutableStateFlow(RashifalProvider.getDailyHoroscope())
    val dailyHoroscopesState: StateFlow<List<RashifalData>> = _dailyHoroscopes.asStateFlow()
    val dailyHoroscopes: List<RashifalData> get() = _dailyHoroscopes.value

    private fun loadHoroscopesWithCache() {
        viewModelScope.launch {
            _dailyHoroscopes.value = cacheRepository.getHoroscopesWith7DayCache()
        }
    }

    private val _selectedRashiId = MutableStateFlow(1) // Mesh
    val selectedRashiId: StateFlow<Int> = _selectedRashiId.asStateFlow()

    fun selectRashi(id: Int) {
        _selectedRashiId.value = id
    }

    // Choghadiya
    private val _choghadiyaDaytime = MutableStateFlow(true)
    val choghadiyaDaytime: StateFlow<Boolean> = _choghadiyaDaytime.asStateFlow()

    val choghadiyaSlots: List<ChoghadiyaSlot>
        get() = ChoghadiyaCalculator.getChoghadiyaSlots(
            _selectedDate.value,
            _choghadiyaDaytime.value,
            _selectedCity.value.latitude,
            _selectedCity.value.longitude
        )

    fun toggleChoghadiyaDayNight(isDay: Boolean) {
        _choghadiyaDaytime.value = isDay
    }

    // Muhurats
    val upcomingMuhurats: List<MuhuratItem> = MuhuratCalculator.getUpcomingMuhurats()

    // Kundali Generator Input State
    var kundaliName = MutableStateFlow("Rahul Saini")
    var kundaliDob = MutableStateFlow("1996-08-15")
    var kundaliTob = MutableStateFlow("10:30")
    var kundaliPlace = MutableStateFlow("Jaipur, Rajasthan")

    private val _generatedKundali = MutableStateFlow(
        KundaliCalculator.generateKundali("Rahul Saini", "1996-08-15", "10:30", "Jaipur, Rajasthan")
    )
    val generatedKundali: StateFlow<KundaliChartData> = _generatedKundali.asStateFlow()

    fun generateKundaliChart(name: String, dob: String, tob: String, place: String) {
        kundaliName.value = name
        kundaliDob.value = dob
        kundaliTob.value = tob
        kundaliPlace.value = place
        _generatedKundali.value = KundaliCalculator.generateKundali(name, dob, tob, place)
    }

    // Kundali Matching / Guna Milan State
    var matchBoyName = MutableStateFlow("Rahul")
    var matchBoyDob = MutableStateFlow("1995-05-20")
    var matchBoyTob = MutableStateFlow("08:15")

    var matchGirlName = MutableStateFlow("Priya")
    var matchGirlDob = MutableStateFlow("1997-11-12")
    var matchGirlTob = MutableStateFlow("14:30")

    private val _gunaResult = MutableStateFlow(
        KundaliMatchingCalculator.matchKundali("Rahul", "1995-05-20", "08:15", "Priya", "1997-11-12", "14:30")
    )
    val gunaResult: StateFlow<GunaMatchingResult> = _gunaResult.asStateFlow()

    fun calculateGunaMatching() {
        _gunaResult.value = KundaliMatchingCalculator.matchKundali(
            matchBoyName.value, matchBoyDob.value, matchBoyTob.value,
            matchGirlName.value, matchGirlDob.value, matchGirlTob.value
        )
    }

    // Numerology
    var numName = MutableStateFlow("Sunil Saini")
    var numDob = MutableStateFlow("1995-07-22")

    private val _numerologyData = MutableStateFlow(
        NumerologyCalculator.calculateNumerology("Sunil Saini", "1995-07-22")
    )
    val numerologyData: StateFlow<NumerologyData> = _numerologyData.asStateFlow()

    fun calculateNumerology() {
        _numerologyData.value = NumerologyCalculator.calculateNumerology(numName.value, numDob.value)
    }

    // AI Astrologer Chat
    private val _aiResponse = MutableStateFlow("")
    val aiResponse: StateFlow<String> = _aiResponse.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    fun askAiAstrologer(question: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val kundaliDetails = "${_generatedKundali.value.personName}, DOB: ${_generatedKundali.value.dateOfBirth}, Lagna: ${_generatedKundali.value.ascendantRashiHi}"
            val res = GeminiAstroService.getAiAstrologyInsight(question, kundaliDetails)
            _aiResponse.value = res
            _isAiLoading.value = false
        }
    }

    // Astro & Astronomical News Grounded via Google Search & Gemini API
    private val _astroNews = MutableStateFlow("")
    val astroNews: StateFlow<String> = _astroNews.asStateFlow()

    private val _isNewsLoading = MutableStateFlow(false)
    val isNewsLoading: StateFlow<Boolean> = _isNewsLoading.asStateFlow()

    fun fetchAstroNews() {
        viewModelScope.launch {
            _isNewsLoading.value = true
            val news = GeminiAstroService.fetchAstroNewsWithSearchGrounding()
            _astroNews.value = news
            _isNewsLoading.value = false
        }
    }

    // Room DB Profiles
    private val _savedProfiles = MutableStateFlow<List<KundaliEntity>>(emptyList())
    val savedProfiles: StateFlow<List<KundaliEntity>> = _savedProfiles.asStateFlow()

    private fun loadSavedProfiles() {
        viewModelScope.launch {
            repository.allProfiles.collect { list ->
                _savedProfiles.value = list
            }
        }
    }

    fun saveCurrentKundaliProfile() {
        viewModelScope.launch {
            val entity = KundaliEntity(
                name = kundaliName.value,
                gender = "MALE",
                dateOfBirth = kundaliDob.value,
                timeOfBirth = kundaliTob.value,
                placeOfBirth = kundaliPlace.value,
                latitude = _selectedCity.value.latitude,
                longitude = _selectedCity.value.longitude,
                notes = "Saved from AstroVeda Kundali Generator"
            )
            repository.saveProfile(entity)
        }
    }

    fun deleteProfile(entity: KundaliEntity) {
        viewModelScope.launch {
            repository.deleteProfile(entity)
        }
    }

    // Premium Dialog state
    var showPremiumDialog = MutableStateFlow(false)

    init {
        val db = AppDatabase.getDatabase(application)
        repository = KundaliRepository(db.kundaliDao())
        cacheRepository = AstroCacheRepository(db.panchangCacheDao(), db.horoscopeCacheDao())
        loadSavedProfiles()
        recalculatePanchang()
        loadHoroscopesWithCache()
        fetchAstroNews()
    }
}
