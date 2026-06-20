package com.shailesh.chronox

import android.app.Application
import com.shailesh.chronox.data.theme.ThemeRepository

class StopwatchApplication : Application() {
    lateinit var themeRepository: ThemeRepository

    override fun onCreate() {
        super.onCreate()
        themeRepository = ThemeRepository(this)
    }
}
