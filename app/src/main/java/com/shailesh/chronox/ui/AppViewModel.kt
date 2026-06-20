package com.shailesh.chronox.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shailesh.chronox.data.theme.AppTheme
import com.shailesh.chronox.data.theme.ThemeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(private val themeRepository: ThemeRepository) : ViewModel() {

    val themeState: StateFlow<AppTheme> = themeRepository.themeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.MIDNIGHT_BLACK
        )

    val amoledState: StateFlow<Boolean> = themeRepository.amoledFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            themeRepository.setTheme(theme)
        }
    }

    fun setAmoledMode(enabled: Boolean) {
        viewModelScope.launch {
            themeRepository.setAmoledMode(enabled)
        }
    }

    class Factory(private val themeRepository: ThemeRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AppViewModel(themeRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
