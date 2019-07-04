package com.kyc.favorapp.base

import android.app.Application

class BaseApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

    }

    companion object {
        lateinit var INSTANCE: BaseApplication
    }
}