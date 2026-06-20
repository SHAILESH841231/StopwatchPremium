package com.shailesh.chronox.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavRoute

@Serializable
data object Stopwatch : NavRoute

@Serializable
data object Settings : NavRoute
