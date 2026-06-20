package com.shailesh.chronox

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.runtime.NavEntry
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowWidthSizeClass
import com.shailesh.chronox.data.theme.AppTheme
import com.shailesh.chronox.ui.AppViewModel
import com.shailesh.chronox.ui.components.GlassCard
import com.shailesh.chronox.ui.navigation.Settings
import com.shailesh.chronox.ui.navigation.Stopwatch
import com.shailesh.chronox.ui.stopwatch.StopwatchState
import com.shailesh.chronox.ui.stopwatch.StopwatchViewModel
import com.shailesh.chronox.ui.theme.*
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val application = context.applicationContext as StopwatchApplication
            val appViewModel: AppViewModel = viewModel(
                factory = AppViewModel.Factory(application.themeRepository)
            )
            val currentTheme by appViewModel.themeState.collectAsState()
            val isAmoled by appViewModel.amoledState.collectAsState()

            ChronoXTheme(appTheme = currentTheme, isAmoled = isAmoled) {
                val backStack = remember { mutableStateListOf<Any>(Stopwatch) }
                
                BackHandler(enabled = backStack.size > 1) {
                    backStack.removeLastOrNull()
                }

                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = { key ->
                        when (key) {
                            is Stopwatch -> NavEntry(key) { _ ->
                                StopwatchContent(backStack)
                            }
                            is Settings -> NavEntry(key) { _ ->
                                SettingsContent(currentTheme, isAmoled, appViewModel, backStack)
                            }
                            else -> NavEntry(key) { _ -> Text("Unknown Route") }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun StopwatchContent(backStack: SnapshotStateList<Any>) {
    val stopwatchViewModel: StopwatchViewModel = viewModel()
    val state by stopwatchViewModel.state.collectAsState()
    
    StopwatchMainScreen(
        state = state,
        onStart = stopwatchViewModel::start,
        onPause = stopwatchViewModel::pause,
        onReset = stopwatchViewModel::reset,
        onLap = stopwatchViewModel::recordLap,
        onNavigateToSettings = { backStack.add(Settings) }
    )
}

@Composable
fun SettingsContent(
    currentTheme: AppTheme,
    isAmoled: Boolean,
    appViewModel: AppViewModel,
    backStack: SnapshotStateList<Any>
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    SettingsScreen(
        currentTheme = currentTheme,
        isAmoled = isAmoled,
        onThemeSelected = { theme ->
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            appViewModel.setTheme(theme)
            changeAppIcon(context, theme)
        },
        onAmoledToggle = { enabled ->
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            appViewModel.setAmoledMode(enabled)
        },
        onBack = { backStack.removeLastOrNull() }
    )
}

@Composable
fun StopwatchMainScreen(
    state: StopwatchState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onLap: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CHRONOX",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    ) { innerPadding ->
        AdaptiveStopwatchScreen(
            state = state,
            onStart = onStart,
            onPause = onPause,
            onReset = onReset,
            onLap = onLap,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun SettingsScreen(
    currentTheme: AppTheme,
    isAmoled: Boolean,
    onThemeSelected: (AppTheme) -> Unit,
    onAmoledToggle: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            // AMOLED Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AMOLED Black Mode",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "True black background for battery saving",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isAmoled,
                    onCheckedChange = onAmoledToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Text(
                text = "Themes & Icons",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 140.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(AppTheme.entries) { theme ->
                    ThemePreviewCard(
                        theme = theme,
                        isSelected = theme == currentTheme,
                        onClick = { onThemeSelected(theme) }
                    )
                }
            }
        }
    }
}

@Composable
fun ThemePreviewCard(
    theme: AppTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (primary, background, secondary) = when (theme) {
        AppTheme.MIDNIGHT_BLACK -> Triple(MidnightPrimary, MidnightBackground, MidnightSecondary)
        AppTheme.OCEAN_BLUE -> Triple(OceanPrimary, OceanBackground, OceanSecondary)
        AppTheme.EMERALD_GREEN -> Triple(EmeraldPrimary, EmeraldBackground, EmeraldSecondary)
        AppTheme.SUNSET_ORANGE -> Triple(SunsetPrimary, SunsetBackground, SunsetSecondary)
        AppTheme.RUBY_RED -> Triple(RubyPrimary, RubyBackground, RubySecondary)
        AppTheme.ARCTIC_WHITE -> Triple(ArcticPrimary, ArcticBackground, ArcticSecondary)
    }

    val cardColor by animateColorAsState(
        targetValue = if (isSelected) primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        label = "CardColorAnimation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, primary) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Mini Preview UI
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(background),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .border(2.dp, primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Represents app icon center dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(primary)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(primary)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(secondary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = theme.name.split("_").first().lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) primary else MaterialTheme.colorScheme.onSurface
                )
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = "Selected",
                        tint = primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AdaptiveStopwatchScreen(
    state: StopwatchState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onLap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isExpanded = adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED

    if (isExpanded) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                TimerSection(
                    state = state,
                    onStart = onStart,
                    onPause = onPause,
                    onReset = onReset,
                    onLap = onLap
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                LapList(laps = state.laps)
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TimerSection(
                state = state,
                onStart = onStart,
                onPause = onPause,
                onReset = onReset,
                onLap = onLap
            )
            Spacer(modifier = Modifier.height(40.dp))
            LapList(laps = state.laps, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun TimerSection(
    state: StopwatchState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onLap: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(max = 600.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            val secondsProgress = (state.timeMillis % 60000) / 60000f
            val animatedProgress by animateFloatAsState(
                targetValue = secondsProgress,
                animationSpec = tween(durationMillis = 10, easing = LinearEasing),
                label = "ProgressAnimation"
            )

            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.size(280.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 8.dp,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                strokeCap = StrokeCap.Round
            )

            GlassCard(
                modifier = Modifier.size(240.dp),
                cornerRadius = 120.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val minutes = (state.timeMillis / 1000) / 60
                    val seconds = (state.timeMillis / 1000) % 60
                    val millis = (state.timeMillis % 1000) / 10

                    Text(
                        text = String.format(Locale.getDefault(), "%02d : %02d", minutes, seconds),
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 48.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = String.format(Locale.getDefault(), ": %02d", millis),
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ControlButton(
                onClick = onReset,
                icon = Icons.Rounded.Refresh,
                contentDescription = "Reset",
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                iconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AnimatedContent(
                targetState = state.isRunning,
                transitionSpec = {
                    (scaleIn() + fadeIn()) togetherWith (scaleOut() + fadeOut())
                },
                label = "StartPauseAnimation"
            ) { isRunning ->
                if (isRunning) {
                    ControlButton(
                        onClick = onPause,
                        icon = Icons.Rounded.Pause,
                        contentDescription = "Pause",
                        containerColor = MaterialTheme.colorScheme.primary,
                        iconColor = MaterialTheme.colorScheme.onPrimary,
                        isLarge = true
                    )
                } else {
                    ControlButton(
                        onClick = onStart,
                        icon = Icons.Rounded.PlayArrow,
                        contentDescription = "Start",
                        containerColor = MaterialTheme.colorScheme.primary,
                        iconColor = MaterialTheme.colorScheme.onPrimary,
                        isLarge = true
                    )
                }
            }

            ControlButton(
                onClick = onLap,
                icon = Icons.Rounded.Flag,
                contentDescription = "Lap",
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                enabled = state.isRunning
            )
        }
    }
}

@Composable
fun LapList(laps: List<Long>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 48.dp)
    ) {
        itemsIndexed(laps) { index, lapTime ->
            val lapMinutes = (lapTime / 1000) / 60
            val lapSeconds = (lapTime / 1000) % 60
            val lapMillis = (lapTime % 1000) / 10
            
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 16.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LAP ${laps.size - index}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "%02d : %02d : %02d", lapMinutes, lapSeconds, lapMillis),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ControlButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    containerColor: Color,
    iconColor: Color,
    enabled: Boolean = true,
    isLarge: Boolean = false
) {
    val size = if (isLarge) 96.dp else 72.dp
    val iconSize = if (isLarge) 48.dp else 32.dp
    
    val animatedContainerColor by animateColorAsState(
        targetValue = if (enabled) containerColor else containerColor.copy(alpha = 0.3f),
        label = "ButtonColorAnimation"
    )

    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.9f,
        label = "ButtonScaleAnimation"
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .size(size)
            .scale(scale),
        shape = MaterialTheme.shapes.extraLarge,
        colors = ButtonDefaults.buttonColors(
            containerColor = animatedContainerColor,
            contentColor = iconColor,
            disabledContainerColor = containerColor.copy(alpha = 0.12f),
            disabledContentColor = iconColor.copy(alpha = 0.38f)
        ),
        enabled = enabled,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 2.dp
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize)
        )
    }
}

fun changeAppIcon(context: Context, theme: AppTheme) {
    val pm = context.packageManager
    val packageName = context.packageName
    
    val components = listOf(
        "MainActivity",
        "MainActivityMidnight",
        "MainActivityOcean",
        "MainActivityEmerald",
        "MainActivitySunset",
        "MainActivityRuby",
        "MainActivityArctic"
    )
    
    val targetComponent = when (theme) {
        AppTheme.MIDNIGHT_BLACK -> "MainActivityMidnight"
        AppTheme.OCEAN_BLUE -> "MainActivityOcean"
        AppTheme.EMERALD_GREEN -> "MainActivityEmerald"
        AppTheme.SUNSET_ORANGE -> "MainActivitySunset"
        AppTheme.RUBY_RED -> "MainActivityRuby"
        AppTheme.ARCTIC_WHITE -> "MainActivityArctic"
    }
    
    components.forEach { component ->
        val state = if (component == targetComponent) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }
        try {
            pm.setComponentEnabledSetting(
                ComponentName(packageName, "$packageName.$component"),
                state,
                PackageManager.DONT_KILL_APP
            )
        } catch (_: Exception) {
            // Component not found or other issue
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StopwatchScreenPreview() {
    ChronoXTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            AdaptiveStopwatchScreen(
                state = StopwatchState(
                    timeMillis = 123456, 
                    isRunning = true, 
                    laps = listOf(100000, 50000)
                ),
                onStart = {},
                onPause = {},
                onReset = {},
                onLap = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
