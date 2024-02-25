package com.moban.mvp

/**
 * Created by thangnguyen on 12/16/17.
 */
open class BaseMvpPresenterImpl<V : BaseMvpView> : BaseMvpPresenter<V> {

    protected var view: V? = null

    override fun attachView(view: V) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }
}
