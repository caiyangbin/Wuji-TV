package com.wuji.tv.utils

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class RxUtils {

    private lateinit var timerDisposable: Disposable
    private lateinit var intervalDisposable: Disposable


    fun executionTimer(time: Long, onNext: (Long) -> Unit) {
        timerDisposable = Observable.timer(time, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onNext)
    }

    fun cancelTimer() {
        if (this::timerDisposable.isInitialized && !timerDisposable.isDisposed)
            timerDisposable.dispose()
    }

    fun executionInterval(time: Long, onNext: (Long) -> Unit) {
        intervalDisposable = Observable.interval(time, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onNext)
    }

    fun cancelInterval() {
        if (this::intervalDisposable.isInitialized && !intervalDisposable.isDisposed)
            intervalDisposable.dispose()
    }
}