package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
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
import com.example.ui.screens.MuhuratScreen
import com.example.ui.screens.NumerologyScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PanchangScreen
import com.example.ui.screens.RashifalScreen
import com.example.ui.screens.SavedProfilesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AstroVedaTheme

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AstroVedaTheme {
                val selectedTab by mainViewModel.selectedTab.collectAsState()
                val showPremium by mainViewModel.showPremiumDialog.collectAsState()
                val isOnboardingCompleted by mainViewModel.isOnboardingCompleted.collectAsState()

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
                                onLanguageToggle = { mainViewModel.toggleLanguage() },
                                onPremiumClick = { mainViewModel.showPremiumDialog.value = true },
                                onSettingsClick = { mainViewModel.selectTab(AppTab.SETTINGS) }
                            )
                        },
                        bottomBar = {
                            Column {
                                if (selectedTab == AppTab.PANCHANG || selectedTab == AppTab.CALENDAR) {
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
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            Crossfade(targetState = selectedTab, label = "TabTransition") { tab ->
                                when (tab) {
                                    AppTab.PANCHANG -> PanchangScreen(mainViewModel)
                                    AppTab.CALENDAR -> CalendarScreen(mainViewModel)
                                    AppTab.RASHIFAL -> RashifalScreen(mainViewModel)
                                    AppTab.KUNDALI -> KundaliScreen(mainViewModel)
                                    AppTab.MUHURAT -> MuhuratScreen(mainViewModel)
                                    AppTab.MATCHING -> MatchingScreen(mainViewModel)
                                    AppTab.NUMEROLOGY_AI -> NumerologyScreen(mainViewModel)
                                    AppTab.SAVED_PROFILES -> SavedProfilesScreen(mainViewModel)
                                    AppTab.SETTINGS -> SettingsScreen(
                                        viewModel = mainViewModel,
                                        onShowPremiumDialog = { mainViewModel.showPremiumDialog.value = true }
                                    )
                                }
                            }

                            if (showPremium) {
                                PremiumDialog(
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

