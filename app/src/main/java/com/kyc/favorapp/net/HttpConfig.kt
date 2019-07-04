package com.kyc.favorapp.net

object HttpConfig {

    const val NETWORKTIMEOUT: Long = 10*1000
    private var isDebug = true


    private const val debugBaseUrl = "http://jybdata.iqdii.com"

    private const val releaseBaseUrl = "http://jybdata.iqdii.com"

    fun getBaseUrl(): String = if (isDebug) debugBaseUrl else releaseBaseUrl

}