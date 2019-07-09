package com.kyc.favorapp.util

import io.reactivex.Observable
import io.reactivex.Scheduler


/**
 * RX拓展函数
 */
//常规操作，子线程处理数据，主线程更新ui
fun <T> Observable<T>.nomarlSubscrib(
    sbThread: Scheduler = RxThread.io,
    obThread: Scheduler = RxThread.ui
): Observable<T> {
    return this.subscribeOn(sbThread).observeOn(obThread)
}