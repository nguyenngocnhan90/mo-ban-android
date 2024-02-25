package com.moban.flow.secondary.create.step2

import android.content.Context
import com.moban.mvp.BaseMvpPresenter

class CreateSecondaryStep2PresenterIml: BaseMvpPresenter<ICreateSecondaryStep2View>, ICreateSecondaryStep2Presenter {
    private var view: ICreateSecondaryStep2View? = null
    private var context: Context? = null

    override fun attachView(view: ICreateSecondaryStep2View) {
        this.view = view
        context = view.getContext()
    }

    override fun detachView() {
        view = null
    }
}
