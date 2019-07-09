package com.kyc.favorapp.util

import android.util.Log
import com.kyc.favorapp.bean.BaseEntity
import io.reactivex.observers.DisposableObserver


abstract class DataCallback<T> : DisposableObserver<T>() {
    override fun onComplete() {

    }

    override fun onNext(t: T) {
        t?.let {
            Log.e("httpresult",t.toJson())
            if (t is BaseEntity<*>) {
                when (t.isSuccess()) {
                    true -> onSuccess(t)
                    else -> {
                        toast { t.msg }
                        onError(t.code, t.msg)
                    }


                }

            }
        }

    }

    override fun onError(e: Throwable) {
        toast { e.toString() }

    }

    abstract fun onSuccess(t: T)

    abstract fun onError(code: Int, msg: String)

}