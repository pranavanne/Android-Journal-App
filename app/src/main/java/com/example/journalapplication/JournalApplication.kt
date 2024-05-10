package com.example.journalapplication

import android.app.Application
import com.example.journalapplication.data.AppContainer
import com.example.journalapplication.data.AppDataContainer

class JournalApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}