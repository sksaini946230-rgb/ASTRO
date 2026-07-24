package com.example.ui

import kotlinx.coroutines.Dispatchers
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
import com.example.data.local.DatabaseProvider
import com.example.data.local.KundaliEntity
import com.example.data.local.KundaliRepository
import com.example.data.local.RecentSearchEntity
import com.example.data.local.RecentSearchRepository
import com.example.data.local.SavedReportEntity
import com.example.data.local.SavedReportRepository
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
import com.example.service.BillingManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.Manifest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

enum class AppTab {
    PANCHANG,
    RASHIFAL,
    KUNDALI,
    MUHURAT,
    MORE
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: KundaliRepository
    private val reportRepository: SavedReportRepository
    private val recentSearchRepository: RecentSearchRepository
    private val cacheRepository: AstroCacheRepository
    private val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    private val _isUsingCache = MutableStateFlow(false)
    val isUsingCache: StateFlow<Boolean> = _isUsingCache.asStateFlow()

    private fun monitorNetwork() {
        val networkRequest = android.net.NetworkRequest.Builder()
            .addCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        connectivityManager.registerNetworkCallback(networkRequest, object : android.net.ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                _isOffline.value = false
            }
            override fun onLost(network: android.net.Network) {
                _isOffline.value = true
            }
        })
        
        // Initial check
        val activeNetwork = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(activeNetwork)
        _isOffline.value = caps == null || !caps.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    private val sharedPrefs = application.getSharedPreferences("astroveda_prefs", Context.MODE_PRIVATE)

    private val _isProUser = MutableStateFlow(sharedPrefs.getBoolean("is_pro", false))
    val isProUser: StateFlow<Boolean> = _isProUser.asStateFlow()

    fun setProUser(isPro: Boolean) {
        _isProUser.value = isPro
        sharedPrefs.edit().putBoolean("is_pro", isPro).apply()
    }

    // Google Play Billing Client Wrapper
    private val billingManager = BillingManager(application) { isUnlocked ->
        setProUser(isUnlocked)
    }

    val isBillingReady = billingManager.isReady
    val billingErrorMessage = billingManager.errorMessage
    val subscriptionProductDetails = billingManager.productDetails

    fun makePurchase(activity: android.app.Activity) {
        billingManager.launchPurchaseFlow(activity)
    }

    // Interstitial ad trigger
    private val _showInterstitialTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val showInterstitialTrigger: SharedFlow<Unit> = _showInterstitialTrigger.asSharedFlow()

    fun triggerInterstitial() {
        _showInterstitialTrigger.tryEmit(Unit)
    }

    // Global Error State for ErrorBoundary integration
    private val _globalError = MutableStateFlow<Throwable?>(null)
    val globalError: StateFlow<Throwable?> = _globalError.asStateFlow()

    fun reportError(t: Throwable) {
        _globalError.value = t
    }

    fun clearGlobalError() {
        _globalError.value = null
    }

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

    // Sub-tab states
    private val _panchangSubTab = MutableStateFlow(0) // 0: Daily Panchang, 1: Monthly Calendar
    val panchangSubTab: StateFlow<Int> = _panchangSubTab.asStateFlow()

    private val _kundaliSubTab = MutableStateFlow(0) // 0: Kundali Chart, 1: Guna Matching, 2: Astro AI, 3: Transits
    val kundaliSubTab: StateFlow<Int> = _kundaliSubTab.asStateFlow()

    private val _transitKundali = MutableStateFlow<KundaliChartData?>(null)
    val transitKundali: StateFlow<KundaliChartData?> = _transitKundali.asStateFlow()

    fun calculateCurrentTransits() {
        viewModelScope.launch(Dispatchers.Default) {
            val now = java.util.Calendar.getInstance()
            val dob = String.format(java.util.Locale.US, "%d-%02d-%02d", now.get(java.util.Calendar.YEAR), now.get(java.util.Calendar.MONTH) + 1, now.get(java.util.Calendar.DAY_OF_MONTH))
            val tob = String.format(java.util.Locale.US, "%02d:%02d", now.get(java.util.Calendar.HOUR_OF_DAY), now.get(java.util.Calendar.MINUTE))
            
            val transitData = KundaliCalculator.generateKundali(
                name = "Current Transits",
                dobString = dob,
                tobString = tob,
                placeName = _selectedCity.value.cityName,
                lat = _selectedCity.value.latitude,
                lng = _selectedCity.value.longitude
            )
            _transitKundali.value = transitData
        }
    }

    private val _moreSubTab = MutableStateFlow(0) // 0: Saved Profiles, 1: Settings
    val moreSubTab: StateFlow<Int> = _moreSubTab.asStateFlow()

    fun selectTab(tab: AppTab) {
        _selectedTab.value = tab
    }

    fun setPanchangSubTab(subTab: Int) {
        _panchangSubTab.value = subTab
    }

    fun setKundaliSubTab(subTab: Int) {
        _kundaliSubTab.value = subTab
    }

    fun setMoreSubTab(subTab: Int) {
        _moreSubTab.value = subTab
    }

    fun navigateToPanchang(subTab: Int = 0) {
        _panchangSubTab.value = subTab
        _selectedTab.value = AppTab.PANCHANG
    }

    fun navigateToKundali(subTab: Int = 0) {
        _kundaliSubTab.value = subTab
        _selectedTab.value = AppTab.KUNDALI
    }

    fun navigateToMore(subTab: Int = 0) {
        _moreSubTab.value = subTab
        _selectedTab.value = AppTab.MORE
    }

    fun navigateToRashifal() {
        _selectedTab.value = AppTab.RASHIFAL
    }

    fun navigateToMuhurat() {
        _selectedTab.value = AppTab.MUHURAT
    }

    // Language Toggle
    fun toggleLanguage() {
        LanguageManager.toggleLanguage()
    }

    // Selected City Location for Panchang
    private val _selectedCity = MutableStateFlow(loadCityFromPrefs())
    val selectedCity: StateFlow<CityLocation> = _selectedCity.asStateFlow()

    private fun loadCityFromPrefs(): CityLocation {
        val lat = sharedPrefs.getFloat("city_lat", 26.9124f).toDouble()
        val lon = sharedPrefs.getFloat("city_lon", 75.7873f).toDouble()
        val name = sharedPrefs.getString("city_name", "Jaipur") ?: "Jaipur"
        val nameHi = sharedPrefs.getString("city_name_hi", "जयपुर") ?: "जयपुर"
        val state = sharedPrefs.getString("city_state", "Rajasthan") ?: "Rajasthan"
        return CityLocation(name, nameHi, state, lat, lon)
    }

    fun setCity(city: CityLocation) {
        _selectedCity.value = city
        sharedPrefs.edit()
            .putFloat("city_lat", city.latitude.toFloat())
            .putFloat("city_lon", city.longitude.toFloat())
            .putString("city_name", city.cityName)
            .putString("city_name_hi", city.cityNameHindi)
            .putString("city_state", city.state)
            .apply()
        recalculatePanchang()
    }

    fun detectGPSLocation(context: Context, onComplete: (Boolean) -> Unit = {}) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onComplete(false)
            return
        }

        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    handleLocation(context, location.latitude, location.longitude, onComplete)
                } else {
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        if (lastLoc != null) {
                            handleLocation(context, lastLoc.latitude, lastLoc.longitude, onComplete)
                        } else {
                            onComplete(false)
                        }
                    }.addOnFailureListener {
                        onComplete(false)
                    }
                }
            }.addOnFailureListener {
                onComplete(false)
            }
        } catch (e: SecurityException) {
            onComplete(false)
        } catch (e: Throwable) {
            onComplete(false)
        }
    }

    private fun handleLocation(context: Context, lat: Double, lon: Double, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var cityName = "Current Location"
            var stateName = "GPS"
            try {
                val geocoder = android.location.Geocoder(context, java.util.Locale.getDefault())
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    cityName = address.locality ?: address.subAdminArea ?: address.adminArea ?: "Current Location"
                    stateName = address.adminArea ?: "GPS"
                }
            } catch (e: Exception) {
                // Ignore geocoding failure, fallback to default
            }

            val gpsCity = CityLocation(
                cityName = cityName,
                cityNameHindi = cityName, // Assuming Hindi translation isn't available from Geocoder easily, keeping same
                state = stateName,
                latitude = lat,
                longitude = lon
            )
            launch(Dispatchers.Main) {
                setCity(gpsCity)
                onComplete(true)
            }
        }
    }

    // Notification Toggles
    val dailyRahuKaalAlert = MutableStateFlow(sharedPrefs.getBoolean("daily_notification_enabled", true))
    val festivalRemindersAlert = MutableStateFlow(sharedPrefs.getBoolean("festival_notification_enabled", true))
    
    private val _notificationHour = MutableStateFlow(sharedPrefs.getInt("notification_hour", 7))
    val notificationHour: StateFlow<Int> = _notificationHour.asStateFlow()
    
    private val _notificationMinute = MutableStateFlow(sharedPrefs.getInt("notification_minute", 0))
    val notificationMinute: StateFlow<Int> = _notificationMinute.asStateFlow()

    fun toggleRahuKaalAlert() {
        dailyRahuKaalAlert.value = !dailyRahuKaalAlert.value
        sharedPrefs.edit().putBoolean("daily_notification_enabled", dailyRahuKaalAlert.value).apply()
        val context = getApplication<Application>()
        com.example.worker.AstroNotificationWorker.scheduleDailyNotification(context)
    }

    fun setNotificationTime(hour: Int, minute: Int) {
        _notificationHour.value = hour
        _notificationMinute.value = minute
        sharedPrefs.edit()
            .putInt("notification_hour", hour)
            .putInt("notification_minute", minute)
            .apply()
        val context = getApplication<Application>()
        com.example.worker.AstroNotificationWorker.scheduleDailyNotification(context)
    }

    fun toggleFestivalAlert() {
        festivalRemindersAlert.value = !festivalRemindersAlert.value
        sharedPrefs.edit().putBoolean("festival_notification_enabled", festivalRemindersAlert.value).apply()
        val context = getApplication<Application>()
        com.example.worker.FestivalNotificationWorker.scheduleFestivalNotification(context)
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
            try {
                _panchangState.value = cacheRepository.getPanchangWith7DayCache(_selectedDate.value, _selectedCity.value)
            } catch (e: Exception) {
                reportError(e)
            }
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
            try {
                _dailyHoroscopes.value = cacheRepository.getHoroscopesWith7DayCache()
            } catch (e: Exception) {
                reportError(e)
            }
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

    private val _isCalculating = MutableStateFlow(false)
    val isCalculating: StateFlow<Boolean> = _isCalculating.asStateFlow()

    fun generateKundaliChart(name: String, dob: String, tob: String, place: String) {
        kundaliName.value = name
        kundaliDob.value = dob
        kundaliTob.value = tob
        kundaliPlace.value = place
        
        addRecentSearch("KUNDALI", name, dob, tob, place)
        
        viewModelScope.launch(Dispatchers.Default) {
            _isCalculating.value = true
            try {
                val result = KundaliCalculator.generateKundali(name, dob, tob, place)
                _generatedKundali.value = result
                triggerInterstitial()
            } catch (e: Exception) {
                reportError(e)
            } finally {
                _isCalculating.value = false
            }
        }
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
        addRecentSearch("MATCHING", matchBoyName.value, matchBoyDob.value, matchGirlName.value, matchGirlDob.value)
        viewModelScope.launch(Dispatchers.Default) {
            _isCalculating.value = true
            try {
                val result = KundaliMatchingCalculator.matchKundali(
                    matchBoyName.value, matchBoyDob.value, matchBoyTob.value,
                    matchGirlName.value, matchGirlDob.value, matchGirlTob.value
                )
                _gunaResult.value = result
                triggerInterstitial()
            } catch (e: Exception) {
                reportError(e)
            } finally {
                _isCalculating.value = false
            }
        }
    }

    // Numerology
    var numName = MutableStateFlow("Sunil Saini")
    var numDob = MutableStateFlow("1995-07-22")

    private val _numerologyData = MutableStateFlow(
        NumerologyCalculator.calculateNumerology("Sunil Saini", "1995-07-22")
    )
    val numerologyData: StateFlow<NumerologyData> = _numerologyData.asStateFlow()

    fun calculateNumerology() {
        try {
            _numerologyData.value = NumerologyCalculator.calculateNumerology(numName.value, numDob.value)
        } catch (e: Exception) {
            reportError(e)
        }
    }

    // AI Astrologer Chat
    private val _aiResponse = MutableStateFlow("")
    val aiResponse: StateFlow<String> = _aiResponse.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _isAiOffline = MutableStateFlow(false)
    val isAiOffline: StateFlow<Boolean> = _isAiOffline.asStateFlow()

    // Personalized Rashifal Insight
    private val _aiRashifalInsight = MutableStateFlow("")
    val aiRashifalInsight: StateFlow<String> = _aiRashifalInsight.asStateFlow()

    private val _isRashifalAiLoading = MutableStateFlow(false)
    val isRashifalAiLoading: StateFlow<Boolean> = _isRashifalAiLoading.asStateFlow()

    fun fetchPersonalizedInsight(rashiName: String) {
        viewModelScope.launch {
            _isRashifalAiLoading.value = true
            try {
                val question = "Provide a personalized daily horoscope insight for $rashiName."
                _aiRashifalInsight.value = GeminiAstroService.getAiAstrologyInsight(question, "Rashi: $rashiName")
            } catch (e: Exception) {
                _aiRashifalInsight.value = "Unable to fetch personalized insight at the moment."
            } finally {
                _isRashifalAiLoading.value = false
            }
        }
    }

    fun askAiAstrologer(question: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            _isAiOffline.value = false
            try {
                val kundaliDetails = "${_generatedKundali.value.personName}, DOB: ${_generatedKundali.value.dateOfBirth}, Lagna: ${_generatedKundali.value.ascendantRashiHi}"
                val res = GeminiAstroService.getAiAstrologyInsight(question, kundaliDetails)
                _aiResponse.value = res
                if (res == com.example.data.ai.GeminiAstroService.getOfflineVedicResponse(question)) {
                    _isAiOffline.value = true
                }
            } catch (e: Exception) {
                _isAiOffline.value = true
                _aiResponse.value = com.example.data.ai.GeminiAstroService.getOfflineVedicResponse(question)
                reportError(e)
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    // Astro & Astronomical News Grounded via Google Search & Gemini API
    private val _astroNews = MutableStateFlow("")
    val astroNews: StateFlow<String> = _astroNews.asStateFlow()

    private val _isNewsLoading = MutableStateFlow(false)
    val isNewsLoading: StateFlow<Boolean> = _isNewsLoading.asStateFlow()

    private val _isNewsOffline = MutableStateFlow(false)
    val isNewsOffline: StateFlow<Boolean> = _isNewsOffline.asStateFlow()

    fun fetchAstroNews() {
        viewModelScope.launch {
            _isNewsLoading.value = true
            _isNewsOffline.value = false
            try {
                val news = GeminiAstroService.fetchAstroNewsWithSearchGrounding()
                _astroNews.value = news
                if (news == com.example.data.ai.GeminiAstroService.getOfflineAstroNews()) {
                    _isNewsOffline.value = true
                }
            } catch (e: Exception) {
                _isNewsOffline.value = true
                _astroNews.value = com.example.data.ai.GeminiAstroService.getOfflineAstroNews()
                reportError(e)
            } finally {
                _isNewsLoading.value = false
            }
        }
    }

    // Room DB Recent Searches
    private val _recentSearches = MutableStateFlow<List<RecentSearchEntity>>(emptyList())
    val recentSearches: StateFlow<List<RecentSearchEntity>> = _recentSearches.asStateFlow()

    // Room DB Profiles
    private val _savedProfiles = MutableStateFlow<List<KundaliEntity>>(emptyList())
    val savedProfiles: StateFlow<List<KundaliEntity>> = _savedProfiles.asStateFlow()

    private fun loadRecentSearches() {
        viewModelScope.launch {
            recentSearchRepository.recentSearches.collect { list ->
                _recentSearches.value = list
            }
        }
    }

    fun addRecentSearch(type: String, name: String, dob: String, tob: String, place: String) {
        val data = "$name|$dob|$tob|$place"
        viewModelScope.launch {
            recentSearchRepository.insertSearch(RecentSearchEntity(type = type, data = data))
        }
    }

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

    fun saveNewProfile(name: String, dob: String, tob: String, place: String) {
        viewModelScope.launch {
            val entity = KundaliEntity(
                name = name,
                gender = "MALE",
                dateOfBirth = dob,
                timeOfBirth = tob,
                placeOfBirth = place,
                latitude = _selectedCity.value.latitude,
                longitude = _selectedCity.value.longitude,
                notes = "Saved Profile"
            )
            repository.saveProfile(entity)
        }
    }

    fun deleteProfile(entity: KundaliEntity) {
        viewModelScope.launch {
            repository.deleteProfile(entity)
        }
    }

    // Room DB Saved Reports
    private val _savedReports = MutableStateFlow<List<SavedReportEntity>>(emptyList())
    val savedReports: StateFlow<List<SavedReportEntity>> = _savedReports.asStateFlow()

    private fun loadSavedReports() {
        viewModelScope.launch {
            reportRepository.allReports.collect { list ->
                _savedReports.value = list
            }
        }
    }

    fun saveReport(
        title: String,
        reportType: String,
        profileName: String,
        summaryText: String,
        detailedJsonData: String = ""
    ) {
        viewModelScope.launch {
            val entity = SavedReportEntity(
                title = title,
                reportType = reportType,
                profileName = profileName,
                summaryText = summaryText,
                detailedJsonData = detailedJsonData
            )
            reportRepository.saveReport(entity)
        }
    }

    fun deleteReport(entity: SavedReportEntity) {
        viewModelScope.launch {
            reportRepository.deleteReport(entity)
        }
    }

    // Firebase Auth & Cloud Backup
    private val authService = com.example.service.FirebaseAuthService()
    private val _currentUser = MutableStateFlow<com.google.firebase.auth.FirebaseUser?>(authService.currentUser)
    val currentUser: StateFlow<com.google.firebase.auth.FirebaseUser?> = _currentUser.asStateFlow()

    private val _backupStatusMessage = MutableStateFlow<String?>(null)
    val backupStatusMessage: StateFlow<String?> = _backupStatusMessage.asStateFlow()

    fun signInWithGoogle(context: android.content.Context, webClientId: String = "") {
        viewModelScope.launch {
            _backupStatusMessage.value = "Signing in with Google..."
            val result = authService.signInWithGoogle(context, webClientId)
            result.onSuccess { user ->
                _currentUser.value = user
                _backupStatusMessage.value = "Signed in as ${user.displayName ?: user.email}"
            }.onFailure { err ->
                _backupStatusMessage.value = "Sign-In failed: ${err.message}"
            }
        }
    }

    fun signOutFirebase() {
        authService.signOut()
        _currentUser.value = null
        _backupStatusMessage.value = "Signed out"
    }

    fun backupProfilesToCloud() {
        val user = _currentUser.value
        if (user == null) {
            _backupStatusMessage.value = "Please sign in with Google first."
            return
        }
        viewModelScope.launch {
            _backupStatusMessage.value = "Backing up profiles to cloud..."
            val profiles = _savedProfiles.value
            val result = authService.backupProfilesToCloud(profiles)
            result.onSuccess { count ->
                _backupStatusMessage.value = "Successfully backed up $count profiles to cloud!"
            }.onFailure { err ->
                _backupStatusMessage.value = "Backup failed: ${err.message}"
            }
        }
    }

    fun restoreProfilesFromCloud() {
        val user = _currentUser.value
        if (user == null) {
            _backupStatusMessage.value = "Please sign in with Google first."
            return
        }
        viewModelScope.launch {
            _backupStatusMessage.value = "Restoring profiles from cloud..."
            val result = authService.restoreProfilesFromCloud()
            result.onSuccess { cloudProfiles ->
                cloudProfiles.forEach { profile ->
                    repository.saveProfile(profile)
                }
                _backupStatusMessage.value = "Restored ${cloudProfiles.size} profiles from cloud!"
            }.onFailure { err ->
                _backupStatusMessage.value = "Restore failed: ${err.message}"
            }
        }
    }

    fun clearBackupStatusMessage() {
        _backupStatusMessage.value = null
    }

    // Premium Dialog state
    var showPremiumDialog = MutableStateFlow(false)

    init {
        repository = DatabaseProvider.getKundaliRepository(application)
        reportRepository = DatabaseProvider.getSavedReportRepository(application)
        recentSearchRepository = DatabaseProvider.getRecentSearchRepository(application)
        cacheRepository = DatabaseProvider.getAstroCacheRepository(application)
        monitorNetwork()
        loadSavedProfiles()
        loadRecentSearches()
        loadSavedReports()
        recalculatePanchang()
        loadHoroscopesWithCache()
        fetchAstroNews()
    }
}
