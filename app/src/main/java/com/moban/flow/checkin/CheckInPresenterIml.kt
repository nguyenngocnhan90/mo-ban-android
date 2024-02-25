package com.moban.flow.checkin

import android.content.Context
import com.moban.LHApplication
import com.moban.mvp.BaseMvpPresenter
import retrofit2.Retrofit

/**
 * Created by LenVo on 7/29/18.
 */
class CheckInPresenterIml: BaseMvpPresenter<ICheckInView>, ICheckInPresenter {
    val retrofit: Retrofit? = LHApplication.instance.getNetComponent()?.retrofit()
    private var view: ICheckInView? = null
    private var context: Context? = null

    override fun attachView(view: ICheckInView) {
        this.view = view
        this.context = view.getContext()
    }

    override fun detachView() {
        view = null
    }
}
