package com.kyc.favorapp.util

import io.reactivex.Observable
import io.reactivex.Scheduler


/**
 * RX拓展函数
 */
//常规操作，子线程处理数据，主线程更新ui
fun <T> Observable<T>.nomarlSubscrib(): Observable<T> {
    return this.subscribeOn(RxThread.io).observeOn(RxThread.ui)
}

fun <T> Observable<T>.nomarlSubscrib(observeThread: Scheduler): Observable<T> {
    return this.subscribeOn(RxThread.io).observeOn(observeThread)
}