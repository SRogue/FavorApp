package com.kyc.favorapp.bean

import java.io.Serializable

data class HttpResultEntity<T>(
    val statusCode: Int,
    val code: Int,
    val msg: String,
    val data: T,
    val SystemCode: Int
) : Serializable {

    fun isSuccess(): Boolean = statusCode == 0 || statusCode == 100000
}