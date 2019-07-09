package com.kyc.favorapp.util

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody

object GsonUtils {
    val instance: Gson = Gson()
}

fun <T> T.toJson(): String {
    return GsonUtils.instance.toJson(this)
}

inline fun <reified T> String.fromJson():T{
    return GsonUtils.instance.fromJson(this,T::class.java)
}

fun Any.toRequestBody(): RequestBody {
    return RequestBody.create(MediaType.parse("application/json"), this.toJson())
}