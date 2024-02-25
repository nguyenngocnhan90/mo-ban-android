package com.moban.flow.cointransfer

import android.content.Context
import com.moban.LHApplication
import com.moban.mvp.BaseMvpPresenter
import com.moban.network.service.UserService

class CoinTransferPresenterIml: BaseMvpPresenter<ICoinTransferView>, ICoinTransferPresenter {
    private val retrofit = LHApplication.instance.getNetComponent()?.retrofit()
    private val userService = retrofit?.create(UserService::class.java)

    private var view: ICoinTransferView? = null
    private var context: Context? = null

    override fun attachView(view: ICoinTransferView) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }
}
