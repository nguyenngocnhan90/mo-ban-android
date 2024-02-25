package com.moban.mvp

/**
 * Created by thangnguyen on 12/16/17.
 */
interface BaseMvpPresenter<in V : BaseMvpView> {

    fun attachView(view: V)

    fun detachView()
}
