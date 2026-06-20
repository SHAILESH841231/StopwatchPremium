package com.shailesh.chronox

import com.shailesh.chronox.ui.stopwatch.StopwatchViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceTimeBy
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StopwatchViewModelTest {

    private lateinit var viewModel: StopwatchViewModel

    @Before
    fun setup() {
        viewModel = StopwatchViewModel()
    }

    @Test
    fun `initial state is correct`() {
        val state = viewModel.state.value
        assertEquals(0L, state.timeMillis)
        assertFalse(state.isRunning)
        assertTrue(state.laps.isEmpty())
    }

    @Test
    fun `start changes state to running`() = runTest {
        viewModel.start()
        assertTrue(viewModel.state.value.isRunning)
    }

    @Test
    fun `pause changes state to not running`() = runTest {
        viewModel.start()
        viewModel.pause()
        assertFalse(viewModel.state.value.isRunning)
    }

    @Test
    fun `reset clears state`() = runTest {
        viewModel.start()
        // Wait a bit
        advanceTimeBy(100)
        viewModel.recordLap()
        viewModel.reset()
        
        val state = viewModel.state.value
        assertEquals(0L, state.timeMillis)
        assertFalse(state.isRunning)
        assertTrue(state.laps.isEmpty())
    }

    @Test
    fun `recordLap adds lap to list`() = runTest {
        viewModel.start()
        advanceTimeBy(50)
        viewModel.recordLap()
        
        assertEquals(1, viewModel.state.value.laps.size)
    }
}
