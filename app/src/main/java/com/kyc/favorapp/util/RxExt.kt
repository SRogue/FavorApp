package com.kyc.favorapp.util

import io.reactivex.Observable


/**
 * RX函数
 */


//常规操作，子线程处理数据，主线程更新ui
fun <T> Observable<T>.nomarlSubscrib(): Observable<T> {
    return this.subscribeOn(RxSchedulers.io).observeOn(RxSchedulers.ui)
}