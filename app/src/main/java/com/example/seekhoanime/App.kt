package com.example.seekhoanime


import android.app.Application

class App : Application(){

    override fun onCreate() {
        super.onCreate()
        AppGraph.init(this)
    }
}
