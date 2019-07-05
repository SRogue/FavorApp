package com.kyc.favorapp.util

class ResultThrowable(val code: Int,val msg: String, throwable: Throwable?) : Throwable(msg, throwable)