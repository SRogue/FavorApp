package com.kyc.favorapp.net

import com.kyc.favorapp.bean.HttpResultEntity
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ServiceHttpUrl {
    //登录
    @Headers("url_source:h5")
    @POST("/shoppingmall/anon/loginForCode")
    fun login(@Body body: RequestBody): Observable<HttpResultEntity<Any>>
}