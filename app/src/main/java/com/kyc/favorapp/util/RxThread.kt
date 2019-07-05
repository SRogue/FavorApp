package com.kyc.favorapp.util

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object RxThread {
    val io: Scheduler
        get() = Schedulers.io()

    val ui: Scheduler
        get() = AndroidSchedulers.mainThread()
}