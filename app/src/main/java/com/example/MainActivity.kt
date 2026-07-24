package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.components.AdBanner
import com.example.ui.components.BottomNavBar
import com.example.ui.components.PremiumDialog
import com.example.ui.components.TopHeaderBar
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.KundaliScreen
import com.example.ui.screens.MatchingScreen
import com.example.ui.screens.MoreScreen
import com.example.ui.screens.MuhuratScreen
import com.example.ui.screens.NumerologyScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PanchangScreen
import com.example.ui.screens.RashifalScreen
import com.example.ui.screens.SavedProfilesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AstroVedaTheme
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.launch
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private var mInterstitialAd: InterstitialAd? = null
    private var lastInterstitialShowTime = 0L

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                com.example.worker.AstroNotificationWorker.scheduleDailyNotification(this)
                com.example.worker.FestivalNotificationWorker.scheduleFestivalNotification(this)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            MobileAds.initialize(this) {}
            loadInterstitialAd()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                if (androidx.core.content.ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    com.example.worker.AstroNotificationWorker.scheduleDailyNotification(this)
                    com.example.worker.FestivalNotificationWorker.scheduleFestivalNotification(this)
                } else {
                    requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                com.example.worker.AstroNotificationWorker.scheduleDailyNotification(this)
                com.example.worker.FestivalNotificationWorker.scheduleFestivalNotification(this)
            }
        } catch (e: Throwable) {
            // fail gracefully
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.showInterstitialTrigger.collect {
                    showInterstitialAd()
                }
            }
        }

        setContent {
            AstroVedaTheme {
                val selectedTab by mainViewModel.selectedTab.collectAsState()
                val showPremium by mainViewModel.showPremiumDialog.collectAsState()
                val isOnboardingCompleted by mainViewModel.isOnboardingCompleted.collectAsState()
                val isOffline by mainViewModel.isOffline.collectAsState()

                if (!isOnboardingCompleted) {
                    OnboardingScreen(
                        viewModel = mainViewModel,
                        onComplete = { mainViewModel.completeOnboarding() }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopHeaderBar(
                                isOffline = isOffline,
                                onLanguageToggle = { mainViewModel.toggleLanguage() },
                                onPremiumClick = { mainViewModel.showPremiumDialog.value = true },
                                onSettingsClick = { mainViewModel.navigateToMore(subTab = 1) }
                            )
                        },
                        bottomBar = {
                            Column {
                                val isPro by mainViewModel.isProUser.collectAsState()
                                if (!isPro && selectedTab == AppTab.PANCHANG) {
                                    AdBanner(
                                        onRemoveAdsClick = { mainViewModel.showPremiumDialog.value = true }
                                    )
                                }
                                BottomNavBar(
                                    selectedTab = selectedTab,
                                    onTabSelected = { mainViewModel.selectTab(it) }
                                )
                            }
                        }
                    ) { innerPadding ->
                        val globalError by mainViewModel.globalError.collectAsState()

                        com.example.ui.components.ErrorBoundary(
                            externalError = globalError,
                            onClearError = { mainViewModel.clearGlobalError() },
                            onRetry = {
                                // Clear error and reset tab or rerun last query
                                mainViewModel.clearGlobalError()
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                AnimatedContent(
                                    targetState = selectedTab,
                                    label = "TabTransition",
                                    transitionSpec = {
                                        if (targetState == AppTab.KUNDALI || initialState == AppTab.KUNDALI) {
                                            (fadeIn(animationSpec = tween(400, delayMillis = 90)) + 
                                             scaleIn(initialScale = 0.92f, animationSpec = tween(400)))
                                                .togetherWith(fadeOut(animationSpec = tween(300)))
                                        } else {
                                            fadeIn(animationSpec = tween(300))
                                                .togetherWith(fadeOut(animationSpec = tween(300)))
                                        }
                                    }
                                ) { tab ->
                                    when (tab) {
                                        AppTab.PANCHANG -> PanchangScreen(mainViewModel)
                                        AppTab.RASHIFAL -> RashifalScreen(mainViewModel)
                                        AppTab.KUNDALI -> KundaliScreen(mainViewModel)
                                        AppTab.MUHURAT -> MuhuratScreen(mainViewModel)
                                        AppTab.MORE -> MoreScreen(mainViewModel)
                                    }
                                }

                                if (showPremium) {
                                    PremiumDialog(
                                        viewModel = mainViewModel,
                                        onDismiss = { mainViewModel.showPremiumDialog.value = false }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadInterstitialAd() {
        val interstitialId = try {
            com.example.BuildConfig.ADMOB_INTERSTITIAL_ID.ifBlank { "ca-app-pub-3940256099942544/1033173712" }
        } catch (e: Throwable) {
            "ca-app-pub-3940256099942544/1033173712"
        }

        if (interstitialId.isBlank()) return

        val adRequest = AdRequest.Builder().build()
        try {
            InterstitialAd.load(this, interstitialId, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            })
        } catch (e: Throwable) {
            // fail gracefully
        }
    }

    private fun showInterstitialAd() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastInterstitialShowTime < 60_000L) {
            // Frequency capped - do not show too often
            return
        }

        if (mainViewModel.isProUser.value) {
            return
        }

        try {
            mInterstitialAd?.let { ad ->
                ad.show(this)
                mInterstitialAd = null
                lastInterstitialShowTime = currentTime
                loadInterstitialAd() // Preload the next one
            } ?: run {
                loadInterstitialAd()
            }
        } catch (e: Throwable) {
            // fail gracefully
        }
    }
}

