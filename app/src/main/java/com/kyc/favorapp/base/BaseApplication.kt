package com.kyc.favorapp.base

import android.app.Application
import com.squareup.leakcanary.LeakCanary



class BaseApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        setupLeakCanary()

    }

    private fun setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }

    companion object {
        lateinit var INSTANCE: BaseApplication
    }
}