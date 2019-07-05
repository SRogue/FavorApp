package com.kyc.favorapp.util

import io.reactivex.Observer
import io.reactivex.disposables.Disposable


interface DataCallback<T> : Observer<T> {
    override fun onComplete() {
    }

    override fun onSubscribe(d: Disposable) {
    }

    override fun onNext(t: T) {
        onSuccess(t)
    }

    override fun onError(e: Throwable) {
        if (e is ResultThrowable) {
            toast { e.msg }
            onError(e.code, e.msg)
        }
    }

    fun onSuccess(t: T)

    fun onError(code: Int, msg: String)
}