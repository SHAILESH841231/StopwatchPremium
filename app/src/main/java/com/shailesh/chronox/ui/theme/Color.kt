package com.shailesh.chronox.ui.theme

import androidx.compose.ui.graphics.Color

// Shared colors
val GlassWhite = Color(0x1AFFFFFF)
val GlassStroke = Color(0x33FFFFFF)

// 1. Midnight Black (Black + Purple)
val MidnightBackground = Color(0xFF000000)
val MidnightPrimary = Color(0xFFBB86FC)
val MidnightSecondary = Color(0xFF03DAC6)

// 2. Ocean Blue (Dark Blue + Cyan)
val OceanBackground = Color(0xFF0D1B2A)
val OceanPrimary = Color(0xFF00D4FF)
val OceanSecondary = Color(0xFF00FFA3)

// 3. Emerald Green (Green + Black)
val EmeraldBackground = Color(0xFF061A13)
val EmeraldPrimary = Color(0xFF2ECC71)
val EmeraldSecondary = Color(0xFF00FF99)

// 4. Sunset Orange (Orange + Dark Gray)
val SunsetBackground = Color(0xFF1A120B)
val SunsetPrimary = Color(0xFFFF8C00)
val SunsetSecondary = Color(0xFFFFD700)

// 5. Ruby Red (Red + Black)
val RubyBackground = Color(0xFF1A0505)
val RubyPrimary = Color(0xFFFF453A)
val RubySecondary = Color(0xFFFF9F0A)

// 6. Arctic White (White + Blue)
val ArcticBackground = Color(0xFFF0F4F8)
val ArcticPrimary = Color(0xFF007AFF)
val ArcticSecondary = Color(0xFF5AC8FA)

// Legacy compatibility tokens (will be overridden by dynamic choice)
val ElectricBlue = OceanPrimary
val NeonGreen = OceanSecondary
val VividPurple = MidnightPrimary
val DarkBackground = MidnightBackground
val SurfaceDark = Color(0xFF161B22)

// M3 Palette Base for Midnight Black
val PrimaryDark = MidnightPrimary
val OnPrimaryDark = Color.Black
val PrimaryContainerDark = Color(0xFF3700B3)
val OnPrimaryContainerDark = Color(0xFFE1D5FF)

val SecondaryDark = MidnightSecondary
val OnSecondaryDark = Color.Black
val SecondaryContainerDark = Color(0xFF018786)
val OnSecondaryContainerDark = Color(0xFFD5FFFF)

val TertiaryDark = Color(0xFFCF6679)
val OnTertiaryDark = Color.Black
val TertiaryContainerDark = Color(0xFF8B0000)
val OnTertiaryContainerDark = Color(0xFFFFD1D1)

val ErrorDark = Color(0xFFCF6679)
val OnErrorDark = Color.Black

val BackgroundDark = MidnightBackground
val OnBackgroundDark = Color(0xFFE1E2E5)
val SurfaceVariantDark = Color(0xFF1F1F1F)
val OnSurfaceVariantDark = Color(0xFFC4C6D0)
val OutlineDark = Color(0xFF8E9199)
