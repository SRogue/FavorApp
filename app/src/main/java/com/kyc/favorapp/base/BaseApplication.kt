package com.kyc.favorapp.base

import android.app.Application
import android.content.Intent
import android.text.TextUtils
import com.kyc.favorapp.activity.ProxyActivity
import com.squareup.leakcanary.LeakCanary
import java.lang.Class.forName
import java.lang.reflect.Proxy


class BaseApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        setupLeakCanary()



//        try {
//            hookActivity()
//        } catch (e: Exception) {
//        }


    }

    @Throws(Exception::class)
    private fun hookActivity() {


        val iActivityManagerClass = forName("android.app.IActivityManager")

//        val activityManagerNormalClass = forName("android.app.ActivityManager")
        val activityManagerNormalClass = forName("android.app.ActivityManagerNative")
        val declaredFieldNormal = activityManagerNormalClass.getDeclaredField("gDefault")
        declaredFieldNormal.isAccessible = true
        val invokeGetServiceNormal = declaredFieldNormal.get(null)
//        val invokeGetServiceNormal = activityManagerNormalClass.getMethod("getDefault").invoke(null)


        val iActivityManagerProxy =
            Proxy.newProxyInstance(classLoader, arrayOf(iActivityManagerClass)) { proxy, method, args ->
                if (TextUtils.equals("startActivity", method.name)) {
                    val intent = Intent(INSTANCE, ProxyActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        putExtra(HOOK_RESOURCE, args[2] as Intent)
                    }
                    args[2] = intent
                }

                method.invoke(invokeGetServiceNormal, args)
            }


//        val activityManagerClass = forName("android.app.ActivityManager")
        val activityManagerClass = forName("android.app.ActivityManagerNative")
        val declaredField = activityManagerClass.getDeclaredField("gDefault")
        declaredField.isAccessible = true
        val invokeGetService = declaredField.get(null)

        val singletonClass = forName("android.util.Singleton")
        val mInstanceField = singletonClass.getDeclaredField("mInstance")
        mInstanceField.isAccessible = true
        mInstanceField.set(invokeGetService, iActivityManagerProxy)

    }

    private fun setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)


    }

    companion object {
        lateinit var INSTANCE: BaseApplication
        const val HOOK_RESOURCE = "HOOK_RESOURCE"
    }
}