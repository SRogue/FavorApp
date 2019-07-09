package com.kyc.favorapp.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.MainThreadDisposable

fun <T> LiveData<T>.toObservable(): Observable<T> {
    return Observable.create { emitter: ObservableEmitter<T> ->
        val observer = Observer<T> { t: T ->
            t?.let {
                emitter.onNext(t)
            }
        }
        observeForever(observer)
        emitter.setCancellable {
            object : MainThreadDisposable() {
                override fun onDispose() {
                    removeObserver(observer)
                }
            }
        }

    }.nomarlSubscrib(sbThread = RxThread.ui, obThread = RxThread.ui)
}