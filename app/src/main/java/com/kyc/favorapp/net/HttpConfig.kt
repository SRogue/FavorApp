package com.kyc.favorapp.net

object HttpConfig {

    const val NETWORKTIMEOUT: Long = 10*1000
    private var isDebug = true


    private const val debugBaseUrl = "https://h5test.buylala.cn"

    private const val releaseBaseUrl = "https://h5.buylala.cn"

    fun getBaseUrl(): String = if (isDebug) debugBaseUrl else releaseBaseUrl

}