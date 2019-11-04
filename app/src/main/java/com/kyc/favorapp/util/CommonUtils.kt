package com.kyc.favorapp.util

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import com.google.gson.Gson
import com.kyc.favorapp.base.BaseApplication
import okhttp3.MediaType
import okhttp3.RequestBody


//toast方法
fun Context.toast(string: String?) {
    if (!TextUtils.isEmpty(string)) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }
}

inline fun toast(str: () -> String) {
    BaseApplication.INSTANCE.toast(str())
}

//sharepreference 方法

fun getStringSp(key: String): String = SpUtil.getString(key)
fun getBooleanSp(key: String): Boolean = SpUtil.getBoolean(key)
fun getFloatSp(key: String): Float = SpUtil.getFloat(key)
fun getIntSp(key: String): Int = SpUtil.getInt(key)
fun getLongSp(key: String): Long = SpUtil.getLong(key)
fun getFastValueSp(key: String, any: Any): Any = SpUtil.getValue(key, any)

fun saveSpValue(key: String, value: Any) {
    SpUtil.saveValue(key, value)
}

fun removeSpValue(key: String) {
    SpUtil.remove(key)
}


//http请求体的转化
fun transformRequestBody(obj: Any): RequestBody {
    val gSon = Gson()
    val json = gSon.toJson(obj)
    return RequestBody.create(MediaType.parse("application/json"), json)
}

