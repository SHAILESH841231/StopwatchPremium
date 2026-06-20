package com.shailesh.chronox.ui.stopwatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StopwatchState(
    val timeMillis: Long = 0L,
    val isRunning: Boolean = false,
    val laps: List<Long> = emptyList()
)

class StopwatchViewModel : ViewModel() {

    private val _state = MutableStateFlow(StopwatchState())
    val state: StateFlow<StopwatchState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var lastTimestamp: Long = 0L

    fun start() {
        if (_state.value.isRunning) return

        _state.update { it.copy(isRunning = true) }
        lastTimestamp = System.currentTimeMillis()
        
        timerJob = viewModelScope.launch {
            while (true) {
                val currentTimestamp = System.currentTimeMillis()
                val diff = currentTimestamp - lastTimestamp
                lastTimestamp = currentTimestamp
                
                _state.update { 
                    it.copy(timeMillis = it.timeMillis + diff)
                }
                delay(10) // Update every ~10ms for millisecond precision display
            }
        }
    }

    fun pause() {
        if (!_state.value.isRunning) return
        
        _state.update { it.copy(isRunning = false) }
        timerJob?.cancel()
        timerJob = null
    }

    fun reset() {
        pause()
        _state.update { StopwatchState() }
    }

    fun recordLap() {
        val currentTime = _state.value.timeMillis
        _state.update { 
            it.copy(laps = listOf(currentTime) + it.laps)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
