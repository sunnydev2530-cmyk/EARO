package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.game.GameViewModel
import com.example.model.AppScreen
import com.example.ui.components.CosmicParticleBackground
import com.example.ui.screens.GameplayScreen
import com.example.ui.screens.HowToPlayScreen
import com.example.ui.screens.LevelSelectScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.PrivacyPolicyScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.ArrowEscapeTheme

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            ArrowEscapeTheme {
                ArrowEscapeApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ArrowEscapeApp(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.onAppForegroundChanged(true)
                Lifecycle.Event.ON_PAUSE -> viewModel.onAppForegroundChanged(false)
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CosmicParticleBackground(
            isAnimating = uiState.isForeground && uiState.currentScreen != AppScreen.GAMEPLAY
        )

        AnimatedContent(
            targetState = uiState.currentScreen,
            transitionSpec = {
                fadeIn(tween(240)) togetherWith fadeOut(tween(180))
            },
            label = "screenTransition"
        ) { screen ->
            when (screen) {
                AppScreen.SPLASH -> {
                    SplashScreen()
                }
                AppScreen.MAIN_MENU -> {
                    MainMenuScreen(
                        uiState = uiState,
                        onPlayClick = { viewModel.playContinue() },
                        onLevelSelectClick = { viewModel.navigateTo(AppScreen.LEVEL_SELECT) },
                        onToggleSfx = { viewModel.toggleSfx() },
                        onToggleMusic = { viewModel.toggleMusic() },
                        onSettingsClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                        onHowToPlayClick = { viewModel.navigateTo(AppScreen.HOW_TO_PLAY) }
                    )
                }
                AppScreen.LEVEL_SELECT -> {
                    LevelSelectScreen(
                        levelProgress = uiState.levelProgress,
                        totalStarsEarned = uiState.totalStarsEarned,
                        maxStarsPossible = uiState.maxStarsPossible,
                        onSelectLevel = { levelNum -> viewModel.startLevel(levelNum) },
                        onBack = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
                    )
                }
                AppScreen.GAMEPLAY -> {
                    GameplayScreen(
                        gameplay = uiState.gameplay,
                        sfxEnabled = uiState.settings.sfxEnabled,
                        isForeground = uiState.isForeground,
                        onArrowTap = { id -> viewModel.onArrowTapped(id) },
                        onUndoClick = { viewModel.undoLastMove() },
                        onHintClick = { viewModel.useHint() },
                        onRestartClick = { viewModel.requestRestart() },
                        onConfirmRestart = { viewModel.confirmRestart() },
                        onDismissRestart = { viewModel.dismissRestartConfirm() },
                        onPauseToggle = { viewModel.setPaused(!uiState.gameplay.isPaused) },
                        onSoundToggle = { viewModel.toggleSfx() },
                        onNextLevel = { viewModel.playNextLevel() },
                        onLevelSelect = { viewModel.navigateTo(AppScreen.LEVEL_SELECT) },
                        onMainMenu = { viewModel.navigateTo(AppScreen.MAIN_MENU) },
                        onBackPressed = { viewModel.handleBackPress() }
                    )
                }
                AppScreen.SETTINGS -> {
                    SettingsScreen(
                        settings = uiState.settings,
                        showResetDialog = uiState.showResetProgressDialog,
                        onToggleSfx = { viewModel.toggleSfx() },
                        onToggleMusic = { viewModel.toggleMusic() },
                        onSfxVolumeChange = { viewModel.setSfxVolume(it) },
                        onMusicVolumeChange = { viewModel.setMusicVolume(it) },
                        onToggleVibration = { viewModel.toggleVibration() },
                        onRequestResetProgress = { viewModel.requestResetProgress() },
                        onConfirmResetProgress = { viewModel.confirmResetProgress() },
                        onDismissResetProgress = { viewModel.dismissResetProgress() },
                        onOpenPrivacyPolicy = { viewModel.navigateTo(AppScreen.PRIVACY_POLICY) },
                        onBack = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
                    )
                }
                AppScreen.HOW_TO_PLAY -> {
                    HowToPlayScreen(
                        onBack = { viewModel.navigateTo(AppScreen.MAIN_MENU) }
                    )
                }
                AppScreen.PRIVACY_POLICY -> {
                    PrivacyPolicyScreen(
                        onBack = { viewModel.navigateTo(AppScreen.SETTINGS) }
                    )
                }
            }
        }
    }
}
