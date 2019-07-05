package com.kyc.favorapp.net

import com.kyc.favorapp.bean.HttpResultEntity
import com.kyc.favorapp.util.*
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object HttpUtils {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(HttpConfig.NETWORKTIMEOUT, TimeUnit.MILLISECONDS)
        //添加应用拦截器
        .addInterceptor { chain ->
            //获取request
            val request = chain.request()
            //获取request的创建者builder
            val builder = request.newBuilder()
            builder.addHeader("token", SpUtil.getString(SpConfig.TOKEN))
                .addHeader("reqSource", "ANDROID").addHeader(
                    "reqShopId",
                    SpUtil.getString(SpConfig.SHOP_ID)
                )
                .addHeader("appVersion", 17.toString())
                .addHeader("appChannel", "maill").addHeader("req-source", "ANDROID-2")
            //从request中获取headers，通过给定的键url_name
            val headerValues = request.headers("url_source")
            val oldHttpUrl = request.url()
            if (headerValues != null && headerValues.size > 0) {
                builder.removeHeader("url_source")
                //匹配获得新的BaseUrl
                val headerValue = headerValues[0]
                val methodName = request.method()
                //重建新的HttpUrl，修改需要修改的url部分
                when (headerValue) {
                    "h5" -> {
                        val newBaseUrl = HttpUrl.parse(HttpConfig.getBaseUrl())
                        builder.addHeader("Accept", "application/json")
                        builder.addHeader("Content-Type", "application/json")
                        builder.addHeader(
                            "shopId",
                            SpUtil.getString(SpConfig.SHOP_ID)
                        )
                        val newFullUrl = oldHttpUrl
                            .newBuilder()
                            .scheme(newBaseUrl!!.scheme())
                            .host(newBaseUrl.host())
                            .port(newBaseUrl.port())
                            .build()
                        //日志打印
                        chain.proceed(builder.url(newFullUrl).build())
                    }
                    "imt" -> {
                        val newBaseUrl = HttpUrl.parse(HttpConfig.getBaseUrl())
                        val baseUrl = newBaseUrl.toString().replace("/", "").trim { it <= ' ' }
                        var newFullUrl: HttpUrl? = null
                        //根据不同环境改变端口
                        if (baseUrl.contains("wwwdev")) {
                            newFullUrl = oldHttpUrl
                                .newBuilder()
                                .scheme(newBaseUrl!!.scheme())
                                .host(newBaseUrl.host())
                                .port(16501)
                                .build()
                        } else if (baseUrl.contains("wwwtest")) {
                            newFullUrl = oldHttpUrl
                                .newBuilder()
                                .scheme(newBaseUrl!!.scheme())
                                .host(newBaseUrl.host())
                                .port(17501)
                                .build()
                        }
                        if (baseUrl.endsWith("n")) {
                            newFullUrl = oldHttpUrl
                                .newBuilder()
                                .scheme(newBaseUrl!!.scheme())
                                .host("im.buylala.cn")
                                //                                                .port(18501)
                                .build()
                        }
                        //日志打印
                        chain.proceed(builder.url(newFullUrl!!).build())
                    }
                    else -> {
                        val newBaseUrl = HttpUrl.parse(HttpConfig.getBaseUrl())
                        val newFullUrl = oldHttpUrl
                            .newBuilder()
                            .scheme(newBaseUrl!!.scheme())
                            .host(newBaseUrl.host())
                            .port(newBaseUrl.port())
                            .build()
                        //日志打印
                        chain.proceed(builder.url(newFullUrl).build())
                    }
                }
            } else {
                val newBaseUrl = HttpUrl.parse(HttpConfig.getBaseUrl())
                val newFullUrl = oldHttpUrl
                    .newBuilder()
                    .scheme(newBaseUrl!!.scheme())
                    .host(newBaseUrl.host())
                    .port(newBaseUrl.port())
                    .build()
                chain.proceed(builder.url(newFullUrl).build())
            }
        }
    private val retrofit: Retrofit = Retrofit.Builder().client(okHttpClient.build())
        .baseUrl(HttpConfig.getBaseUrl())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()

    private val serviceHttpUrl = retrofit.create(ServiceHttpUrl::class.java)

    fun <T> doHttp(observable: Observable<HttpResultEntity<T>>, callBack: DataCallback<T>) {
        observable.flatMap { mapper ->
            Observable.create(ObservableOnSubscribe<T> {
                if (mapper.isSuccess()) {
                    it.onNext(mapper.data)
                } else {
                    it.onError(ResultThrowable(mapper.code, mapper.msg, null))
                }
                it.onComplete()
            })
        }.nomarlSubscrib().subscribe(callBack)
    }

    fun <T> doHttp(observable: Observable<HttpResultEntity<T>>): Observable<T>? {
        return observable.flatMap { mapper ->
            Observable.create(ObservableOnSubscribe<T> {
                if (mapper.isSuccess()) {
                    it.onNext(mapper.data)
                } else {
                    it.onError(ResultThrowable(mapper.code, mapper.msg, null))
                }
                it.onComplete()
            })
        }
    }


    fun get(): ServiceHttpUrl = serviceHttpUrl


}
