package com.app.music

import android.app.Application
import com.app.music.database.AppDatabase

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize the database
        AppDatabase.createInstance(this)
    }
}